import sun.misc.Signal;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static java.lang.StringTemplate.STR;

public class InfiniteLeibnizSeries {
    private static class InfiniteLeibnizWorker extends Thread {
        private static final int ITERATIONS_PER_CYCLE = 10_000;
        private final byte index, signFlipper;
        private final short step;
        private final CyclicBarrier barrier;
        private long currentDenominator;
        private double partialSum = 0;

        InfiniteLeibnizWorker(byte signFlipper, byte initialDenominator, short step, CyclicBarrier barrier, byte index) {
            this.signFlipper = signFlipper;
            this.currentDenominator = initialDenominator;
            this.step = step;
            this.barrier = barrier;
            this.index = index;
        }

        @Override
        public void run() {
            int completedIterations = 0;
            double currentSign = currentDenominator > 0 ? 1 : -1;
            currentDenominator = Math.abs(currentDenominator);

            while (true) {
                for (int i = 0; i < ITERATIONS_PER_CYCLE; i += 1) {
                    partialSum += currentSign / currentDenominator;
                    currentDenominator += step;
                    currentSign *= signFlipper;
                }

                completedIterations += 1;

                try {
                    barrier.await();
                } catch (BrokenBarrierException | InterruptedException exception) {
                    System.out.println(STR."Worker #\{index} was stopped after \{completedIterations} iterations; denominator - \{currentDenominator}.");
                    break;
                }
            }
        }

        public double getPartialSum() {
            return partialSum;
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Program should be run with one and only one argument!");
            System.exit(1);
        }

        final byte threadQuantity = Byte.parseByte(args[0]);

        var barrier = new CyclicBarrier(threadQuantity);
        byte step = (byte) (threadQuantity * 2);
        byte signFlipper = (byte) ((threadQuantity % 2 == 0) ? 1 : -1);

        InfiniteLeibnizWorker[] workers = new InfiniteLeibnizWorker[threadQuantity];
        for (byte idx = 0; idx < threadQuantity; idx++) {
            byte initialDenominator = (byte) ((idx * 2 + 1) * (idx % 2 == 0 ? 1 : -1));
            workers[idx] = new InfiniteLeibnizWorker(signFlipper, initialDenominator, step, barrier, (byte) (idx + 1));
        }

        for (byte idx = 0; idx < threadQuantity; idx++) {
            workers[idx].start();
            System.out.println(STR."Worker #\{idx + 1} has been started.");
        }

        Signal.handle(new Signal("INT"), sig -> {
            System.out.println("SIGINT was caught; stopping workers...");
            double result = 0;

            for (byte idx = 0; idx < threadQuantity; idx++) {
                workers[idx].interrupt();
                System.out.println(STR."Interrupted worker #\{idx + 1}.");
            }

            for (byte idx = 0; idx < threadQuantity; idx++) {
                try {
                    workers[idx].join();
                } catch (InterruptedException exception) {
                    System.err.println(STR."Something went terribly wrong with the signal handler: \{exception.getMessage()}");
                    System.exit(1);
                }
                result += workers[idx].getPartialSum();
            }

            System.out.println(result);
        });
    }
}

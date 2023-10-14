public class LeibnizSeries {
    private static final int TOTAL_ITERATIONS = 720_720 * 16;

    private static class LeibnizWorker extends Thread {
        private final byte signFlipper;
        private final short step;
        private final int iterationsQuantity;
        private long currentDenominator;
        private double partialSum = 0;

        LeibnizWorker(byte signFlipper, byte initialDenominator, short step, int iterationsQuantity) {
            this.signFlipper = signFlipper;
            this.currentDenominator = initialDenominator;
            this.step = step;
            this.iterationsQuantity = iterationsQuantity;
        }

        @Override
        public void run() {
            double currentSign = currentDenominator > 0 ? 1 : -1;
            currentDenominator = Math.abs(currentDenominator);

            for (int i = 0; i < iterationsQuantity; i += 1) {
                partialSum += currentSign / currentDenominator;
                currentDenominator += step;
                currentSign *= signFlipper;
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

        byte threadQuantity = 0;
        try {
            threadQuantity = Byte.parseByte(args[0]);
        } catch (NumberFormatException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }

        double result = 0;
        int iterationsQuantity = TOTAL_ITERATIONS / threadQuantity;
        byte step = (byte) (threadQuantity * 2);
        byte signFlipper = (byte) ((threadQuantity % 2 == 0) ? 1 : -1);

        LeibnizWorker[] workers = new LeibnizWorker[threadQuantity];
        for (byte idx = 0; idx < threadQuantity; idx++) {
            byte initialDenominator = (byte) ((idx * 2 + 1) * (idx % 2 == 0 ? 1 : -1));
            workers[idx] = new LeibnizWorker(signFlipper, initialDenominator, step, iterationsQuantity);
            workers[idx].start();
        }

        for (int idx = 0; idx < threadQuantity; idx++) {
            try {
                workers[idx].join();
            } catch (InterruptedException exception) {
                System.err.println(STR."Something went terribly wrong with the main thread: \{exception.getMessage()}");
                System.exit(1);
            }
            result += workers[idx].getPartialSum();
        }

        System.out.println(result);
    }
}

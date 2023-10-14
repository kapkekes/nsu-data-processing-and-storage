import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;

public class ChildThreadPrintGracefulExit {
    private static class PrinterThread extends Thread {
        private static final int EIGHT_MB = 8 * 1024 * 1024;

        @Override
        public void run() {
            var stream = PrinterThread.class.getResourceAsStream("/war-and-peace.txt");
            if (stream == null) {
                System.err.println("Required resources are missing!");
                return;
            }

            try (var buffer = new BufferedReader(new InputStreamReader(stream), EIGHT_MB)) {
                buffer.mark(EIGHT_MB);
                while (!this.isInterrupted()) {
                    while (buffer.ready() && !this.isInterrupted()) {
                        System.out.println(buffer.readLine());
                    }
                    buffer.reset();
                }
            } catch (IOException e) {
                System.err.println(STR."An I/O error occurred: \{e.getMessage()}");
            }

            System.out.println("Shutdown...");
        }
    }

    public static void main(String[] args) {
        var thread = new PrinterThread();
        thread.start();

        try {
            thread.join(Duration.ofSeconds(2));
        } catch (InterruptedException e) {
            System.err.println(STR."Something went terribly wrong with the main thread: \{e.getMessage()}");
            System.exit(1);
        }

        try {
            thread.interrupt();
        } catch (SecurityException e) {
            System.err.println(STR."Access to the created thread was somehow modified: \{e.getMessage()}");
        }
    }
}

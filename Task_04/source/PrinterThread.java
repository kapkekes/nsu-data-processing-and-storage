import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrinterThread extends Thread {
    @Override
    public void run() {
        var stream = Main.class.getResourceAsStream("/war-and-peace.txt");
        if (stream == null) {
            System.err.println("Required resources are missing!");
            System.exit(1);
        }

        try (var buffer = new BufferedReader(new InputStreamReader(stream))) {
            while (!this.isInterrupted()) {
                buffer.mark(8388608);
                while (buffer.ready() && !this.isInterrupted()) {
                    System.out.println(buffer.readLine());
                }
                buffer.reset();
            }
        } catch (IOException e) {
            System.err.println(STR."An I/O error occurred: \{e.getMessage()}");
        }
    }
}

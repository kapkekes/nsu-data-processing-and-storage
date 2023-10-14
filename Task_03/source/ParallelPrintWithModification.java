import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.StringTemplate.STR;

public class ParallelPrintWithModification {
    private static class PrinterThread extends Thread {
        private final List<String> lines;
        private final String prefix;

        PrinterThread(List<String> lines, String prefix) {
            this.lines = new ArrayList<>(lines);
            this.prefix = prefix;
        }

        @Override
        public void run() {
            lines.forEach((s) -> System.out.println(STR."\{prefix}\{s}"));
        }
    }

    static ArrayList<String> abc = new ArrayList<>(List.of("A", "B", "C", "D", "E"));

    public static void main(String[] args) {
        Thread[] threads = new Thread[4];

        for (var i = 0; i < threads.length; i++) {
            System.out.println(STR."Thread \{i + 1}: \{String.join(", ", abc)}.");
            threads[i] = new PrinterThread(abc, STR."\{i + 1}) ");
            Collections.shuffle(abc);
        }

        for (var t : threads) {
            t.start();
        }
    }
}

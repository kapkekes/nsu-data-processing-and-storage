public class ParallelPrintWithWaiting {
    static String[] pythonZen = {
            "Beautiful is better than ugly.",
            "Explicit is better than implicit.",
            "Simple is better than complex.",
            "Complex is better than complicated.",
            "Flat is better than nested.",
            "Sparse is better than dense.",
            "Readability counts.",
            "Special cases aren't special enough to break the rules.",
            "Although practicality beats purity.",
            "..."
    };

    static void printZen() {
        for (var s : pythonZen) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) {
        var thread = new Thread(ParallelPrintWithWaiting::printZen);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException exception) {
            System.err.println("Something went terribly wrong with the main thread.");
            System.exit(1);
        }

        printZen();
    }
}

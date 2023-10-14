public class ParallelPrint {
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
        new Thread(ParallelPrint::printZen).start();
        printZen();
    }
}

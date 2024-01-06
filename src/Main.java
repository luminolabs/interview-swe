public class Main {
    public static void main(String[] args) {

        FileInterface fileInterface = new FileInterface();
        System.out.println("Reading input transaction file");
        fileInterface.performBankActions("/Users/yogesh/Projects/lumino-interview/src/input-1.csv");
        fileInterface.writeBankStatements("output-1.csv");
        System.out.println("Final balances stored in output file");
    }
}
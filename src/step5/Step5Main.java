package step5;

public class Step5Main {
    public static void main(String[] args) {

        FileInterface fileInterface = new FileInterface();
        System.out.println("Reading input-5 transaction file");
        fileInterface.performBankActions("/Users/yogesh/Projects/lumino-interview/src/step5/input-5.csv");
        fileInterface.writeBankStatements("/Users/yogesh/Projects/lumino-interview/src/step5/output-5.csv");
        System.out.println("Final balances stored in output-5 file");
    }
}
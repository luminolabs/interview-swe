package step2;

public class Step2Main {
    public static void main(String[] args) {

        FileInterface fileInterface = new FileInterface();
        System.out.println("Reading input-2 transaction file");
        fileInterface.performBankActions("/Users/yogesh/Projects/lumino-interview/src/step2/input-2.csv");
        fileInterface.writeBankStatements("/Users/yogesh/Projects/lumino-interview/src/step2/output-2.csv");
        System.out.println("Final balances stored in output-2 file");
    }
}
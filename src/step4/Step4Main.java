package step4;

public class Step4Main {
    public static void main(String[] args) {

        FileInterface fileInterface = new FileInterface();
        System.out.println("Reading input-4 transaction file");
        fileInterface.performBankActions("/Users/yogesh/Projects/lumino-interview/src/step4/input-4.csv");
        fileInterface.writeBankStatements("/Users/yogesh/Projects/lumino-interview/src/step4/output-4.csv");
        System.out.println("Final balances stored in output-4 file");
    }
}
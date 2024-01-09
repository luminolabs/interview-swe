package step3;

public class Step3Main {
    public static void main(String[] args) {

        FileInterface fileInterface = new FileInterface();
        System.out.println("Reading input-3 transaction file");
        fileInterface.performBankActions("/Users/yogesh/Projects/lumino-interview/src/step3/input-3.csv");
        fileInterface.writeBankStatements("/Users/yogesh/Projects/lumino-interview/src/step3/output-3.csv");
        System.out.println("Final balances stored in output-3 file");
    }
}
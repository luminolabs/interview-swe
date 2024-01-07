package step1;

public class Step1Main {
    public static void main(String[] args) {

        FileInterface fileInterface = new FileInterface();
        System.out.println("Reading input transaction file");
        fileInterface.performBankActions("/Users/yogesh/Projects/lumino-interview/src/step1/input-1.csv");
        fileInterface.writeBankStatements("/Users/yogesh/Projects/lumino-interview/src/step1/output-1.csv");
        System.out.println("Final balances stored in output file");
    }
}
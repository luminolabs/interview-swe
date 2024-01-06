import java.io.*;
import java.util.Map;

public class FileInterface {
    LuminoBankService bankService;

    public FileInterface() {
        bankService = new LuminoBankService();
    }
    public void performBankActions(String inputFile) {
        try(BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            // Read and discard the header line
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length < 4) {
                    continue; //skip invalid lines
                }
                String account = tokens[1].trim();
                String action = tokens[2].trim();
                int amount = tokens[3].isEmpty() ? 0 : Integer.parseInt(tokens[3].trim().substring(1));

                switch (action.toLowerCase()) {
                    case "create account":
                        // handle account creation, if necessary
                        break;
                    case "deposit":
                        bankService.depositCash(account, amount);
                        break;
                    case "withdraw":
                        bankService.withdrawCash(account, amount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeBankStatements(String outputFile) {
        // calls LuminoBankService functions
        //writes to output file
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Integer> entry : bankService.getAccountBalances().entrySet()) {
                writer.write(entry.getKey() + ", $" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

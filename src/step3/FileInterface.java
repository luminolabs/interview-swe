package step3;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FileInterface {
    LuminoBankService bankService;

    public FileInterface() {
        bankService = new LuminoBankService();
    }

    public void performBankActions(String inputFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line = reader.readLine(); // Read and discard the header line
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length < 5) {
                    continue; // Skip invalid lines
                }
                String time = tokens[0].trim();
                String account = tokens[1].trim();
                String creditString = tokens[2].trim();
                String debitString = tokens[3].trim();
                //String status = tokens[4].trim();
                String type = tokens[4].trim();

                int amount = 0;
                String action = "";
                if (!creditString.isEmpty()) {
                    amount = parseAmount(creditString);
                    action = "deposit";
                } else if (!debitString.isEmpty()) {
                    amount = parseAmount(debitString);
                    action = "withdraw";
                }

                System.out.println(time + " " + account + " " + action + " " + amount);
                switch (action.toLowerCase()) {
                    case "deposit":
                        bankService.depositCash(time, account, amount, "SUCCESS", type);
                        break;
                    case "withdraw":
                        bankService.withdrawCash(time, account, amount, type);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int parseAmount(String amountString) {
        amountString = amountString.replace("$", "").replace(",", "");
        try {
            float amountFloat = Float.parseFloat(amountString); // Convert to float
            return (int) (amountFloat); // Convert dollars to cents
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format: " + amountString);
            return 0;
        }
    }

    public void writeBankStatements(String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("time,account,credit,debit,status,type,balance");
            writer.newLine();

            // Sort accounts and then sort transactions within each account
            bankService.getAccountTransactions().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()) // Sort by account name
                    .forEach(entry -> {
                        String account = entry.getKey();
                        List<LuminoBankService.Transaction> sortedTransactions = entry.getValue();
                        // Sort transactions by time within each account
                        sortedTransactions.sort(Comparator.comparing(t -> t.time));

                        for (LuminoBankService.Transaction t : sortedTransactions) {
                            try {
                                writer.write(String.format("%s,%s,$%d,$%d,%s,%s,$%d",
                                        t.time, t.account, t.credit, t.debit, t.status, t.type, t.balance));
                                System.out.println("printing writing to file" + String.format("%s,%s,$%d,$%d,%s,%s,$%d",
                                        t.time, t.account, t.credit, t.debit, t.status, t.type, t.balance));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

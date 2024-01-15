package step5;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FileInterface {
    LuminoBankService bankService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");

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
                String type = tokens[4].trim();

                System.out.println("time:" + time + " " + account + " " + creditString + " " + debitString + " " + type );
                int amount = 0;
                if (!creditString.isEmpty()) {
                    amount = parseAmount(creditString);
                    if (type.equals("SLOW TRANSFER")) {
                        bankService.slowTransfer(time, account, amount);
                    } else {
                        bankService.depositCash(time, account, amount, "SUCCESS", type);
                    }
                } else if (!debitString.isEmpty()) {
                    amount = parseAmount(debitString);
                    if (canWithdraw(time, account, amount)) {
                        bankService.withdrawCash(time, account, amount, type);
                    } else {
                        // Record failure due to pending slow transfer or insufficient funds
                        bankService.recordFailedTransaction(time, account, amount, type);
                    }
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
            return (int) (amountFloat * 100); // Convert dollars to cents
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format: " + amountString);
            return 0;
        }
    }

    private boolean canWithdraw(String time, String account, int amount) {
        LocalDateTime transactionTime = LocalDateTime.parse(time, formatter);
        return bankService.canWithdraw(account, transactionTime, amount);
    }

    public void writeBankStatements(String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("time,account,credit,debit,status,type,balance");
            writer.newLine();

            bankService.getAccountTransactions().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()) // Sort by account name
                    .forEach(entry -> {
                        String account = entry.getKey();
                        List<LuminoBankService.Transaction> sortedTransactions = entry.getValue();
                        // Sort transactions by time within each account
                        sortedTransactions.sort(Comparator.comparing(t -> t.time));

                        for (LuminoBankService.Transaction t : sortedTransactions) {
                            try {
                                writer.write(String.format("%s,%s,$%.2f,$%.2f,%s,%s,$%.2f",
                                        t.time, t.account, t.credit / 100.0, t.debit / 100.0, t.status, t.type, t.balance / 100.0));
                                writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException("Error writing to file: " + e.getMessage(), e);
                            }
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error opening or creating the file: " + e.getMessage());
        }
    }
}

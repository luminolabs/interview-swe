package step5;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuminoBankService {
    static class Transaction {
        String time;
        String account;
        int credit;
        double debit;
        String status;
        String type;
        double balance;

        public Transaction(String time, String account, int credit, double debit, String status, String type, double balance) {
            this.time = time;
            this.account = account;
            this.credit = credit;
            this.debit = debit;
            this.status = status;
            this.type = type;
            this.balance = balance;
        }
    }

    Map<String, Double> accountBalances = new HashMap<>();
    Map<String, List<LuminoBankService.Transaction>> accountTransactions = new HashMap<>();

    private Map<String, LocalDateTime> pendingSlowTransfers = new HashMap<>();

    public void slowTransfer(String time, String account, int amount) {
        // Define the formatter to match your date-time string format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");

        // Parse the time string using the formatter
        LocalDateTime transferTime = LocalDateTime.parse(time, formatter);

        pendingSlowTransfers.put(account, transferTime.plusHours(1)); // Transfer completes after 1 hour

        // Assume the transfer amount is immediately deducted from the account balance
        withdrawCash(time, account, amount, "SLOW TRANSFER");
    }

    public boolean isTransferCompleted(String account, String currentTime) {
        LocalDateTime now = LocalDateTime.parse(currentTime);
        LocalDateTime transferCompletionTime = pendingSlowTransfers.getOrDefault(account, now);
        return now.isAfter(transferCompletionTime);
    }

    public boolean canWithdraw(String account, LocalDateTime transactionTime, int amount) {
        // Check if there's enough balance in the account
        double currentBalance = accountBalances.getOrDefault(account, 0.0);
        if (currentBalance < amount) {
            return false; // Not enough balance
        }

        // Check for pending slow transfers
        LocalDateTime slowTransferCompletionTime = pendingSlowTransfers.get(account);
        if (slowTransferCompletionTime != null && transactionTime.isBefore(slowTransferCompletionTime)) {
            return false; // Slow transfer is still pending
        }

        return true; // Withdrawal can be processed
    }

    public double getCurrentBalance(String accountId) {
        return accountBalances.getOrDefault(accountId, 0.0);
    }

    public void depositCash(String time, String account, int amount, String status, String type) {
        double currentBalance = getCurrentBalance(account);
        currentBalance += amount;
        accountBalances.put(account, currentBalance);

        LuminoBankService.Transaction transaction = new LuminoBankService.Transaction(time, account, amount, 0, status, type, currentBalance);
        accountTransactions.computeIfAbsent(account, k -> new ArrayList<>()).add(transaction);
    }

    public boolean withdrawCash(String time, String account, int amount, String type) {

        if (pendingSlowTransfers.containsKey(account)) {
            LocalDateTime completionTime = pendingSlowTransfers.get(account);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");
            LocalDateTime currentTime = LocalDateTime.parse(time, formatter);
            if (currentTime.isBefore(completionTime)) {
                return false; // Withdrawal fails if within slow transfer window
            }
        }

        double currentBalance = getCurrentBalance(account);
        String status;
        if(currentBalance >= amount) {
            currentBalance -= amount;
            accountBalances.put(account, currentBalance);
            status = "SUCCESS";
        } else {
            status = "FAILED";
        }
        LuminoBankService.Transaction transaction = new LuminoBankService.Transaction(time, account, 0, amount, status, type, currentBalance);
        accountTransactions.computeIfAbsent(account, k -> new ArrayList<>()).add(transaction);
        return status.equals("SUCCESS");
    }

    public boolean processExternalTransfer(String time, String luminoAccount, int amount, String type) {
        boolean isSuccess = withdrawCash(time, luminoAccount, amount, type);
        if (isSuccess) {
            chargeFee(time, luminoAccount, 0.01); // Charge $0.01 fee
        }
        return isSuccess;
    }

    private void chargeFee(String time, String account, double feeAmount) {
        double currentBalance = getCurrentBalance(account);
        currentBalance -= feeAmount; // Deduct fee
        accountBalances.put(account, currentBalance);

        // Record the fee transaction
        LuminoBankService.Transaction feeTransaction = new LuminoBankService.Transaction(time, account, 0, feeAmount, "SUCCESS", "FEE", currentBalance);
        accountTransactions.computeIfAbsent(account, k -> new ArrayList<>()).add(feeTransaction);
    }

    public void recordFailedTransaction(String time, String account, int amount, String type) {
        // Assuming 'Transaction' is a static class within LuminoBankService
        // that stores transaction details
        Transaction failedTransaction = new Transaction(time, account, 0, amount, "FAILURE", type, getCurrentBalance(account));

        // Add the failed transaction to the account's transaction list
        List<Transaction> transactions = accountTransactions.computeIfAbsent(account, k -> new ArrayList<>());
        transactions.add(failedTransaction);
    }

    public Map<String, List<LuminoBankService.Transaction>> getAccountTransactions() {
        return accountTransactions;
    }
}

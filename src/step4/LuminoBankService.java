package step4;

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
    Map<String, List<Transaction>> accountTransactions = new HashMap<>();

    public double getCurrentBalance(String accountId) {
        return accountBalances.getOrDefault(accountId, 0.0);
    }

    public void depositCash(String time, String account, int amount, String status, String type) {
        double currentBalance = getCurrentBalance(account);
        currentBalance += amount;
        accountBalances.put(account, currentBalance);

        Transaction transaction = new Transaction(time, account, amount, 0, status, type, currentBalance);
        accountTransactions.computeIfAbsent(account, k -> new ArrayList<>()).add(transaction);
    }

    public boolean withdrawCash(String time, String account, int amount, String type) {
        double currentBalance = getCurrentBalance(account);
        String status;
        if(currentBalance >= amount) {
            currentBalance -= amount;
            accountBalances.put(account, currentBalance);
            status = "SUCCESS";
        } else {
            status = "FAILED";
        }

        Transaction transaction = new Transaction(time, account, 0, amount, status, type, currentBalance);
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
        Transaction feeTransaction = new Transaction(time, account, 0, feeAmount, "SUCCESS", "FEE", currentBalance);
        accountTransactions.computeIfAbsent(account, k -> new ArrayList<>()).add(feeTransaction);
    }

    public Map<String, List<Transaction>> getAccountTransactions() {
        return accountTransactions;
    }
}

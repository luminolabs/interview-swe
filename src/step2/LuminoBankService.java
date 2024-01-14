package step2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class LuminoBankService {
    static class Transaction {
        String time;
        String account;
        int credit;
        int debit;
        String status;
        String type;
        int balance;

        public Transaction(String time, String account, int credit, int debit, String status, String type, int balance) {
            this.time = time;
            this.account = account;
            this.credit = credit;
            this.debit = debit;
            this.status = status;
            this.type = type;
            this.balance = balance;
        }
    }

    Map<String, Integer> accountBalances = new HashMap<>();
    Map<String, List<Transaction>> accountTransactions = new HashMap<>();

    public int getCurrentBalance(String accountId) {
        return accountBalances.getOrDefault(accountId, 0);
    }

    public void depositCash(String time, String account, int amount, String type) {
        int currentBalance = getCurrentBalance(account);
        currentBalance += amount;
        accountBalances.put(account, currentBalance);

        Transaction transaction = new Transaction(time, account, amount, 0, "SUCCESS", type, currentBalance);
        accountTransactions.computeIfAbsent(account, k -> new ArrayList<>()).add(transaction);
    }

    public boolean withdrawCash(String time, String account, int amount) {
        int currentBalance = getCurrentBalance(account);
        String status;
        if(currentBalance >= amount) {
            currentBalance -= amount;
            accountBalances.put(account, currentBalance);
            status = "SUCCESS";
        } else {
            status = "FAILED";
        }

        Transaction transaction = new Transaction(time, account, 0, amount, status, "ATM", currentBalance);
        accountTransactions.computeIfAbsent(account, k -> new ArrayList<>()).add(transaction);
        return status.equals("SUCCESS");
    }

    public Map<String, List<Transaction      >> getAccountTransactions() {
        return accountTransactions;
    }
}

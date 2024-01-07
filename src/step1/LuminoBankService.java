package step1;

import java.util.HashMap;
import java.util.Map;

public class LuminoBankService {
    Map<String, Integer> accountBalances = new HashMap<>();
    public int getCurrentBalance(String accountId) {
        return accountBalances.get(accountId);
    }

    public void depositCash(String account, int amount) {
        if(!accountBalances.containsKey(account)) {
            accountBalances.put(account, 0);
        }

        int currentBalance = getCurrentBalance(account);
        accountBalances.put(account, currentBalance + amount);
    }

    public boolean withdrawCash(String account, int amount) {
        int currentBalance = getCurrentBalance(account);
        if(currentBalance >= amount) {
            accountBalances.put(account, currentBalance - amount);
            return true;
        }
        return false;
    }

    public Map<String, Integer> getAccountBalances() {
        return accountBalances;
    }
}

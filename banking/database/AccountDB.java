package banking.database;

import banking.account.Account;

import java.util.Arrays;
import java.util.Objects;
import java.sql.*;
import org.sqlite.*;

public class AccountDB {
    private static int load = 16;

    public static int getNumberOfObjInArr() {
        return numberOfObjInArr;
    }

    private static int numberOfObjInArr = 0;
    public static Account[] allAccounts = new Account[load];
    private static final double LOAD_CAPACITY = 0.75;

    public static void addAccount(Account account) {
        //TODO: SOLVE PROBLEM INT_MAX_SIZE / 2
        if (numberOfObjInArr >= load * LOAD_CAPACITY) {
            load = load * 2;
            allAccounts = Arrays.copyOf(allAccounts, load);
        }
        allAccounts[numberOfObjInArr] = account;
        numberOfObjInArr++;
    }

    public static Account getAccount(String cardNumber) {
        for (int i = 0; i < numberOfObjInArr; i++) {
            if (Objects.nonNull(allAccounts[i]) && cardNumber.equals(allAccounts[i].getCardNumber())) {
                return allAccounts[i];
            }
        }
        return null;
    }
}

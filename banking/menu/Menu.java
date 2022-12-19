package banking.menu;

import banking.account.Account;
import banking.database.AccountDB;
import java.util.Objects;
import java.util.Scanner;

public class Menu {
    private void showStartMenu() {
        System.out.println("""
                1. Create an account
                2. Log into account
                0. Exit""");
    }

    private void showAccountMenu() {
        System.out.println("""
                1. Balance
                2. Add income
                3. Do transfer
                4. Close account
                5. Log out
                0. Exit""");
    }

    public void startMenu() {
        boolean isWorking = true;
        Scanner scanner = new Scanner(System.in);
        while (isWorking) {

            showStartMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    createAccount();
                    break;
                case "2":
                    isWorking = logIntoAccount();
                    if (!isWorking) {
                        System.out.println("\nBye!");
                    }
                    break;
                case "0":
                    isWorking = false;
                    System.out.println("\nBye!");
                    break;
                default:
                    System.out.println("Your input number is incorrect. Input 1, 2 or 0\n");
            }
        }
    }

    private void createAccount(){
        Account account = new Account.AccountBuilder().build();
        AccountDB.addAccount(account);
        System.out.println("\n"+"Your card has been created\n" +
                "Your card number:\n" +
                account.getCardNumber() + "\n" +
                "Your card PIN:\n" +
                account.getPin() + "\n");
    }

    private boolean logIntoAccount() {
        Scanner scanner = new Scanner(System.in);
        String cardNumber;
        String pin;

        System.out.println("\nEnter your card number:");
        cardNumber = scanner.nextLine().trim();
        System.out.println("Enter your pin:");
        pin = scanner.nextLine().trim();

        Account account = findAccount(cardNumber, pin);

        if (Objects.nonNull(account)) {
            System.out.println("\nYou have successfully logged in!\n");
            return accountMenu(account);
        } else {
            System.out.println("\nWrong card number or PIN!\n");
            return true;
        }
    }

    private Account findAccount(String cardNumber, String pin) {
        Account acc = AccountDB.getAccount(cardNumber);
        if (Objects.nonNull(acc)) {
            if (acc.getCardNumber().equals(cardNumber) && acc.getPin().equals(pin)) {
                return acc;
            }
        }
        return null;
    }

    private Account findAccount(String cardNumber) {
        Account acc = AccountDB.getAccount(cardNumber);
        if (Objects.nonNull(acc)) {
            if (acc.getCardNumber().equals(cardNumber)) {
                return acc;
            }
        }
        return null;
    }

    private boolean accountMenu(Account account) {
        boolean isWorking = true;
        Scanner scanner = new Scanner(System.in);
        while (isWorking) {

            showAccountMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    System.out.println("\nBalance: " + AccountDB.retrieveBalance(account.getCardNumber()) + "\n");
                    break;
                case "2":
                    addIncome(account.getCardNumber());
                    break;
                case "3":
                    transferTheMoney(account);
                    break;
                case "4":
                    isWorking = deleteTheAccount(account);
                    break;
                case "5":
                    isWorking = false;
                    System.out.println("\nYou have successfully logged out!\n");
                    break;
                case "0":
                    return false;
                default:
                    System.out.println("Your input number is incorrect. Input 1, 2 or 0\n");
            }
        }
        return true;
    }

    private void addIncome(String cardNumber) {
        Scanner scanner = new Scanner(System.in);
        long income;
        System.out.println("\nEnter income:");
        income = scanner.nextLong();
        AccountDB.updateBalance(income, cardNumber);
        System.out.println("Income was added!\n");
    }

    static boolean checkLuhn(String cardNumber)
    {
        int nDigits = cardNumber.length();
        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--)
        {
            int d = cardNumber.charAt(i) - '0';
            if (isSecond) {
                d = d * 2;
            }
            nSum += d / 10;
            nSum += d % 10;
            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }

    private void transferTheMoney(Account account) {
        Scanner scanner = new Scanner(System.in);
        String cardToTransfer;
        System.out.println("\nTransfer");
        System.out.println("Enter card number:");
        cardToTransfer = scanner.nextLine();
        if (account.getCardNumber().equals(cardToTransfer)) {
            System.out.println("You can't transfer money to the same account!\n");
            return;
        }
        if (!checkLuhn(cardToTransfer)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!\n");
            return;
        }
        Account accountToTransfer = findAccount(cardToTransfer);
        if (Objects.isNull(accountToTransfer)) {
            System.out.println("Such a card does not exist.\n");
            return;
        }
        System.out.println("Enter how much money you want to transfer:");
        long moneyToTransfer = scanner.nextLong();
        if (AccountDB.retrieveBalance(account.getCardNumber()) < moneyToTransfer) {
            System.out.println("Not enough money!\n");
            return;
        }
        try {
            AccountDB.transferMoney(account.getCardNumber(), accountToTransfer.getCardNumber(), moneyToTransfer);
            System.out.println("Success!\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean deleteTheAccount(Account account) {
        try {
            AccountDB.deleteAccount(account.getCardNumber());
            System.out.println("\nThe account has been closed!\n");
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return true;
        }
    }
}

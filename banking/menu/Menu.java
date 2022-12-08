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
                2. Log out
                0. Exit""");
    }

    public void startMenu() {
        boolean isWorking = true;
        Scanner scanner = new Scanner(System.in);
//        for (int i = 0; i < 9; i++) {
//            createAccount();
//        }
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
        //TODO: удалить перед проверкой
//        for (Account acc:AccountDB.allAccounts) {
//            if (Objects.nonNull(acc)) {
//                System.out.println(acc);
//            }
//        }
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

        for (Account acc:AccountDB.allAccounts) {
            if (Objects.nonNull(acc)) {
                if (acc.getCardNumber().equals(cardNumber) && acc.getPin().equals(pin)) {
                    return acc;
                }
            }
        }
        return null;
    }

    private boolean accountMenu(Account account) {
        boolean isWorking = true;
        Scanner scanner = new Scanner(System.in);
//        for (int i = 0; i < 9; i++) {
//            createAccount();
//        }
        while (isWorking) {

            showAccountMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    System.out.println("\nBalance: " + account.getBalance() + "\n");
                    break;
                case "2":
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
}

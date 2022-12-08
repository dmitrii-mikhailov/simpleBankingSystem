package banking.account;

import banking.database.AccountDB;

import java.util.Objects;
import java.util.Random;

public class Account {
    private final String cardNumber;
    private final String pin;
    private final long balance;

    private Account(String cardNumber, String pin, long balance) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public long getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "cardNumber='" + cardNumber + '\'' +
                ", pin='" + pin + '\'' +
                ", balance=" + balance +
                '}';
    }

    public static class AccountBuilder {
        private final int BOUND = 1000000000;
        private String cardNumber;
        private String pin;
        private long balance;

        private void generateCardNumber() {

            String tempCardNumber = generateRandomNumberCard();
            //TODO: SOLVE INFINITE CYCLE IF MAXIMUM NUMBER OF CARDS REACHED
            if(AccountDB.getNumberOfObjInArr() < BOUND) {
                while (Objects.nonNull(AccountDB.getAccount(tempCardNumber))) {
                    tempCardNumber = generateRandomNumberCard();
                }
            } else {
                throw new RuntimeException("MAXIMUM NUMBER OF ACCOUNTS REACHED");
            }
            this.cardNumber = tempCardNumber;
        }

        private String generateBin() {
            return "400000";
        }

        private String generateCheckSum(String fifteenDigitNumber) {
            char[] chars = fifteenDigitNumber.toCharArray();
            int sum = 0;
            int checkSum;

            for (int i = 0; i < chars.length; i++) {
                int step = i + 1;
                int currentNumber = Character.getNumericValue(chars[i]);
                if (step % 2 != 0) {
                    currentNumber = currentNumber * 2;
                }
                if (currentNumber > 9) {
                    currentNumber = currentNumber - 9;
                }
                sum = sum + currentNumber;
            }
            checkSum = 10 - (sum % 10);
            if (checkSum == 10) {
                checkSum = 0;
            }
            return Integer.toString(checkSum);
        }
        private String generateRandomNumberCard() {
            Random randomAccountNumber = new Random();
            int randomNumber;
            String bin = generateBin();
            randomNumber = randomAccountNumber.nextInt(BOUND);
            String checkSum = generateCheckSum(bin + String.format("%09d", randomNumber));
            return bin + String.format("%09d", randomNumber) + checkSum;
        }

        private void generatePin() {
            this.pin = String.format("%04d",new Random().nextInt(10000));
        }

        private void generateBalance() {
            this.balance = 0;
        }

        public Account build() {
            generateCardNumber();
            generatePin();
            generateBalance();
            return new Account(this.cardNumber, this.pin, this.balance);
        }
    }
}
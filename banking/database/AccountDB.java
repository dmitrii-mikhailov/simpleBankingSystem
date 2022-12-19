package banking.database;

import banking.Main;
import banking.account.Account;

import java.sql.*;
import org.sqlite.*;

public class AccountDB {

    public static int getNumberOfObjInArr() {
        SQLiteDataSource dataSource = createDataSource();
        int numberOfCards = -1;
        String selectCount = "SELECT COUNT (id) AS total FROM CARD";
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(selectCount)) {
                try (ResultSet cards = statement.executeQuery()) {
                    numberOfCards = cards.getInt("total");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (numberOfCards == -1) {
            throw new RuntimeException("Error in counting rows from table CARD");
        } else {
            return numberOfCards;
        }
    }

    public static void addAccount(Account account) {
        SQLiteDataSource dataSource = createDataSource();
        String selectAccountSQL = "SELECT * FROM CARD ORDER BY id DESC LIMIT 1";
        String insertAccountSQL = "INSERT INTO CARD VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement selectAccount = con.prepareStatement(selectAccountSQL);
                 PreparedStatement insertAccount = con.prepareStatement(insertAccountSQL)) {
                int id;
                try (ResultSet cards = selectAccount.executeQuery()) {
                    boolean rowsExists = cards.isBeforeFirst();
                    id = rowsExists ? cards.getInt("id") + 1 : 1;
                }
                insertAccount.setInt(1, id);
                insertAccount.setString(2, account.getCardNumber());
                insertAccount.setString(3, account.getPin());
                insertAccount.setInt(4, (int) account.getBalance());
                insertAccount.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Account getAccount(String cardNumber) {
        SQLiteDataSource dataSource = createDataSource();
        String selectAccount = "SELECT * FROM CARD WHERE number = ?";

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(selectAccount)) {
                statement.setString(1, cardNumber);
                try (ResultSet cards = statement.executeQuery()) {
                    boolean rowsExists = cards.isBeforeFirst();
                    if (rowsExists) {
                        return new Account(cards.getString("number"), cards.getString("pin"), cards.getInt("balance"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SQLiteDataSource createDataSource() {
        String url = "jdbc:sqlite:" + Main.getArgs()[1];
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        return dataSource;
    }

    public static void createDataBase() {
        SQLiteDataSource dataSource = createDataSource();
        String createDB = "CREATE TABLE IF NOT EXISTS card(" +
                "id INTEGER," +
                "number TEXT," +
                "pin TEXT," +
                "balance INTEGER DEFAULT 0)";
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(createDB)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateBalance(long income, String cardNumber) {
        SQLiteDataSource dataSource = createDataSource();
        String updateAccount = "UPDATE card " +
                "SET balance = balance + ? " +
                "WHERE number = ?";

        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement statement = con.prepareStatement(updateAccount)) {
                statement.setLong(1, income);
                statement.setString(2, cardNumber);
                statement.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static long retrieveBalance(String cardNumber) {
        SQLiteDataSource dataSource = createDataSource();
        String selectAccount = "SELECT * FROM CARD WHERE number = ?";

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement statement = con.prepareStatement(selectAccount)) {
                statement.setString(1, cardNumber);
                try (ResultSet cards = statement.executeQuery()) {
                    boolean rowsExists = cards.isBeforeFirst();
                    if (rowsExists) {
                        return cards.getLong("balance");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Error occurred while retrieving balance!");
    }

    public static void transferMoney(String cardNumberFrom, String cardNumberTo, long moneyToTransfer) {
        SQLiteDataSource dataSource = createDataSource();
        String subtractMoneySQL = "UPDATE card " +
                "SET balance = balance - ? " +
                "WHERE number = ?";
        String addMoneySQL = "UPDATE card " +
                "SET balance = balance + ? " +
                "WHERE number = ?";

        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement subtractMoney = con.prepareStatement(subtractMoneySQL);
                 PreparedStatement addMoney = con.prepareStatement(addMoneySQL)) {
                subtractMoney.setLong(1, moneyToTransfer);
                subtractMoney.setString(2, cardNumberFrom);
                subtractMoney.executeUpdate();

                addMoney.setLong(1, moneyToTransfer);
                addMoney.setString(2, cardNumberTo);
                addMoney.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAccount(String cardNumber) {
        SQLiteDataSource dataSource = createDataSource();
        String deleteAccount = "DELETE from card " +
                "WHERE number = ?";

        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement statement = con.prepareStatement(deleteAccount)) {
                statement.setString(1, cardNumber);
                statement.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

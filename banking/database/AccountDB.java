package banking.database;

import banking.Main;
import banking.account.Account;

import java.sql.*;
import org.sqlite.*;

public class AccountDB {

    public static int getNumberOfObjInArr() {
        SQLiteDataSource dataSource = createDataSource();
        int numberOfCards = -1;

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet cards = statement.executeQuery("SELECT COUNT (id) AS total FROM CARD")) {
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

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                int id;

                try (ResultSet cards = statement.executeQuery("SELECT * FROM CARD ORDER BY id DESC LIMIT 1")) {
                    boolean rowsExists = cards.isBeforeFirst();
                    id = rowsExists ? cards.getInt("id") + 1 : 1;
                }

                int i = statement.executeUpdate("INSERT INTO CARD VALUES " +
                        "("+id+", '"+account.getCardNumber()+"', '"+account.getPin()+"', "+(int) account.getBalance()+")");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Account getAccount(String cardNumber) {
        SQLiteDataSource dataSource = createDataSource();

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet cards = statement.executeQuery("SELECT * FROM CARD " +
                                                                 "WHERE number = '" + cardNumber + "'")) {
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
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

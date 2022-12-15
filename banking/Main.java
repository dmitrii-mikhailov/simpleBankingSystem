package banking;

import banking.database.AccountDB;
import banking.menu.Menu;

public class Main {
    private static String[] savedArgs;
    public static String[] getArgs() {
        return savedArgs;
    }
    public static void main(String[] args) {
        savedArgs = args;
        AccountDB.createDataBase();
        Menu menu = new Menu();
        menu.startMenu();
    }
}
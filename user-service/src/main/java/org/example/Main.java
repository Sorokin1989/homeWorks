package org.example;

import org.example.console.ConsoleMenu;
import org.example.dao.UserDao;
import org.example.util.HibernateUtil;

public class Main {
    public static void main(String[] args) {


        try {
            UserDao userDao = new UserDao();
            ConsoleMenu consoleMenu = new ConsoleMenu(userDao);
            consoleMenu.mainMenu();

        } finally {
            HibernateUtil.shutdown();
        }


    }
}
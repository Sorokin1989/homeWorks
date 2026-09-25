package org.example.console;

import org.example.dao.UserDao;
import org.example.entity.User;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public ConsoleMenu(UserDao userDao) {
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
    }

    public void mainMenu() {
        while (true) {
            printMenu();

            int option = readInteger("Введите номер: ");
            switch (option) {
                case 1:
                    createUser();
                    break;
                case 2:
                    findUser();
                    break;
                case 3:
                    findAllUsers();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    deleteUser();
                    break;
                case 0:
                    System.out.println("Выход....");
                    return;
                default:
                    System.out.println("Некорректное значение!");


            }


        }


    }

    private void printMenu() {
        System.out.println(
                "=== User Service ===\n" +
                        "1. Создать пользователя\n" +
                        "2. Найти по ID\n" +
                        "3. Показать всех\n" +
                        "4. Обновить\n" +
                        "5. Удалить\n" +
                        "0. Выход"
        );
    }

    private void createUser() {
        String username = readString("Введите имя: ");
        String email = readString("Введите email: ");
        Integer age = readInteger("Введите возраст: ");

        User newUser = new User(username, email, age);

        try {
            userDao.save(newUser);
            System.out.println("Создан " + newUser);

        } catch (Exception e) {
            System.out.println("Ошибка " + e.getMessage());
        }
    }

    private void findUser() {

        Integer id = readInteger("Id: ");

        User user = userDao.findById(Long.valueOf(id));
        if (user != null) {
            System.out.println("Найден: " + user);
        } else {
            System.out.println("Пользователь с id " + id + " не найден!");
        }
    }

    private void findAllUsers() {
        List<User> users = userDao.findAll();

        if (users.isEmpty()) {
            System.out.println("Список пуст! ");
        } else {
            for (User user : users) {
                System.out.println("Список пользователей: " + user);
            }
        }
    }

    private void updateUser() {
        Integer id = readInteger("Id: ");
        User user = userDao.findById(Long.valueOf(id));
        if (user == null) {
            System.out.println("Пользователь с id " + id + "не найден!");
            return;
        }
            String username = readString("Введите новое имя: ");
            String email = readString("Введите новый email: ");
            Integer age = readInteger("Введите новый возраст: ");

            user.setName(username);
            user.setEmail(email);
            user.setAge(age);

            try {
                userDao.update(user);
                System.out.println("Обновлен " + user);

            } catch (Exception e) {
                System.out.println("Ошибка обновления " + e.getMessage());


        }
    }

    private void deleteUser() {
        int id = readInteger("Id: ");

        User user = userDao.findById((long) id);
        if (user == null) {
            System.out.println("Пользователя с id " + id + "нет!");
            return;
        }

        try {
            userDao.deleteById((long) id);
            System.out.println("Пользователь с id " + id + " удален");
        } catch (Exception e) {
            System.out.println("Ошибка " + e.getMessage());
        }

    }


    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private Integer readInteger(String prompt) {

        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Некорректное значение! " + e.getMessage());

            }
        }
    }
}

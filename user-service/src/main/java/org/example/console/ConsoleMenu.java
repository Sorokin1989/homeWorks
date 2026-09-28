package org.example.console;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.UserDao;
import org.example.dto.UserDto;
import org.example.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Slf4j
public class ConsoleMenu {

    private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = FACTORY.getValidator();

    private final UserDao userDao;
    private final Scanner scanner;

    public ConsoleMenu(UserDao userDao) {
       this(userDao,new Scanner(System.in));
    }

    public ConsoleMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void mainMenu() {
        while (true) {
            printMenu();

            Integer option = readInteger("Введите номер: ");

            if (option==null){
                return;
            }

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

    public void printMenu() {
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

    public void createUser() {
        String username = readString("Введите имя: ");
        String email = readString("Введите email: ");
        Integer age = readInteger("Введите возраст: ");

        UserDto userDto = new UserDto(null, username, email, age);

        if (!validate(userDto)) {
            return;
        }

        try {
            User newUser = toUser(userDto, null);
            userDao.save(newUser);
            log.info("Пользователь создан: {}", newUser);
            System.out.println("Пользователь создан! " + newUser);
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя  ", e);
            System.out.println("Ошибка " + e.getMessage());
        }
    }

    public void findUser() {

        Integer id = readInteger("Id: ");

        User user = userDao.findById(Long.valueOf(id));
        if (user != null) {
            UserDto userDto = toUserDto(user);
            log.info("Пользователь найден: {}", userDto);
            System.out.println("Найден: " + userDto);
        } else {
            log.info("Пользователь с id {} не найден", id);
            System.out.println("Пользователь с id " + id + " не найден!");
        }
    }

   public void findAllUsers() {
        List<User> users = userDao.findAll();

        if (users.isEmpty()) {
            log.info("Список пользователей пуст");
            System.out.println("Список пуст! ");
        } else {
            log.info("Пользователи найдены: {}", users.size());
            System.out.println("Список пользователей: ");
            users.stream().map(this::toUserDto).forEach(System.out::println);

        }
    }

   public void updateUser() {
        Integer id = readInteger("Id: ");
        User user = userDao.findById(Long.valueOf(id));
        if (user == null) {
            System.out.println("Пользователь с id " + id + " не найден!");
            return;
        }
        String username = readString("Введите новое имя: ");
        String email = readString("Введите новый email: ");
        Integer age = readInteger("Введите новый возраст: ");

        UserDto userDto = new UserDto(user.getId(), username, email, age);
        if (!validate(userDto)) {
            return;
        }

        User updated = toUser(userDto, user.getCreatedAt());


        try {
            userDao.update(updated);
            log.info("Пользователь обновлен: {}", updated);
            System.out.println("Обновлен " + updated);

        } catch (Exception e) {
            log.error("Ошибка обновления пользователя с id {}", id, e);
            System.out.println("Ошибка обновления " + e.getMessage());


        }
    }

    public void deleteUser() {
        int id = readInteger("Id: ");

        User user = userDao.findById((long) id);
        if (user == null) {
            System.out.println("Пользователя с id " + id + " нет!");
            return;
        }

        try {
            userDao.deleteById((long) id);
            log.info("Пользователь удален: {}", user);
            System.out.println("Пользователь с id " + id + " удален");
        } catch (Exception e) {
            log.error("Ошибка удаления пользователя с id {}", id, e);
            System.out.println("Ошибка " + e.getMessage());
        }

    }


    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private Integer readInteger(String prompt) {

        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                System.out.println("Ввод отменён.");
                return null;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Некорректное значение! " + e.getMessage());

            }
        }
    }

    private boolean validate(UserDto userDto) {
        Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(userDto);
        if (!violations.isEmpty()) {
            System.out.println("Ошибки ввода:");
            for (ConstraintViolation<UserDto> v : violations) {
                System.out.println(" - " + v.getMessage());
            }
            return false;
        }
        return true;
    }

    private User toUser(UserDto userDto, LocalDateTime createdAt) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setAge(userDto.getAge());
        user.setCreatedAt(createdAt);
        return user;
    }

    private UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setAge(user.getAge());
        return userDto;
    }
}

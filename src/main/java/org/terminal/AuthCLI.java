package org.terminal;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.exception.WalletExceptions.UserAlreadyExistsException;
import org.exception.WalletExceptions.UserNotFoundException;
import org.exception.WalletExceptions.WrongPasswordException;
import org.model.User;
import org.service.AuthService;

public class AuthCLI {

    private final Scanner scanner;
    private final AuthService authService;

    public AuthCLI(Scanner scanner, AuthService authService) {
        this.scanner = scanner;
        this.authService = authService;
    }

    /**
     * Показать меню авторизации и вернуть вошедшего пользователя
     */
    public User runAuthMenu() {
        System.out.println("\n--- Выполните вход в систему ---");
        User loggedUser = null;
        while (loggedUser == null) {
            loggedUser = handleAuthMenu();
        }
        return loggedUser;
    }
    
    /**
     * Обработка команды
     */
    private User handleAuthMenu() {
        while (true) {
            String[] args = scanner.nextLine().trim().split(" ");
            switch (args[0]) {
                case "": {
                    continue;
                }
                case "registration":
                    return handleAuth(args, args[0]);
                case "login":
                    return handleAuth(args, args[0]);
                case "exit", "q":{
                    System.out.println("Выход из программы...");
                    System.exit(0);
                }
                case "help": {
                    if (args.length == 1)
                        System.out.println(CLIText.AUTH_HELP);
                    else
                        CLIText.showHelp(args);
                    break;
                }
                default:
                    System.out.println("Неизвестная команда! Используйте команды из списка.");
            }

        }
    }

    /**
     * Обработка команды в зависимости от команды
     */
    private User handleAuth(String[] args, String command) {
        try {
            Map<String, String> params = new HashMap<>();

            try {
                Map<String, String> parsedParams = CommandParser.parseCommand(args, command);
                if (parsedParams != null) {
                    params = parsedParams;
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга аргументов: " + e.getMessage());
            }

            String username = getAuthInput("-u", "Имя пользователя", params);
            if (username == null)
                return null;

            String password = getAuthInput("-p", "Пароль", params);
            if (password == null)
                return null;

            if ("login".equalsIgnoreCase(command)) {
                User user = authService.login(username, password);
                System.out.println("Вход выполнен.");
                return user;
            } else if ("registration".equalsIgnoreCase(command)) {
                User user = new User(username, password);
                authService.addUser(user);
                System.out.println("Регистрация успешна.");
                return null;
            } else {
                System.out.println("Неизвестная команда: " + command);
                return null;
            }
        } catch (UserAlreadyExistsException | UserNotFoundException | WrongPasswordException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Ошибка безопасности: " + e.getMessage());
        }
        return null;
    }

    /**
     * Получение информации от пользователя
     */
    private String getAuthInput(String paramName, String prompt, Map<String, String> params) {
        String value = params.get(paramName);
        if (value == null || value.trim().isEmpty()) {
            System.out.print(prompt + " (q - отмена): ");
            value = scanner.nextLine().trim();
            if ("q".equalsIgnoreCase(value)) {
                return null;
            }
            if (value.isEmpty()) {
                System.out.println(prompt + " не может быть пустым.");
                return null;
            }
        }
        return value;
    }
}

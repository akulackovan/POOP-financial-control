package org.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

import org.exception.WalletExceptions.UserAlreadyExistsException;
import org.exception.WalletExceptions.UserNotFoundException;
import org.exception.WalletExceptions.WrongPasswordException;
import org.model.User;
import org.storage.Config;

/**
 * Сервис аутентификации и управления пользователями.
 */
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public AuthService() {
        this(new UserService(Config.get("user.file")));
    }

    /**
     * Хэширует пароль с использованием соли и алгоритма SHA-256.
     * 
     * @param password исходный пароль
     * @return массив [хэш пароля, соль] в Base64 кодировке
     */
    public static Map<String, String> hashPassword(String password) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashedPasswordBytes = md.digest(password.getBytes());

        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String encodedHashedPassword = Base64.getEncoder().encodeToString(hashedPasswordBytes);

        return Map.of(
                "hash", encodedHashedPassword,
                "salt", encodedSalt);
    }

    /**
     * Сохранение пользователя
     * @param user пользователь
     */
    public void saveUser(User user){
        try {
            userService.updateUser(user);
        } catch (Exception e) {
            System.err.println("Ошибка сохранения " + e.getMessage());
        }
    }

    /**
     * Проверяет соответствие пароля хранимому хэшу.
     */
    public static boolean verifyPassword(String password, String storedHashedPassword, String storedSalt)
            throws NoSuchAlgorithmException {
        byte[] salt = Base64.getDecoder().decode(storedSalt);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashedPasswordBytes = md.digest(password.getBytes());

        String newHashedPassword = Base64.getEncoder().encodeToString(hashedPasswordBytes);

        return newHashedPassword.equals(storedHashedPassword);
    }

    /**
     * Регистрирует нового пользователя с хэшированием пароля.
     */
    public void addUser(User user) throws UserAlreadyExistsException, NoSuchAlgorithmException {
        if (userService.findUserByName(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        Map<String, String> hashAndSalt = hashPassword(user.getPassword());
        user.changePassword(hashAndSalt.get("hash"), hashAndSalt.get("salt"));

        userService.addUser(user);
    }

    /**
     * Выполняет аутентификацию пользователя.
     */
    public User login(String username, String password)
            throws UserNotFoundException, WrongPasswordException, NoSuchAlgorithmException {
        User user = userService.findUserByName(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        }

        if (!verifyPassword(password, user.getPassword(), user.getSalt())) {
            throw new WrongPasswordException();
        }

        return user;
    }

    /**
     * Изменяет пароль пользователя с перехэшированием.
     */
    public void changePassword(String username, String newPassword)
            throws UserNotFoundException, NoSuchAlgorithmException {
        User user = userService.findUserByName(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        }

        Map<String, String> hashAndSalt = hashPassword(user.getPassword());
        user.changePassword(hashAndSalt.get("hash"), hashAndSalt.get("salt"));

        userService.updateUser(user);
    }
}

package org.model;

/**
 * Класс, представляющий пользователя системы управления финансами.
 *
 *        Хранит данные о пользователе, его кошельке, имени пользователя и
 *        пароле.
 */
public class User {

    /** Имя пользователя */
    private String username;

    /** Хэш пароля пользователя */
    private String password;

    /** Соль для хэширования пароля */
    private String salt;

    /** Кошелек пользователя */
    private Wallet wallet;

    /**
     * Конструктор без аргументов.
     *
     *        Нужен для корректной работы с библиотекой Jackson (десериализация).
     *        Инициализирует пустой кошелек.
     */
    public User() {
        this.wallet = new Wallet();
    }

    /**
     * Конструктор с указанием имени пользователя, пароля и кошелька.
     * @param username Имя пользователя.
     * @param password Пароль пользователя.
     * @param wallet   Кошелек пользователя.
     */
    public User(String username, String password, Wallet wallet) {
        this.username = username;
        this.password = password;
        this.wallet = wallet;
        this.salt = "";
    }

    /**
     * Конструктор с указанием имени пользователя и пароля.
     * @param username Имя пользователя.
     * @param password Пароль пользователя.
     *
     *                 Инициализирует пустой кошелек.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.wallet = new Wallet();
        this.salt = "";
    }

    /** @return Имя пользователя */
    public String getUsername() {
        return username;
    }

    /** @param username Устанавливает имя пользователя */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return Пароль пользователя */
    public String getPassword() {
        return password;
    }

    /** @param password Устанавливает пароль пользователя */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return Кошелек пользователя */
    public Wallet getWallet() {
        return wallet;
    }

    /** @param wallet Устанавливает кошелек пользователя */
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    /** @return Соль для хэширования пароля */
    public String getSalt() {
        return salt;
    }

    /** @param salt Устанавливает соль для хэширования пароля */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Изменяет пароль пользователя.
     * @param hash Новый хэш пароля.
     * @param salt Новая соль для хэширования.
     */
    public void changePassword(String hash, String salt) {
        this.password = hash;
        this.salt = salt;
    }

}

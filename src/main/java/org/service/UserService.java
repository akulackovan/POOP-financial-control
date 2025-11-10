package org.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.model.User;
import org.storage.Config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Сервис для работы с пользователями
 * Хранит данные в JSON файле
 */
public final class UserService {

    private final String FILE_PATH;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final File file;

    /**
     * Конструктор
     * 
     * @param file - путь к файлу, если null - берется из конфига
     */
    public UserService(String file) {
        objectMapper.registerModule(new JavaTimeModule());
        FILE_PATH = file == null ? Config.get("user.file") : file;
        createFile(FILE_PATH);
        this.file = new File(FILE_PATH);
        objectMapper.findAndRegisterModules();
    }

    /**
     * Загружает всех пользователей из файла
     * Если файла нет - создает новый
     * 
     * @return список пользователей
     */
    public ArrayList<User> loadUsers() {
        if (file.length() == 0) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                objectMapper.writeValue(file, new ArrayList<User>());
            } catch (IOException e) {
                System.err.println("Ошибка при создании файла: " + e.getMessage());
            }
            return new ArrayList<>();
        }

        try {
            ArrayList<User> users = objectMapper.readValue(file, new TypeReference<ArrayList<User>>() {
            });
            // Пересчитываем ID транзакций после загрузки
            for (User user : users) {
                user.getWallet().calculateTranscactionsIDs();
            }
            saveUsers(users);
            return users;
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла пользователей: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Сохраняет всех пользователей в файл
     * 
     * @param users - список пользователей для сохранения
     */
    public void saveUsers(ArrayList<User> users) {
        try {
            objectMapper.writeValue(file, users);
        } catch (IOException e) {
            System.err.println("Ошибка записи пользователей в файл: " + e.getMessage());
        }
    }

    /**
     * Ищет пользователя по имени
     * 
     * @param username - имя пользователя
     * @return найденный пользователь или null
     */
    public User findUserByName(String username) {
        return loadUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Добавляет нового пользователя
     * 
     * @param user - пользователь для добавления
     * @throws RuntimeException если пользователь уже существует
     */
    public void addUser(User user) {
        ArrayList<User> users = loadUsers();
        boolean exists = users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));
        if (exists) {
            throw new RuntimeException("Пользователь " + user.getUsername() + " уже существует!");
        }
        users.add(user);
        saveUsers(users);
    }

    /**
     * Обновляет данные пользователя
     * 
     * @param updatedUser - обновленные данные пользователя
     * @return true если обновление успешно, false если пользователь не найден
     */
    public boolean updateUser(User updatedUser) {
        ArrayList<User> users = loadUsers();
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getUsername().equalsIgnoreCase(updatedUser.getUsername())) {
                users.set(i, updatedUser);
                saveUsers(users);
                return true;
            }
        }
        return false; // пользователь не найден
    }

    public boolean deleteUser(User user) {
        ArrayList<User> users = loadUsers();
        boolean removed = users.removeIf(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));
        if (removed)
            saveUsers(users);
        return removed;
    }

    /**
     * Создает файл если его нет
     * 
     * @param filename - путь к файлу
     */
    public void createFile(String filename) {
        File file = new File(filename);
        if (!file.exists() || file.length() == 0) {
            try {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    boolean dirsCreated = parentDir.mkdirs();
                    if (!dirsCreated) {
                        System.err.println("Не удалось создать директорию: " + parentDir.getAbsolutePath());
                    }
                }

                // Создаем файл
                if (!file.exists()){
                    boolean fileCreated = file.createNewFile();
                    if (!fileCreated) {
                        System.err.println("Не удалось создать файл: " + filename);
                    }
                }
                
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
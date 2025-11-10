package org.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.model.User;
import org.model.Wallet;
import org.storage.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Сервис экспорта/импорта данных пользователя в JSON файл
 */
public class ExportService {
    private final ObjectMapper objectMapper;
    Path exportPath = Paths.get(Config.get("export.path"));
    Path importPath = Paths.get(Config.get("import.path"));

    public ExportService() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
        try {
            Files.createDirectories(exportPath);
        } catch (IOException e) {
            System.out.println("Ошибка создания директории экспорта: " + e.getMessage());
        }
        try {
            Files.createDirectories(importPath);
        } catch (IOException e) {
            System.out.println("Ошибка создания директории импорта: " + e.getMessage());
        }
    }

    /**
     * Экспорт данных пользователя в JSON файл
     */
    public void exportUserData(User user, String filePath) {
        try {
            Map<String, Object> exportData = new HashMap<>();

            exportData.put("username", user.getUsername());
            exportData.put("exportDate", LocalDateTime.now());
            exportData.put("wallet", user.getWallet());

            Path fullPath = Paths.get(exportPath.toString(), filePath);
            objectMapper.writeValue(fullPath.toFile(), exportData);
            System.out.println("Данные экспортированы в файл: " + fullPath);
        } catch (IOException e) {
            System.out.println("Ошибка экспорта: " + e.getMessage());
        }
    }

    /**
     * Импорт данных пользователя из JSON файла
     */
    public void importUserData(User user, String filePath) throws IOException {
        try {
            Path fullPath = Paths.get(importPath.toString(), filePath);
            File importFile = fullPath.toFile();

            // Проверяем существование файла
            if (!importFile.exists()) {
                throw new IOException("Файл не найден: " + fullPath);
            }

            // Читаем как Map
            Map<String, Object> importData = objectMapper.readValue(importFile, Map.class);

            // Восстанавливаем кошелек
            Map<String, Object> walletData = (Map<String, Object>) importData.get("wallet");
            Wallet importedWallet = objectMapper.convertValue(walletData, Wallet.class);
            
            user.setWallet(importedWallet);

            user.getWallet().calculateTranscactionsIDs();
            UserService userService = new UserService(Config.get("user.file"));
            userService.updateUser(user);
            System.out.println("ID данных изменены");
            System.out.println("Данные импортированы из файла: " + filePath);

        } catch (Exception e) {
            System.out.println("Ошибка импорта: " + e.getMessage());
        }
    }

    /**
     * Создает автоматическое имя файла для экспорта
     */
    public String generateExportFileName(User user) {
        String timestamp = LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        return "backup_" + user.getUsername() + "_" + timestamp + ".json";
    }
}
package org.terminal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Класс для утилит CLI
 */
public class UtilsCLI {

    // Вспомогательные методы для парсинга
    public static Integer parseInteger(String value) {
        if (value == null)
            return null;
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: неверный формат числа: " + value);
            return null;
        }
    }

    public static LocalDateTime parseDateTime(String value) {
        if (value == null)
            return null;
        if (value.matches("\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}")) { // проверка через регулярные выражения
            // Формат с временем: "dd.MM.yyyy HH:mm"
            try {
                return LocalDateTime.parse(value,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка парсинга даты с временем: " + value);
                return null;
            }
        } else if (value.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            // Формат без времени: "dd.MM.yyyy"
            try {
                LocalDate date = LocalDate.parse(value,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                return date.atTime(23, 59); // конец дня
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка парсинга даты: " + value);
                return null;
            }
        } else {
            System.out.println("Неверный формат даты. Используйте: дд.мм.гггг [чч:мм]");
            return null;
        }
    }

}

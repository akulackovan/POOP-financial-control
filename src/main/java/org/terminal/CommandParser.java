package org.terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для обработки параметов команды
 */
public class CommandParser {
    private final static List<String> errors = new ArrayList<>();
    private final static Map<String, List<String>> commandArgs = new HashMap<>();

    static {
        commandArgs.put("login", Arrays.asList("-u", "-p"));
        commandArgs.put("registration", Arrays.asList("-u", "-p"));
        commandArgs.put("add_income", Arrays.asList("-c", "-a", "-d"));
        commandArgs.put("add_outcome", Arrays.asList("-c", "-a", "-d"));
        commandArgs.put("set_budget", Arrays.asList("-c", "-a", "-d"));
        commandArgs.put("status", Collections.emptyList());
        commandArgs.put("actions list", Arrays.asList("-c", "-t", "-n", "-from", "-to"));
        commandArgs.put("actions remove", Arrays.asList("-i"));
        commandArgs.put("category remove", Arrays.asList("-c", "-t"));
        commandArgs.put("category list", Arrays.asList("-c", "-t"));
        commandArgs.put("category edit", Arrays.asList("-c", "-t", "-n"));
        commandArgs.put("category add", Arrays.asList("-c", "-t"));
        commandArgs.put("export", Arrays.asList("-f"));
        commandArgs.put("import", Arrays.asList("-f"));
    }

    public static Map<String, String> parseCommand(String[] args, String command) {
        errors.clear();

        // Получаем ожидаемые аргументы для команды
        List<String> expectedArgs = commandArgs.get(command);
        if (expectedArgs == null) {
            errors.add("Неизвестная команда: " + command);
            throw new IllegalArgumentException(String.join(", ", errors));
        }

        // Индекс начала аргументов (после имени команды)
        int indexOfBeginArguments = command.split(" ").length;

        Map<String, String> params = new HashMap<>();

        for (int i = indexOfBeginArguments; i < args.length; i++) {
            String currentArg = args[i];

            if (currentArg.startsWith("-")) {
                // Проверяем, что флаг ожидаемый
                if (!expectedArgs.contains(currentArg)) {
                    errors.add("Неизвестный флаг: " + currentArg);
                    continue;
                }

                // Проверяем, что есть значение для флага
                if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                    errors.add("Флаг " + currentArg + " требует значения");
                    continue;
                }

                // Обработка значений в кавычках
                String value = args[i + 1];
                if (value.startsWith("\"") && !value.endsWith("\"")) {
                    // Многословное значение в кавычках
                    StringBuilder quotedValue = new StringBuilder();
                    quotedValue.append(value.substring(1)); // убираем открывающую кавычку

                    int j = 2;
                    while (i + j < args.length && !args[i + j].endsWith("\"")) {
                        quotedValue.append(" ").append(args[i + j]);
                        j++;
                    }

                    if (i + j < args.length) {
                        quotedValue.append(" ").append(args[i + j], 0, args[i + j].length() - 1);
                        value = quotedValue.toString();
                        i += j; // пропускаем все слова кавычек
                    } else {
                        errors.add("Незакрытая кавычка для флага: " + currentArg);
                        value = quotedValue.toString();
                    }
                } else {
                    // Обычное значение
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    i++; // пропускаем значение
                }

                params.put(currentArg, value);
            } else {
                errors.add("Неожиданный аргумент: " + currentArg + ". Ожидался флаг.");
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        return params;
    }

}
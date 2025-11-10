package org.terminal;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.exception.WalletExceptions.BudgetExceededException;
import org.exception.WalletExceptions.IncomeCategoryAlreadyExistsException;
import org.exception.WalletExceptions.OutcomeCategoryAlreadyExistsException;
import org.exception.WalletExceptions.TransactionNotFoundException;
import org.model.Transaction;
import org.model.TransactionType;
import org.model.User;
import org.model.Wallet;
import org.service.AuthService;
import org.service.ExportService;
import org.service.WalletService;

/**
 * Класс для обрабтка команд и вовода информации
 */
public class CLI {

    private final Scanner scanner;
    private final AuthService authService;
    private final WalletService walletService;
    private User loggedUser;

    public CLI() {
        this.scanner = new Scanner(System.in, "CP866");
        this.authService = new AuthService();
        this.walletService = new WalletService();
        this.loggedUser = null;
    }

    public CLI(AuthService authService, WalletService walletService) {
        this.scanner = new Scanner(System.in, "CP866");
        this.authService = authService;
        this.walletService = new WalletService();
        this.loggedUser = null;
    }

    /**
     * Точка входа
     */
    public void run() {
        System.out.println("Система управления личными финансами");
        showAuthMenu();
    }

    /**
     * Окно авторизации
     */
    private void showAuthMenu() {
        AuthCLI authCLI = new AuthCLI(scanner, authService);
        loggedUser = authCLI.runAuthMenu();
        showMainMenu();
    }

    /**
     * Показать уведоиление
     */
    private void showWarning() {
        Wallet wallet = loggedUser.getWallet();
        for (String category : wallet.getOutcomeCategories()) {
            if (loggedUser.getWallet().isBudgetExceeded(category)) {
                System.out.println("Внимание! Расходы категории " + category + " превышают бюджет.");
            } else if (loggedUser.getWallet().isBudgetWarning(category)) {
                System.out.println("Внимание! Расходы категории " + category + " превышают 80% бюджета.");
            }
        }
        walletService.showBudgetWarningTotal(loggedUser);
    }

    /**
     * Получить значение от пользователя
     */
    private String getInput(String prompt) {
        System.out.print(prompt + " (q - отмена): ");
        String value = scanner.nextLine().trim();
        if ("q".equalsIgnoreCase(value)) {
            return null;
        }
        if (value.isEmpty()) {
            System.out.println(prompt + " не может быть пустым.");
            return null;
        }
        return value;
    }

    /**
     * Главное меню
     */
    private void showMainMenu() {
        System.out.println("\n--- Вход в главное меню ---");

        showWarning();

        while (loggedUser != null) {
            System.out.println("\nВведите операцию:");

            String[] args = scanner.nextLine().trim().split(" ");

            switch (args[0]) {
                case "" -> {
                    continue;
                }
                case "status" -> showStatus();
                case "actions" -> handleEvents(args);
                case "add_income", "add_outcome", "set_budget" -> addAmount(args, args[0]);
                case "export" -> handleExport(args);
                case "import" -> handleImport(args);
                case "help" -> showHelpMain(args);
                case "exit", "q" -> exit();
                case "category" -> handleCategory(args);
                case "logout" -> logout(args);
                default -> System.out.println("Неизвестная команда! Используйте команды из списка.");
            }
            System.out.println("Операция завершена");
        }
    }

    /**
     * Выбор команды транзакций
     */
    private void handleEvents(String[] args) {
        if (args.length == 2) {
            switch (args[0] + " " + args[1]) {
                case "actions list" -> showEvents(args);
                case "actions remove" -> removeEvents(args);
            }
        } else {
            System.out.println("Неправильно введена команда");
        }

    }

    /**
     * Показать справку о командах
     */
    private void showHelpMain(String[] args) {
        if (args.length == 1) {
            System.out.println(CLIText.MAIN_HELP);
            return;
        }
        if (args.length != 2) {
            System.out.println("У команды нет параметров");
            System.out.println(CLIText.AUTH_HELP);
        }
    }

    /**
     * Выход из учетной записи
     */
    private void logout(String[] args) {
        if (args.length != 1) {
            System.out.println("У команды нет параметров");
            return;
        }
        loggedUser = null;

    }

    /**
     * Показать список категорий
     */
    private String getListCategories(boolean isIncome, boolean isCreate) {
        while (true) {
            System.out.println("Выберите категорию из списка:");
            List<String> categories;
            if (isIncome) {
                categories = walletService.getIncomeCategories(loggedUser);
            } else {
                categories = walletService.getOutcomeCategories(loggedUser);
            }
            if (categories.isEmpty()) {
                System.out.println(
                        "Отсутсвуют категории!");
                if (isCreate) {
                    return getInput("Введите название для создания новой категории");
                } else {
                    return null;
                }
            }
            int i = 1;
            if (isCreate)
                System.err.println("0. Новая категория");
            for (String key : categories) {
                System.out.println(i + ". " + key);
                i++;
            }
            String input = getInput("");
            if (input.equals("0") && isCreate) {
                return getInput("Введите название для создания новой категории");
            }
            try {
                int index = Integer.parseInt(input); // переводим ввод в число
                if (index <= categories.size()) {
                    return categories.get(index - 1);
                } else {
                    System.out.println("Некорректный номер категории. Введите число от 1 до " + categories.size());
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println("Некорректный ввод категории. Введите номер категории из списка");
            }
        }

    }

    /**
     * Показать общий статус
     */
    private void showStatus() {
        Wallet wallet = loggedUser.getWallet();

        System.out.println();
        System.out.println("==================== ФИНАНСОВЫЙ ОТЧЕТ ====================");
        System.out.println();

        System.out.println("+----------------------+---------------------------+");
        System.out.println("| Показатель           | Значение, руб              |");
        System.out.println("+----------------------+---------------------------+");
        System.out.printf("| %-20s | %20.2f руб. |\n", "Доходы", wallet.getTotalIncome() / 100.0);
        System.out.printf("| %-20s | %20.2f руб. |\n", "Расходы", wallet.getTotalOutcomeSpent() / 100.0);
        System.out.printf("| %-20s | %20.2f руб. |\n", "Бюджет", wallet.getTotalOutcomePlanned() / 100.0);
        System.out.printf("| %-20s | %20.2f руб. |\n", "Остаток",
                (wallet.getTotalIncome() - wallet.getTotalOutcomeSpent()) / 100.0);
        System.out.println("+----------------------+---------------------------+");
        System.out.println();

        System.out.println("----------- ДОХОДЫ ПО КАТЕГОРИЯМ -----------");
        Map<String, Long> incomeByCategory = wallet.getIncomeByCategory();

        if (incomeByCategory.isEmpty()) {
            System.out.println("Нет данных");
        } else {
            System.out.println("Категория                 Сумма (руб.)");
            System.out.println("--------------------------------------------");
            incomeByCategory.forEach((cat, sum) -> System.out
                    .printf("%-25s | %15.2f\n-------------------------------------------\n", cat, sum / 100.0));
        }

        System.out.println();
        System.out.println("------------- БЮДЖЕТ ПО КАТЕГОРИЯМ -------------");
        List<String> outcomeCategories = wallet.getOutcomeCategories();

        if (outcomeCategories.isEmpty()) {
            System.out.println("Нет данных");
        } else {
            System.out.println(
                    "      Статус            Категория         Бюджет, руб      Остаток, руб           Факт, руб");
            System.out.println(
                    "--------------------------------------------------------------------------------------------");

            outcomeCategories.forEach(category -> {
                long planned = wallet.getPlannedOutcomeByCategory().getOrDefault(category, 0L);
                long spent = wallet.getSpentOutcomeByCategory().getOrDefault(category, 0L);
                long remaining = wallet.getRemainingBudget(category);

                String status = wallet.isBudgetExceeded(category)
                        ? "Бюджет превышен"
                        : wallet.isBudgetWarning(category)
                                ? "Расходы ≥ 80%"
                                : "";

                System.out.printf(
                        "%-18s | %-15s | %15.2f | %15.2f | %15.2f%n--------------------------------------------------------------------------------------------\n",
                        status,
                        category,
                        planned / 100.0,
                        remaining / 100.0,
                        spent / 100.0);
            });
        }

        System.out.println();
        walletService.showBudgetWarningTotal(loggedUser);

    }

    /**
     * Показать транзакции
     */
    private void showEvents(String[] args) {
        Map<String, String> parsedArgs;
        try {
            parsedArgs = CommandParser.parseCommand(args, "actions list");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Парсим параметры с валидацией
        String category = parsedArgs.get("-c");
        String type = parsedArgs.get("-t");
        Integer count = UtilsCLI.parseInteger(parsedArgs.get("-n"));

        LocalDateTime fromArg;
        LocalDateTime toArg;

        try {
            fromArg = UtilsCLI.parseDateTime(parsedArgs.get("-from"));
            toArg = UtilsCLI.parseDateTime(parsedArgs.get("-to"));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга дат: " + e.getMessage());
            return;
        }

        // Применяем фильтры
        List<Transaction> filtered = loggedUser.getWallet().getTransactions().stream()
                .filter(tx -> category == null || tx.getCategory().equals(category))
                .filter(tx -> type == null || tx.getTransactionType().getName().equalsIgnoreCase(type))
                .filter(tx -> fromArg == null || !tx.getTimestamp().isBefore(fromArg))
                .filter(tx -> toArg == null || !tx.getTimestamp().isAfter(toArg))
                .collect(Collectors.toList());

        // Применяем ограничение по количеству
        if (count != null && count > 0 && count < filtered.size()) {
            filtered = filtered.subList(filtered.size() - count, filtered.size());
        }

        // Выводим результат
        if (filtered.isEmpty()) {
            System.out.println("Транзакций не найдено для выбранных фильтров.");
        } else {
            filtered.forEach(System.out::println);
        }
    }

    /**
     * Удаление транзакций
     */
    private void removeEvents(String[] args) {
        Map<String, String> params;
        try {
            params = CommandParser.parseCommand(args, "actions remove");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        String idParam = params.get("-i");
        if (idParam == null) {
            try {
                idParam = getInput("Введите id транзакции");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            walletService.removeTransaction(loggedUser, idParam);
        } catch (TransactionNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Получение параметров
     */
    private Map<String, String> parseArgsSafely(String[] args, String command) {
        try {
            return CommandParser.parseCommand(args, command);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * 
     * Получение категории
     */
    private String getCategoryWithValidation(Map<String, String> params, boolean isIncome) {
        String category = params.getOrDefault("-c", null);
        String amountStr = params.getOrDefault("-a", null);

        if (category == null && amountStr != null) {
            System.out.println("Категория не задана при установленной сумме");
            return null;
        }

        if (category == null) {
            category = getListCategories(isIncome, true);
        }
        return category;
    }

    /**
     * Получение суммы
     */
    private Long getAmountWithValidation(Map<String, String> params) {
        String amountStr = params.getOrDefault("-a", null);

        try {
            if (amountStr == null) {
                return promptForAmount();
            } else {
                return (long) (Double.parseDouble(amountStr) * 100);
            }
        } catch (NumberFormatException e) {
            System.out.println("Введено не число в поле суммы");
            return null;
        }
    }

    /**
     * Универсальный метод для добавления доходов/расходов/бюджетов
     */
    private void addAmount(String[] args, String command) {
        Map<String, String> params = parseArgsSafely(args, command);
        if (params == null)
            return;

        String category = getCategoryWithValidation(params, command.equals("add_income"));
        if (category == null)
            return;

        Long amount = getAmountWithValidation(params);
        if (amount == null)
            return;

        if (!command.equals("add_income")) {
            try {
                checkForOutBudget(category, amount, command);
            } catch (BudgetExceededException e) {
                System.out.println(e.getMessage());
                return;
            }
        }

        switch (command) {
            case "set_budget":
                walletService.addPlannedOutcome(loggedUser, category, amount);
                break;
            case "add_outcome":
                walletService.addSpentOutcome(loggedUser, category, amount);
                break;
            default:
                walletService.addIncome(loggedUser, category, amount);
                break;
        }
        walletService.showBudgetWarning(loggedUser, category);
        walletService.showBudgetWarningTotal(loggedUser);
    }

    /**
     * Проверяет возможное превышение бюджета
     */
    private void checkForOutBudget(String category, long amount, String command) throws BudgetExceededException {
        if (loggedUser.getWallet().isExistBudget(category, amount, !command.equals("set_budget"))) {
            System.out.println(String.format("При выполнении операции траты будут превышать бюджет"));
            System.out.println(String.format("Вы уверены, что хотите продолжить операцию"));

            System.out.print("Введите 'y' для подтверждения, любой другой символ для отмены: ");
            String confirm = scanner.nextLine().trim();
            if (!confirm.equalsIgnoreCase("y")) {
                throw new BudgetExceededException();
            }
        }
        if (loggedUser.getWallet().isExistIncome(category, amount, command.equals("set_budget"))) {
            System.out.println(
                    String.format("При выполнении операции траты или планируемый бюджет будут превышать поступления"));
            System.out.println(String.format("Вы уверены, что хотите продолжить операцию"));

            System.out.print("Введите 'y' для подтверждения, любой другой символ для отмены: ");
            String confirm = scanner.nextLine().trim();
            if (!confirm.equalsIgnoreCase("y")) {
                throw new BudgetExceededException();
            }
        }
    }

    /**
     * Считывание суммы
     * 
     * @return сумма а копейках
     */
    private long promptForAmount() {
        while (true) {
            String input = getInput("Введите сумму для категории");
            try {
                return (long) (Double.parseDouble(input) * 100);
            } catch (NumberFormatException e) {
                System.out.println("Некорректная сумма. Введите число (например: 1500.50)");
            }
        }
    }

    /**
     * Обработка категорий
     */
    private void handleCategory(String[] args) {
        Map<String, String> argumentsCategory;
        if (args.length < 2) {
            System.out.println("Отсутствует подкоманда.");
            return;
        }
        try {
            argumentsCategory = CommandParser.parseCommand(args, args[0] + " " + args[1]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        String type = argumentsCategory.getOrDefault("-t", null);
        String category = argumentsCategory.getOrDefault("-c", null);
        if (category == null || (loggedUser.getWallet().hasIncomeCategory(category)
                && loggedUser.getWallet().hasOutcomeCategory(category))) {
            type = getType(argumentsCategory.get("-t"));
            if (type == null) {
                return;
            }
        }
        if (category != null && ("remove".equals(args[1]) || "edit".equals(args[1]))) {
            if (loggedUser.getWallet().hasIncomeCategory(category)
                    && !loggedUser.getWallet().hasOutcomeCategory(category)) {
                type = "income";
            } else if (!loggedUser.getWallet().hasIncomeCategory(category)
                    && loggedUser.getWallet().hasOutcomeCategory(category)) {
                type = "outcome";
            } else if (loggedUser.getWallet().hasIncomeCategory(category)
                    && loggedUser.getWallet().hasOutcomeCategory(category)) {
                type = getType(argumentsCategory.get("-t"));
                if (type == null) {
                    return;
                }
            } else {
                System.out.println("Категории не существует");
                return;
            }
        }

        if (!"list".equals(args[1]) && !"add".equals(args[1]) && category == null) {
            category = getListCategories("income".equals(type), false);
            if (category == null)
                return; // отмена
        }

        String newName = args[1].equals("add") ? argumentsCategory.getOrDefault("-c", null)
                : argumentsCategory.getOrDefault("-n", null);
        if (newName == null && (args[1].equals("edit") || args[1].equals("add"))) {
            newName = getInput("Введите новое название категории");
        }

        switch (args[1]) {
            case "list" -> printListCategories(type);
            case "remove" -> removeCategory(type, category);
            case "edit" -> editCategory(type, category, newName);
            case "add" -> addCategory(type, newName);
            default -> System.out.println("Неизвестная команда категории");
        }

    }

    private void addCategory(String type, String category) {
        if ((loggedUser.getWallet().hasIncomeCategory(category) && "income".equals(type)) || (loggedUser.getWallet()
                .hasOutcomeCategory(category) && "outcome".equals(type))) {
            System.out.println("Категория уже существует. Операция будет прервана.");
            return;
        }
        if (type.equals("outcome")) {

            walletService.addSpentOutcome(loggedUser, category, 0L);
        } else if (type.equals("income")) {
            walletService.addIncome(loggedUser, category, 0L);
        }

    }

    /**
     * Удаление категорий
     * 
     * @param type     тип
     * @param category категория
     */
    private void removeCategory(String type, String category) {
        String typeText = "income".equalsIgnoreCase(type) ? "категории доходов" : "категории расходов";
        System.out.println(String.format("Вы уверены, что хотите удалить данные из %s '%s'?", typeText, category));

        System.out.print("Введите 'y' для подтверждения, любой другой символ для отмены: ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Удаление отменено.");
            return;
        }

        if ("income".equals(type)) {
            walletService.removeTransactionsByCategoryAndType(loggedUser, category, TransactionType.INCOME);
        } else {
            walletService.removeTransactionsByCategoryAndType(loggedUser, category, TransactionType.OUTCOME_PLANNED);
            walletService.removeTransactionsByCategoryAndType(loggedUser, category, TransactionType.OUTCOME_SPENT);
        }

        System.out.println("Удалены транзакции из " + typeText + " " + category);

    }

    /**
     * Изменение категории
     */
    private void editCategory(String type, String category, String newName) {
        try {
            if ((loggedUser.getWallet().hasIncomeCategory(newName) && "income".equals(type)) || (loggedUser.getWallet()
                    .hasOutcomeCategory(newName) && "outcome".equals(type))) {
                System.out.println("Категория уже существует. Операция будет прервана.");
                return;
            }
            walletService.editCategory(loggedUser, category, type, newName);

        } catch (IncomeCategoryAlreadyExistsException | OutcomeCategoryAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Получение типа категории
     */
    private String getType(String type) {
        if (type != null)
            return type;
        System.out.println("Введите тип категорий:");
        System.out.println("1. income - доход");
        System.out.println("2. outcome - расход");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("q"))
            return null;
        switch (input) {
            case "1" -> type = "income";
            case "2" -> type = "outcome";
            default -> {
                System.out.println("Введено неверное значение");
                return null;
            }
        }
        return type;
    }

    /**
     * Вывод категорий
     **/
    private void printListCategories(String type) {
        if (type == null || type.isBlank()) {
            System.out.println("\n=== Категории доходов ===");
            walletService.printIncomeCategories(loggedUser);

            System.out.println("\n=== Категории расходов ===");
            walletService.printOutcomeCategories(loggedUser);
            return;
        }

        switch (type.toLowerCase()) {
            case "income" -> {
                System.out.println("\n=== Категории доходов ===");
                walletService.printIncomeCategories(loggedUser);
            }
            case "outcome" -> {
                System.out.println("\n=== Категории расходов ===");
                walletService.printOutcomeCategories(loggedUser);
            }
            default -> System.out.println("Ошибка: тип должен быть 'income' или 'outcome'.");
        }
    }

    /**
     * Экспорт кошелька
     **/
    private void handleExport(String[] args) {
        Map<String, String> arguments;
        try {
            arguments = CommandParser.parseCommand(args, args[0]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        ExportService exportService = new ExportService();
        try {
            exportService.exportUserData(loggedUser,
                    arguments.getOrDefault("-f", exportService.generateExportFileName(loggedUser)));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Импорт файла
     **/
    private void handleImport(String[] args) {
        Map<String, String> arguments;
        try {
            arguments = CommandParser.parseCommand(args, args[0]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        String filename = arguments.getOrDefault("-f", null);
        if (filename == null) {
            System.out.println("Введите имя файла:");
            filename = scanner.nextLine().trim();
            if (filename.equalsIgnoreCase("q"))
                return;
        }

        ExportService exportService = new ExportService();
        try {
            exportService.importUserData(loggedUser,
                    arguments.getOrDefault("-f", exportService.generateExportFileName(loggedUser)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Завершает работу программы.
     */
    public void exit() {
        if (loggedUser != null) {
            authService.saveUser(loggedUser);
        }
        System.out.println("Выход из программы...");
        System.exit(0);
    }

}
package org.service;

import java.util.List;
import java.util.stream.Collectors;

import org.exception.WalletExceptions.CategoryNotFoundException;
import org.exception.WalletExceptions.IncomeCategoryAlreadyExistsException;
import org.exception.WalletExceptions.OutcomeCategoryAlreadyExistsException;
import org.exception.WalletExceptions.TransactionNotFoundException;
import org.model.Transaction;
import org.model.TransactionType;
import org.model.User;
import org.model.Wallet;
import org.storage.Config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * Сервис работы с кошельком пользователя
 */
@JsonIgnoreProperties(ignoreUnknown = true) // игнорирование сериализаций
public class WalletService {
    private final UserService userService = new UserService(Config.get("user.file"));

    /**
     * Добавляет доход пользователю
     */
    public void addIncome(User user, String category, long amount) {
        Transaction transaction = new Transaction(category, amount, TransactionType.INCOME);
        try {
            user.getWallet().addTransaction(transaction);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        userService.updateUser(user);
    }

    /**
     * Добавляет планируемый расход
     */
    public void addPlannedOutcome(User user, String category, long amount) {
        Transaction transaction = new Transaction(category, amount, TransactionType.OUTCOME_PLANNED);
        try {
            user.getWallet().addTransaction(transaction);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        userService.updateUser(user);

    }

    /**
     * Добавляет фактический расход
     */
    public void addSpentOutcome(User user, String category, long amount) {
        Transaction transaction = new Transaction(category, amount, TransactionType.OUTCOME_SPENT);
        try {
            user.getWallet().addTransaction(transaction);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        userService.updateUser(user);
    }

    public List<String> getIncomeCategories(User user) {
        return user.getWallet().getIncomeCategories();
    }

    public List<String> getOutcomeCategories(User user) {
        return user.getWallet().getOutcomeCategories();
    }

    /**
     * Валидация существования категории доходов
     */
    public void validateIncomeCategory(User user, String category) throws CategoryNotFoundException {
        if (!user.getWallet().hasIncomeCategory(category)) {
            throw new CategoryNotFoundException(category);
        }
    }

    /**
     * Валидация существования категории расходов
     */
    public void validateOutcomeCategory(User user, String category) throws CategoryNotFoundException {
        if (!user.getWallet().hasOutcomeCategory(category)) {
            throw new CategoryNotFoundException(category);
        }
    }

    /**
     * Выводит в консоль категории доходов с суммами
     */
    public void printIncomeCategories(User user) {
        Wallet wallet = user.getWallet();
        List<String> categories = wallet.getIncomeCategories();

        if (categories.isEmpty()) {
            System.out.println("Нет категорий доходов.");
            return;
        }

        System.out.printf("%-20s | %-15s%n", "Категория", "Сумма");
        System.out.println("----------------------+------------");

        for (String category : categories) {
            double amount = wallet.getIncomeByCategory().get(category) / 100.0;
            System.out.printf("%-20s | %15.2f%n", category, amount);
        }
    }

    /**
     * Выводит в консоль категории расходов с бюджетами и остатками
     */
    public void printOutcomeCategories(User user) {
        Wallet wallet = user.getWallet();
        List<String> categories = wallet.getOutcomeCategories();

        if (categories.isEmpty()) {
            System.out.println("Нет категорий расходов.");
            return;
        }

        System.out.printf("%-20s | %-15s | %-15s | %-15s%n", "Категория", "План", "Факт", "Остаток");
        System.out.println("----------------------+------------+------------+------------");

        for (String category : categories) {
            double planned = wallet.getPlannedOutcomeByCategory().getOrDefault(category, 0L) / 100.0;
            double spent = wallet.getSpentOutcomeByCategory().getOrDefault(category, 0L) / 100.0;
            double remaining = (wallet.getPlannedOutcomeByCategory().getOrDefault(category, 0L) -
                    wallet.getSpentOutcomeByCategory().getOrDefault(category, 0L)) / 100.0;

            System.out.printf("%-20s | %15.2f | %15.2f | %15.2f%n",
                    category, planned, spent, remaining);
        }
    }

    /**
     * Удаляет все транзакции указанной категории и типа
     */
    public void removeTransactionsByCategoryAndType(User user, String category, TransactionType type) {
        Wallet wallet = user.getWallet();
        List<Transaction> transactions = wallet.getTransactions();

        // Фильтруем транзакции
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(tx -> !(tx.getCategory().equals(category) && tx.getTransactionType() == type))
                .collect(Collectors.toList());

        wallet.setTransactions(filteredTransactions);
        wallet.calculateTranscactionsIDs();
        userService.updateUser(user);
    }

    /**
     * Показывает предупреждения о бюджете для категории
     */
    public void showBudgetWarning(User user, String category) {
        if (user.getWallet().isBudgetExceeded(category)) {
            System.out.println("Внимание! Расходы превышают бюджет категории!");
        } else if (user.getWallet().isBudgetWarning(category)) {
            System.out.println("Внимание! Расходы в категории больше или равны 80% бюджета!");
        }
    }

    /**
     * Показывает общие предупреждения о превышении доходов
     */
    public void showBudgetWarningTotal(User user) {
        if (user.getWallet().isBudgetExceededPlanned()) {
            System.out.println(
                    "Внимание! Бюджет категорий (планируемые расходы для категории) превышает общие накопления!");
        }
        if (user.getWallet().isBudgetExceededSpent()) {
            System.out.println("Внимание! Расходы превышают общие накопления!");
        }
    }

    /**
     * Удаляет транзакцию по ID с пересчетом идентификаторов
     */
    public void removeTransaction(User user, String id) throws TransactionNotFoundException {
        if (id == null || id.trim().isEmpty()) {
            System.out.println("ID транзакции не может быть пустым");
            return;
        }

        try {
            int transactionId = Integer.parseInt(id.trim());
            List<Transaction> transactions = user.getWallet().getTransactions();

            boolean removed = transactions.removeIf(tx -> tx.getId() == transactionId);

            if (!removed) {
                throw new TransactionNotFoundException(id);
            }

            // Пересчитываем ID и сохраняем изменения
            user.getWallet().setTransactions(transactions);
            user.getWallet().calculateTranscactionsIDs();
            userService.updateUser(user);

        } catch (NumberFormatException e) {
            throw new TransactionNotFoundException(id);
        }
    }

    /**
     * Переименовывает категорию во всех связанных транзакциях
     */
    public void editCategory(User user, String oldCategory, String type, String newCategory)
            throws IncomeCategoryAlreadyExistsException,
            OutcomeCategoryAlreadyExistsException {
        Wallet wallet = user.getWallet();
        List<Transaction> transactions = wallet.getTransactions();

        // Проверяем, что новая категория не существует в том же типе
        if ("income".equalsIgnoreCase(type)) {
            if (wallet.hasIncomeCategory(newCategory)) {
                throw new IncomeCategoryAlreadyExistsException(newCategory);

            }
        } else if ("outcome".equalsIgnoreCase(type)) {
            if (wallet.hasOutcomeCategory(newCategory)) {
                throw new OutcomeCategoryAlreadyExistsException(newCategory);
            }
        }

        // Обновляем категорию в транзакциях
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().equals(oldCategory)) {
                // Проверяем тип транзакции, если указан тип
                if (type == null ||
                        ("income".equalsIgnoreCase(type) && transaction.getTransactionType() == TransactionType.INCOME)
                        ||
                        ("outcome".equalsIgnoreCase(type) &&
                                (transaction.getTransactionType() == TransactionType.OUTCOME_PLANNED ||
                                        transaction.getTransactionType() == TransactionType.OUTCOME_SPENT))) {
                    transaction.setCategory(newCategory);
                }
            }
        }

        user.getWallet().setTransactions(transactions);
        user.getWallet().calculateTranscactionsIDs();
        userService.updateUser(user);
    }
}
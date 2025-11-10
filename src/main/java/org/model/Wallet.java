package org.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Класс, представляющий кошелек пользователя.
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class Wallet {
    private List<Transaction> transactions = new ArrayList<>();
    private int lastTransactionId = 1;


    public Wallet() {
        
    }

    public Wallet(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }

    /**
     * Добавляет транзакцию в кошелек с автоматическим назначением ID.
     * 
     * @param transaction транзакция для добавления
     * @throws IllegalArgumentException если сумма не положительная
     */
    public void addTransaction(Transaction transaction) {
        if (transaction.getAmount() < 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        transaction.setId(lastTransactionId);
        transactions.add(transaction);
        lastTransactionId++;
    }

    /**
     * Проверяет превышение бюджета категории при операции.
     * 
     * @param category  категория для проверки
     * @param amount    сумма операции
     * @param isPlanned true для планируемого расхода, false для фактического
     * @return true если операция превысит бюджет категории
     */
    public boolean isExistBudget(String category, long amount, boolean isPlanned) {
        long planned = getPlannedOutcomeByCategory().getOrDefault(category, 0L);
        long spent = getSpentOutcomeByCategory().getOrDefault(category, 0L);

        return (!isPlanned && planned > 0 && spent + amount > planned) || (isPlanned && spent > planned + amount);
    }


    /**
     * Проверяет превышение общих доходов при операции.
     * 
     * @param category  категория операции
     * @param amount    сумма операции
     * @param isPlanned true для планируемого расхода, false для фактического
     * @return true если операция превысит общие доходы
     */
    public boolean isExistIncome(String category, long amount, boolean isPlanned) {
        long planned = getTotalOutcomePlanned();
        long spent = getTotalOutcomeSpent();
        long income = getTotalIncome();

        return (!isPlanned && spent + amount > income) || (isPlanned && income < planned + amount);
    }


    public Map<String, Long> getIncomeByCategory() {
        return transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingLong(Transaction::getAmount)));
    }

    public long getTotalIncome() {
        return transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.INCOME)
                .mapToLong(Transaction::getAmount)
                .sum();
    }


    public Map<String, Long> getPlannedOutcomeByCategory() {
        Map<String, Long> result = new HashMap<>();
        for (Transaction tx : transactions) {
            if (tx.getTransactionType() == TransactionType.OUTCOME_PLANNED) {
                result.put(tx.getCategory(), tx.getAmount());
            }
        }
        return result;
    }

    public Map<String, Long> getSpentOutcomeByCategory() {
        return transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.OUTCOME_SPENT)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingLong(Transaction::getAmount)));
    }

    public long getTotalOutcomePlanned() {
        return getPlannedOutcomeByCategory().values().stream().mapToLong(Long::longValue).sum();
    }

    public long getTotalOutcomeSpent() {
        return getSpentOutcomeByCategory().values().stream().mapToLong(Long::longValue).sum();
    }


    public List<String> getIncomeCategories() {
        return transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.INCOME)
                .map(Transaction::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getOutcomeCategories() {
        return transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionType.OUTCOME_PLANNED ||
                        tx.getTransactionType() == TransactionType.OUTCOME_SPENT)
                .map(Transaction::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean hasIncomeCategory(String category) {
        return transactions.stream()
                .anyMatch(tx -> tx.getTransactionType() == TransactionType.INCOME &&
                        tx.getCategory().equals(category));
    }

    public boolean hasOutcomeCategory(String category) {
        return transactions.stream()
                .anyMatch(tx -> (tx.getTransactionType() == TransactionType.OUTCOME_PLANNED ||
                        tx.getTransactionType() == TransactionType.OUTCOME_SPENT) &&
                        tx.getCategory().equals(category));
    }


    public long getRemainingBudget(String category) {
        long planned = getPlannedOutcomeByCategory().getOrDefault(category, 0L);
        long spent = getSpentOutcomeByCategory().getOrDefault(category, 0L);
        return planned - spent;
    }

    public boolean isBudgetExceeded(String category) {
        return getRemainingBudget(category) < 0;
    }

    public boolean isBudgetWarning(String category) {
        long planned = getPlannedOutcomeByCategory().getOrDefault(category, 0L);
        long spent = getSpentOutcomeByCategory().getOrDefault(category, 0L);
        return planned > 0 && (double) spent / planned >= 0.8 && (double) spent / planned < 1.0;
    }

    public boolean isBudgetExceededPlanned() {
        long planned = getTotalOutcomePlanned();
        long income = getTotalIncome();
        return income < planned;
    }
    public boolean isBudgetExceededSpent() {
        long spent = getTotalOutcomeSpent();
        long income = getTotalIncome();
        return income < spent;
    }


    /**
     * Пересчитывает ID всех транзакций (используется при импорте данных).
     * Назначает последовательные ID начиная с 1.
     */
    public void calculateTranscactionsIDs(){
        int currentLastId = 1;

        // Назначаем новые ID всем транзакциям
        for (var transaction : transactions) {
            transaction.setId(currentLastId); // Сбрасываем ID
            currentLastId += 1; // Назначаем новый ID
        }
        
    }


    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }

    public int getLastTransactionId() {
        return lastTransactionId;
    }

    public void setLastTransactionId(int lastTransactionId) {
        this.lastTransactionId = lastTransactionId;
    }


}
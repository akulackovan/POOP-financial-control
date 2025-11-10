package org.model;

import java.time.LocalDateTime;

/**
 *        Класс, представляющий транзакцию
 *
 *        Хранит данные о типе транзакции, времени, id, категории
 */
public class Transaction   {

    private int id; // id
    private String category; // категория (например, "Зарплата", "Продукты")
    private long amount; // сумма транзакции (long из-за выполнения операций)
    private TransactionType transactionType; // тип транзакции
    private LocalDateTime timestamp; // время создания транзакции

    /**
     * Конструктор по умолчанию.
     */
    public Transaction() {
    }

    /**
     * Основной конструктор транзакции.
     * 
     * @param category        Категория транзакции
     * @param amount          Сумма транзакции в копейках
     * @param transactionType Тип транзакции
     */
    public Transaction(String category, long amount, TransactionType transactionType) {
        this.category = category;
        this.amount = amount;
        this.transactionType = transactionType;
        this.timestamp = LocalDateTime.now(); // автоматически ставим текущее время
    }

    /**
     * @return Категория транзакции
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category Категория транзакции
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return Сумма транзакции в копейках
     */
    public long getAmount() {
        return amount;
    }

    /**
     * @param amount Сумма транзакции в копейках
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }

    /**
     * @return Тип транзакции
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * @param transactionType Тип транзакции
     */
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * @return Время создания транзакции
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp Время создания транзакции
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return Уникальный идентификатор транзакции
     */
    public int getId() {
        return id;
    }

    /**
     * @param id Уникальный идентификатор транзакции
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Строковое представление транзакции в формате:
     *         "ID [время] категория: сумма руб. (тип)"
     */
    @Override
    public String toString() {
        String timeStr = timestamp != null ? timestamp.toString() : "без времени";
        String catStr = category != null ? category : "Ошибка получении категории";
        return String.format("%-5d [%s] %-40s: %-10.2f руб. (%s)", id, timeStr,
                catStr, (double) amount / 100, transactionType.getDescription());
    }



}

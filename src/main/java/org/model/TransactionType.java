package org.model;

/**
 * Enum, представляющий типы транзакций.
 * 
 *        Каждый тип транзакции имеет внутреннее имя и описание
 */
public enum TransactionType {

    /** Доход */
    INCOME("income", "Доход"),
    /** Планируемый расход */
    OUTCOME_PLANNED("outcome", "Планируемый расход"),
    /** Фактический расход */
    OUTCOME_SPENT("outcome", "Фактический расход");


    /** Внутреннее имя типа транзакции */
    private final String name;

    /** Описание типа транзакции */
    private final String description;

    /**
     * Конструктор для TransactionType.
     * 
     * @param name Внутреннее имя типа транзакции
     * @param description Описание типа транзакции
     */
    TransactionType(String name, String description) {
        this.description = description;
        this.name = name;
    }

    /**
     * Получить описание типа транзакции.
     * 
     * @return строка с описанием
     */
    public String getDescription() {
        return description;
    }

    /**
     * Получить внутреннее имя типа транзакции.
     * 
     * @return строка с именем
     */
    public String getName() {
        return name;
    }
}

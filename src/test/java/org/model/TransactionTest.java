package org.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TransactionTest {

    // Проверка изменения полей
    @Test
    void testTransactionCreationAndSetters() {
        Transaction transaction = new Transaction("Зарплата", 100000L, TransactionType.INCOME);
            
        assertEquals("Зарплата", transaction.getCategory());
        assertEquals(100000L, transaction.getAmount());
        assertEquals(TransactionType.INCOME, transaction.getTransactionType());
        assertNotNull(transaction.getTimestamp());

        transaction.setCategory("Бонус");
        transaction.setAmount(50000L);
        transaction.setTransactionType(TransactionType.OUTCOME_SPENT);
        transaction.setId(5);

        assertEquals("Бонус", transaction.getCategory());
        assertEquals(50000L, transaction.getAmount());
        assertEquals(TransactionType.OUTCOME_SPENT, transaction.getTransactionType());
        assertEquals(5, transaction.getId());
    }

    // Проверка вывода
    @Test
    void testTransactionToStringFormat() {
        Transaction transaction = new Transaction("Продукты", 250050L, TransactionType.OUTCOME_SPENT);
        transaction.setId(1);

        String result = transaction.toString();

        assertTrue(result.contains("1"));
        assertTrue(result.contains("Продукты"));
        assertTrue(result.matches(".*2500[.,]50.*"),
                "Актуально: " + result);        
        assertTrue(result.contains("Фактический расход"));
    }

}

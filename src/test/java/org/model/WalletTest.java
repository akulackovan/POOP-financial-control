package org.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletTest {
    private Wallet wallet;
    
    @BeforeEach
    void setUp() {
        wallet = new Wallet();
    }
    
    // Проверка на добавление транзакции
    @Test
    void testAddTransaction() {
        Transaction transaction = new Transaction("Зарплата", 100000L, TransactionType.INCOME);
        
        wallet.addTransaction(transaction);
        
        assertEquals(1, wallet.getTransactions().size());
        assertEquals(100000L, wallet.getTotalIncome());
    }
    
    // Добавление дохода с отрицательным доходом
    @Test
    void testAddTransactionWithNegativeAmount() {
        Transaction transaction = new Transaction("Тест", -100L, TransactionType.INCOME);
        
        assertThrows(IllegalArgumentException.class, () -> {
            wallet.addTransaction(transaction);
        });
    }
    
    // Добавление расхода с отрицательным доходом
    @Test
    void testOutcomeTransactionWithNegativeAmount() {
        Transaction transaction = new Transaction("Тест", -100L, TransactionType.OUTCOME_SPENT);
        
        assertThrows(IllegalArgumentException.class, () -> {
            wallet.addTransaction(transaction);
        });
    }

    // Проверка взятия последней записи о бюджете
    @Test
    void testBudgetTransactionRewrite() {
        long planned = wallet.getPlannedOutcomeByCategory().getOrDefault("Тест", 0L);
        assertEquals(0, planned);
        Transaction transaction = new Transaction("Тест", 100L, TransactionType.OUTCOME_PLANNED);
        wallet.addTransaction(transaction);
        planned = wallet.getPlannedOutcomeByCategory().getOrDefault("Тест", 0L);
        assertEquals(100L, planned);

        transaction = new Transaction("Тест", 80L, TransactionType.OUTCOME_PLANNED);
        wallet.addTransaction(transaction);
        planned = wallet.getPlannedOutcomeByCategory().getOrDefault("Тест", 0L);
        assertEquals(80L, planned);
    }

    // Проверка суммирования записей о затратах
    @Test
    void testOucomeTransactionSum() {
        long planned = wallet.getSpentOutcomeByCategory().getOrDefault("Тест", 0L);
        assertEquals(0, planned);
        Transaction transaction = new Transaction("Тест", 100L, TransactionType.OUTCOME_SPENT);
        wallet.addTransaction(transaction);
        planned = wallet.getSpentOutcomeByCategory().getOrDefault("Тест", 0L);
        assertEquals(100L, planned);

        transaction = new Transaction("Тест", 80L, TransactionType.OUTCOME_SPENT);
        wallet.addTransaction(transaction);
        planned = wallet.getSpentOutcomeByCategory().getOrDefault("Тест", 0L);
        assertEquals(180L, planned);
    }

    // Проверка счета транзакций (общий доход)
    @Test
    void testGetIncomeByCategory() {
        wallet.addTransaction(new Transaction("Зарплата", 50000L, TransactionType.INCOME));
        wallet.addTransaction(new Transaction("Зарплата", 30000L, TransactionType.INCOME));
        wallet.addTransaction(new Transaction("Подарок", 20000L, TransactionType.INCOME));
        
        assertEquals(80000L, wallet.getIncomeByCategory().get("Зарплата"));
        assertEquals(20000L, wallet.getIncomeByCategory().get("Подарок"));
    }
    
    // Расходы превысили бюджет в камках одной категории
    @Test
    void testBudgetExceeded() {
        wallet.addTransaction(new Transaction("Продукты", 50000L, TransactionType.OUTCOME_PLANNED));
        wallet.addTransaction(new Transaction("Продукты", 60000L, TransactionType.OUTCOME_SPENT));
        
        assertTrue(wallet.isBudgetExceeded("Продукты"));
    }

    // Проверка выхода за границы дохода
    @Test
    void testBudgetExceededSpent() {
        wallet.addTransaction(new Transaction("Зарплата", 50000L, TransactionType.INCOME));
        wallet.addTransaction(new Transaction("Продукты", 60000L, TransactionType.OUTCOME_SPENT));
        
        assertTrue(wallet.isBudgetExceededSpent());
    }
    
    // Проверка предупреждения о 80%
    @Test
    void testBudgetWarningTrue() {
        long planned = 1000;
        long spent = (long) (planned * 0.8);
        wallet.addTransaction(new Transaction("Продукты", planned, TransactionType.OUTCOME_PLANNED));
        wallet.addTransaction(new Transaction("Продукты", spent, TransactionType.OUTCOME_SPENT));
        
        assertTrue(wallet.isBudgetWarning("Продукты"));
    }

    // Проверка отсутствия предупреждения о 80%
    @Test
    void testBudgetWarningFalse() {
        long planned = 1000;
        long spent = (long) (planned * 0.799);
        System.out.println(spent);
        wallet.addTransaction(new Transaction("Продукты", planned, TransactionType.OUTCOME_PLANNED));
        wallet.addTransaction(new Transaction("Продукты", spent, TransactionType.OUTCOME_SPENT));
        
        assertFalse(wallet.isBudgetWarning("Продукты"));
    }
    
    // Проверка на пустые категории
    @Test
    void testEmptyWalletOperations() {
        assertTrue(wallet.getIncomeCategories().isEmpty());
        assertTrue(wallet.getOutcomeCategories().isEmpty());
        assertEquals(0, wallet.getTotalIncome());
        assertEquals(0, wallet.getTotalOutcomePlanned());
        assertEquals(0, wallet.getTotalOutcomeSpent());
    }

} 

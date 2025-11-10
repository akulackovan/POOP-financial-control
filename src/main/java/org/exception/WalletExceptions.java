package org.exception;

/**
 * Сборник исключений для работы с пользователями, кошельком и транзакциями.
 */
public class WalletExceptions {

    /**
     * Исключение при попытке регистрации существующего пользователя
     */
    public static class UserAlreadyExistsException extends Exception {
        public UserAlreadyExistsException(String username) {
            super("Ошибка: пользователь '" + username + "' уже существует!");
        }
    }

    /**
     * Исключение при входе под несуществующим пользователем
     */
    public static class UserNotFoundException extends Exception {
        public UserNotFoundException(String username) {
            super("Ошибка: пользователь '" + username + "' не найден!");
        }
    }

    /**
     * Исключение при неверном пароле
     */
    public static class WrongPasswordException extends Exception {
        public WrongPasswordException() {
            super("Ошибка: неверный пароль!");
        }
    }

    /**
     * Исключение при создании существующей категории доходов
     */
    public static class IncomeCategoryAlreadyExistsException extends Exception {
        public IncomeCategoryAlreadyExistsException(String categoryName) {
            super("Ошибка: категория доходов '" + categoryName + "' уже существует!");
        }
    }

    /**
     * Исключение при создании существующей категории расходов
     */
    public static class OutcomeCategoryAlreadyExistsException extends Exception {
        public OutcomeCategoryAlreadyExistsException(String categoryName) {
            super("Ошибка: категория расходов '" + categoryName + "' уже существует!");
        }
    }

    /**
     * Исключение при операциях с несуществующей категорией
     */
    public static class CategoryNotFoundException extends Exception {
        public CategoryNotFoundException(String categoryName) {
            super("Ошибка: категория '" + categoryName + "' не найдена!");
        }
    }

    /**
     * Исключение при превышении бюджетных лимитов
     */
    public static class BudgetExceededException extends Exception {
        public BudgetExceededException() {
            super("Отмена операции из-за превышения расходов.");
        }
    }

    /**
     * Исключение при попытке операции с отрицательной суммой
     */
    public static class NegativeAmountException extends Exception {
        public NegativeAmountException(String context) {
            super("Ошибка: сумма " + context + " не может быть отрицательной!");
        }
    }

    /**
     * Исключение при транзакции с несуществующей категорией
     */
    public static class TransactionCategoryNotFoundException extends Exception {
        public TransactionCategoryNotFoundException(String category) {
            super("Ошибка: для транзакции категория '" + category + "' не найдена!");
        }
    }

    /**
     * Исключение при удалении/поиске несуществующей транзакции
     */
    public static class TransactionNotFoundException extends Exception {
        public TransactionNotFoundException(String id) {
            super("Ошибка: транзакция с ID '" + id + "' не найдена!");
        }
    }
}
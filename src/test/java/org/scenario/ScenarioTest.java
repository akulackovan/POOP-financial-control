package org.scenario;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.model.User;
import org.service.AuthService;
import org.service.UserService;
import org.service.WalletService;

class ScenarioTest {

    private static final String TEST_USER_FILE = "data/test_users.json";
    private UserService testUserService;
    private AuthService testAuthService;
    private WalletService walletService;

    @BeforeEach
    void setUp() throws IOException {
        // Очищаем перед созданием
        cleanupTestFiles();

        // Инициализация сервисов с тестовым файлом
        testUserService = new UserService(TEST_USER_FILE);
        testAuthService = new AuthService(testUserService);
        walletService = new WalletService();
    }

    @AfterEach
    void tearDown() {
        cleanupTestFiles();
    }

    private void cleanupTestFiles() {
        File file = new File(TEST_USER_FILE);
        if (file.exists()) {
            file.delete();
            System.out.println("Удален файл: " + TEST_USER_FILE);

        }
    }

    // Проверка добавления категорий с пользователем и кошельком
    @Test
    void testScanarioUserWithWallet() throws Exception {
        String username = "user";
        String password = "user";

        User user = new User(username, password);
        testAuthService.addUser(user);

        User loginUser = testAuthService.login(username, password);

        assertNotNull(loginUser);

        walletService.addIncome(user, "Зарплата", 10000L);
        walletService.addSpentOutcome(user, "Еда", 2000L);
        walletService.addPlannedOutcome(user, "Еда", 5000L);

        assertTrue(user.getWallet().getTotalIncome() == 10000L);
        System.err.println(user.getWallet().getRemainingBudget("Еда"));
        assertTrue(user.getWallet().getRemainingBudget("Еда") == 3000L);

    }
}
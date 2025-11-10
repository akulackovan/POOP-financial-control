package org.service;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.model.User;

class AuthServiceTest {

    private static final String TEST_USER_FILE = "data/test_users.json";
    private UserService testUserService;
    private AuthService testAuthService;

    @BeforeEach
    void setUp() throws IOException {
        // Очищаем перед созданием
        cleanupTestFiles();

        // Инициализация сервисов с тестовым файлом
        testUserService = new UserService(TEST_USER_FILE);
        testAuthService = new AuthService(testUserService);
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

    // Проверка успешного входа за пользователя
    @Test
    void getLoginSuccess() throws Exception {
        String username = "user";
        String password = "user";

        User user = new User(username, password);
        testAuthService.addUser(user);

        assertNotEquals(password, user.getPassword()); // Исправлено: было "plainpassword"
        assertNotNull(user.getSalt());

        assertTrue(AuthService.verifyPassword(password,
                user.getPassword(), user.getSalt()));

        User loginUser = testAuthService.login(username, password);

        assertNotNull(loginUser);
        assertNotEquals(password, loginUser.getPassword()); // Исправлено
        assertNotNull(loginUser.getSalt());
    }

    // Проверка хеширования пароля
    @Test
    void testAddUserHashesPassword() throws Exception {
        String username = "user";
        String password = "user";

        User user = new User(username, password);
        testAuthService.addUser(user);

        assertNotEquals(password, user.getPassword());
        assertNotNull(user.getSalt());
        assertTrue(AuthService.verifyPassword(password, user.getPassword(), user.getSalt()));
    }
}
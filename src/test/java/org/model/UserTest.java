package org.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class UserTest {

    // Проверка изменения полей
    @Test
    void testUserCreationAndPasswordChange() {
        Wallet wallet = new Wallet();
        User user = new User("testUser", "password123", wallet);

        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals(wallet, user.getWallet());

        user.changePassword("newHash", "newSalt");
        assertEquals("newHash", user.getPassword());
        assertEquals("newSalt", user.getSalt());
    }

}

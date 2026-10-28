package com.chatsphere.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chatsphere.model.UserAccount;
import com.chatsphere.repository.UserAccountRepository;

@Service
public class AuthService {
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;

    private final UserAccountRepository users;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserAccountRepository users) {
        this.users = users;
    }

    public String register(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        String cleanUsername = username.trim();
        if (!cleanUsername.matches("[A-Za-z0-9_]{3,20}")) {
            throw new IllegalArgumentException("Username must be 3-20 characters and contain only letters, numbers, or underscores.");
        }
        if (password.length() < 8 || password.length() > 128) {
            throw new IllegalArgumentException("Password must contain 8-128 characters.");
        }

        String key = cleanUsername.toLowerCase(Locale.ROOT);
        if (users.existsByUsernameKey(key)) {
            throw new IllegalArgumentException("This username is already registered.");
        }
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        UserAccount account = new UserAccount(cleanUsername, key,
                Base64.getEncoder().encodeToString(salt), hashPassword(password, salt));
        try {
            users.saveAndFlush(account);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("This username is already registered.");
        }
        return cleanUsername;
    }

    @Transactional(readOnly = true)
    public String authenticate(String username, String password) {
        if (username == null || password == null) return null;
        String key = username.trim().toLowerCase(Locale.ROOT);
        UserAccount account = users.findByUsernameKey(key).orElse(null);
        if (account == null) return null;
        byte[] salt;
        try {
            salt = Base64.getDecoder().decode(account.getSalt());
        } catch (IllegalArgumentException ex) {
            return null;
        }
        String candidateHash = hashPassword(password, salt);
        boolean valid = MessageDigest.isEqual(
                candidateHash.getBytes(StandardCharsets.UTF_8),
                account.getPasswordHash().getBytes(StandardCharsets.UTF_8));
        return valid ? account.getUsername() : null;
    }

    private String hashPassword(String password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to process password securely.", ex);
        } finally {
            spec.clearPassword();
        }
    }
}

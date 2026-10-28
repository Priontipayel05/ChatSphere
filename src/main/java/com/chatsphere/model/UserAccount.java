package com.chatsphere.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username_key", columnNames = "username_key")
})
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(name = "username_key", nullable = false, length = 20)
    private String usernameKey;

    @Column(name = "password_salt", nullable = false, length = 64)
    private String salt;

    @Column(name = "password_hash", nullable = false, length = 128)
    private String passwordHash;

    @Column(name = "display_name", length = 40)
    private String displayName;

    @Column(name = "status_message", length = 120)
    private String statusMessage;

    @Column(name = "avatar_color", length = 20)
    private String avatarColor = "#7764ee";

    protected UserAccount() { }

    public UserAccount(String username, String usernameKey, String salt, String passwordHash) {
        this.username = username;
        this.usernameKey = usernameKey;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public String getSalt() { return salt; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName == null || displayName.isBlank() ? username : displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getStatusMessage() { return statusMessage == null ? "Ready to connect" : statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
    public String getAvatarColor() { return avatarColor == null ? "#7764ee" : avatarColor; }
    public void setAvatarColor(String avatarColor) { this.avatarColor = avatarColor; }
}

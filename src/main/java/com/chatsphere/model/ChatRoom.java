package com.chatsphere.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "chat_rooms", uniqueConstraints = @UniqueConstraint(name = "uk_chat_rooms_name", columnNames = "name_key"))
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 40) private String name;
    @Column(name = "name_key", nullable = false, length = 40) private String nameKey;
    @Column(length = 120) private String description;
    @Column(name = "created_by", nullable = false, length = 20) private String createdBy;
    @Column(name = "created_at", nullable = false) private Long createdAt;
    protected ChatRoom() {}
    public ChatRoom(String name, String nameKey, String description, String createdBy, Long createdAt) {
        this.name=name; this.nameKey=nameKey; this.description=description; this.createdBy=createdBy; this.createdAt=createdAt;
    }
    public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;}
    public String getCreatedBy(){return createdBy;} public Long getCreatedAt(){return createdAt;}
}

package com.chatsphere.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_room_timestamp", columnList = "room_name, timestamp")
})
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender", nullable = false, length = 20)
    private String sender;

    @Column(name = "sender_display_name", length = 40)
    private String senderDisplayName;

    @Column(name = "sender_avatar_color", length = 20)
    private String senderAvatarColor;

    @Column(name = "content", nullable = false, length = 4000)
    private String content;

    @Column(name = "room_name", nullable = false, length = 60)
    private String room;

    @Column(name = "message_type", nullable = false, length = 20)
    private String type;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;

    @Column(name = "reply_to_id")
    private Long replyToId;

    @Column(name = "reply_to_sender", length = 20)
    private String replyToSender;

    @Column(name = "reply_to_content", length = 300)
    private String replyToContent;

    @Column(name = "edited", nullable = false, columnDefinition = "boolean default false")
    private boolean edited = false;

    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    public ChatMessage() { }

    public ChatMessage(String sender, String content, String room, String type, Long timestamp) {
        this.sender = sender;
        this.content = content;
        this.room = room;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public String getSender() { return sender; }
    public String getSenderDisplayName() { return senderDisplayName == null || senderDisplayName.isBlank() ? sender : senderDisplayName; }
    public void setSenderDisplayName(String senderDisplayName) { this.senderDisplayName = senderDisplayName; }
    public String getSenderAvatarColor() { return senderAvatarColor == null ? "#7764ee" : senderAvatarColor; }
    public void setSenderAvatarColor(String senderAvatarColor) { this.senderAvatarColor = senderAvatarColor; }
    public void setSender(String sender) { this.sender = sender; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
    public String getReplyToSender() { return replyToSender; }
    public void setReplyToSender(String replyToSender) { this.replyToSender = replyToSender; }
    public String getReplyToContent() { return replyToContent; }
    public void setReplyToContent(String replyToContent) { this.replyToContent = replyToContent; }
    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}

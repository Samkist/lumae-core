package net.lumae.LumaeCore.storage;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity(value = "message", useDiscriminator = false)
public class Message {

    @Id
    String messageId;
    String message;

    public Message(String messageId, String message) {
        this.messageId = messageId;
        this.message = message;
    }

    public Message() {

    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

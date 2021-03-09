package net.lumae.LumaeCore.storage;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity(value = "message", useDiscriminator = false)
public class Message {

    @Id
    String messageId;
    String message;

    public Message() {

    }
}

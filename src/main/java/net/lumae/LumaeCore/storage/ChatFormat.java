package net.lumae.LumaeCore.storage;

import dev.morphia.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity(value = "ChatFormats", useDiscriminator = false)
public class ChatFormat {

	@Id
	String name;
	String permission;
	String messageFormat;
	Integer priority;

	public ChatFormat() {

	}

}

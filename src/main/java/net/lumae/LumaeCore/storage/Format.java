package net.lumae.LumaeCore.storage;

import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class Format {

	@Id
	String name;
    String permission;
	String messageFormat;
	Integer priority;

}

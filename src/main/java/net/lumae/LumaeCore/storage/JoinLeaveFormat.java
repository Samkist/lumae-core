package net.lumae.LumaeCore.storage;

import dev.morphia.annotations.Entity;

@Entity(value = "joinLeaveFormats", useDiscriminator = false)
public class JoinLeaveFormat extends Format {

	public JoinLeaveFormat() {

	}

	public JoinLeaveFormat(String name, String permission, String messageFormat, Integer priority) {
		super(name, permission, messageFormat, priority);
	}

}

package net.lumae.LumaeCore.storage;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConfigDefaults {
	private final String playerDataCollection;
	private final String chatFormatsCollection;
	private final ChatFormat defaultChatFormat;
	private final JoinLeaveFormat defaultJoinLeaveFormat;
}

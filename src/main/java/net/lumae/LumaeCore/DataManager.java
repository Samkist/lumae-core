package net.lumae.LumaeCore;

import net.lumae.LumaeCore.storage.ChatFormat;
import net.lumae.LumaeCore.storage.JoinLeaveFormat;
import net.lumae.LumaeCore.storage.Message;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class DataManager {

	private static final Lumae plugin = JavaPlugin.getPlugin(Lumae.class);
	private final FileManager fileManager;
	private final DBManager dbManager;
	private final Map<UUID, PlayerData> playerDataMap;
	private final Map<String, Message> pluginMessages;
	private final List<ChatFormat> chatFormats;
	private final List<JoinLeaveFormat> joinLeaveFormats;
	public DataManager(FileManager fileManager, DBManager dbManager) {
		this.fileManager = fileManager;
		this.dbManager = dbManager;
		this.playerDataMap = new HashMap<>();
		this.pluginMessages = new HashMap<>();
		this.chatFormats = loadChatFormats();
		this.joinLeaveFormats = loadJoinLeaveFormats();
		initialize();
	}

	private void initialize() {
		final List<Message> msgs = dbManager.loadMessages();
		msgs.forEach(m -> pluginMessages.put(m.getMessageId(), m));

	}

	public void saveAllPlayers(Map<UUID, PlayerData> playerData) {
		dbManager.saveAllPlayers(playerData);
	}

	public void savePlayerData(Player player, PlayerData playerData) {
		dbManager.savePlayerData(player,playerData);
	}

	public String topPlayerByField(String field) {
		return dbManager.topPlayerByField(field);
	}

	public Optional<PlayerData> loadPlayerData(Player player) {
		return dbManager.loadPlayerData(player);
	}

	public void initializePlayerData(Player player) {
		dbManager.initializePlayerData(player);
	}

	public PlayerData fetchPlayerData(Player player) {
		return playerDataMap.get(player.getUniqueId());
	}

	public PlayerData fetchPlayerData(UUID uuid) {
		return playerDataMap.get(uuid);
	}

	public List<ChatFormat> loadChatFormats() {
		return dbManager.loadChatFormats();
	}

	public List<JoinLeaveFormat> loadJoinLeaveFormats() {
		return dbManager.loadJoinLeaveFormats();
	}

	public Optional<Message> messageById(String id) {
		return Optional.ofNullable(pluginMessages.get(id));
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public DBManager getDbManager() {
		return dbManager;
	}

	public Map<UUID, PlayerData> getPlayerDataMap() {
		return playerDataMap;
	}

	public Map<String, Message> getPluginMessages() {
		return pluginMessages;
	}

	public List<ChatFormat> getChatFormats() {
		return chatFormats;
	}

	public List<JoinLeaveFormat> getJoinLeaveFormats() {
		return joinLeaveFormats;
	}
}

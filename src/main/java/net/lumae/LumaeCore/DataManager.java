package net.lumae.LumaeCore;

import lombok.Getter;
import lombok.NonNull;
import net.lumae.LumaeCore.storage.ChatFormat;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
public class DataManager {

	private static final Lumae plugin = JavaPlugin.getPlugin(Lumae.class);
	private final FileManager fileManager;
	private final DBManager dbManager;
	private final Map<UUID, PlayerData> playerDataMap;
	private final List<ChatFormat> chatFormats;

	public DataManager(FileManager fileManager, DBManager dbManager) {
		this.fileManager = fileManager;
		this.dbManager = dbManager;
		this.playerDataMap = new HashMap<>();
		this.chatFormats = loadChatFormats();
	}

	public @NonNull void saveAllPlayers(Map<UUID, PlayerData> playerData) {
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


}

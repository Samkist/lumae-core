package net.lumae.LumaeCore;

import lombok.Getter;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DataManager {

	private final FileManager fileManager;
	private final DBManager dbManager;
	@Getter
	private final Map<UUID, PlayerData> playerDataMap;

	public DataManager(FileManager fileManager, DBManager dbManager) {
		this.fileManager = fileManager;
		this.dbManager = dbManager;
		this.playerDataMap = new HashMap<>();
	}

	public void saveAllPlayers(Map<Player, PlayerData> playerData) {
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

	public void initializePlayerData(Player player, PlayerData playerData) {
		dbManager.initializePlayerData(player,playerData);
	}


}

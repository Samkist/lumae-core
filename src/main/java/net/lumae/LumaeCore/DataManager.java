package net.lumae.LumaeCore;

import com.mongodb.bulk.BulkWriteResult;
import lombok.Getter;
import lombok.NonNull;
import net.lumae.LumaeCore.storage.ChatFormat;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bukkit.entity.Player;

import java.util.*;

public class DataManager {

	private final FileManager fileManager;
	private final DBManager dbManager;
	@Getter
	private final Map<UUID, PlayerData> playerDataMap;

	public DataManager(FileManager fileManager, DBManager dbManager) {
		this.fileManager = fileManager;
		this.dbManager = dbManager;
		this.playerDataMap = new HashMap<>();
		initialize();
	}

	private void initialize() {
		dbManager.initializeChatFormats(new ChatFormat("default", "lumae.chat.default",
				"%luckperms_prefix% %player_displayname% &8»&7 %lumae_message%", 99999));
	}

	public @NonNull Optional<BulkWriteResult> saveAllPlayers(Map<UUID, PlayerData> playerData) {
		return dbManager.saveAllPlayers(playerData);
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

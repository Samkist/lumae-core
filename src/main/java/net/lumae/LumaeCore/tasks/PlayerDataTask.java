package net.lumae.LumaeCore.tasks;

import net.lumae.LumaeCore.DataManager;
import net.lumae.LumaeCore.Lumae;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PlayerDataTask implements Runnable {

	private static final Lumae plugin = JavaPlugin.getPlugin(Lumae.class);
	private final DataManager dataManager = plugin.getDataManager();

	@Override
	public void run() {
		calculatePlayTime();
		saveAllPlayers();
	}

	private void calculatePlayTime() {
		final Iterator<Map.Entry<UUID, PlayerData>> it = dataManager.getPlayerDataMap().entrySet().iterator();
		while(it.hasNext()) {
			final PlayerData playerData = it.next().getValue();
			playerData.setSecondsPlayed(playerData.getSecondsPlayed() + 300);
		}
	}


	private void saveAllPlayers() {
		try {
			dataManager.saveAllPlayers(dataManager.getPlayerDataMap());
		} catch (Exception e) {
			plugin.getLogger().severe("Fatal error, unable to bulk write to database!");
			e.printStackTrace();
			return;
		}
		plugin.getLogger().info("Player data was automatically saved.");
	}
}

package net.lumae.LumaeCore.tasks;

import lombok.extern.java.Log;
import lombok.val;
import net.lumae.LumaeCore.DataManager;
import net.lumae.LumaeCore.Lumae;
import org.bukkit.plugin.java.JavaPlugin;

@Log(topic="Lumae - PlayerDataSaveTask")
public class PlayerDataTask implements Runnable {

	private static final Lumae plugin = JavaPlugin.getPlugin(Lumae.class);
	private final DataManager dataManager = plugin.getDataManager();

	@Override
	public void run() {
		calculatePlayTime();
		saveAllPlayers();
	}

	private void calculatePlayTime() {
		val it = dataManager.getPlayerDataMap().entrySet().iterator();
		while(it.hasNext()) {
			val playerData = it.next().getValue();
			playerData.setSecondsPlayed(playerData.getSecondsPlayed() + 300);
		}
	}


	private void saveAllPlayers() {
		try {
			dataManager.saveAllPlayers(dataManager.getPlayerDataMap());
		} catch (Exception e) {
			log.severe("Fatal error, unable to bulk write to database!");
			e.printStackTrace();
			return;
		}
		log.info("Player data was automatically saved.");
	}
}

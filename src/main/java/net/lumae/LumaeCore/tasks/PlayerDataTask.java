package net.lumae.LumaeCore.tasks;

import com.mongodb.bulk.BulkWriteResult;
import lombok.extern.java.Log;
import lombok.val;
import net.lumae.LumaeCore.DataManager;
import net.lumae.LumaeCore.Lumae;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

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
		Optional<BulkWriteResult> oResult = dataManager.saveAllPlayers(dataManager.getPlayerDataMap());
		if(oResult.isPresent()) {
			BulkWriteResult result = oResult.get();
			if(result.wasAcknowledged()) {
				log.info("Player data was automatically saved.");
			} else {
				log.severe("Fatal error, bulk write was not acknowledged by database!");
			}
		} else {
			log.severe("Fatal error, unable to bulk write to database!");
		}
	}
}

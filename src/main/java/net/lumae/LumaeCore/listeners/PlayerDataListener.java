package net.lumae.LumaeCore.listeners;

import lombok.AllArgsConstructor;
import lombok.val;
import net.lumae.LumaeCore.DataManager;
import net.lumae.LumaeCore.Lumae;
import net.lumae.LumaeCore.storage.DatabasePlayerData;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class PlayerDataListener implements Listener {

	private final Lumae plugin;
	private final Map<UUID, PlayerData> playerDataMap;
	private final DataManager dataManager;


	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Optional<PlayerData> playerData = dataManager.loadPlayerData(player);
		if(playerData.isPresent()) {
			playerDataMap.put(player.getUniqueId(), playerData.get());
		} else {
			val freshPlayerData = new DatabasePlayerData(player);
			dataManager.initializePlayerData(player, freshPlayerData);
			playerDataMap.put(player.getUniqueId(), freshPlayerData);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		dataManager.savePlayerData(player, playerDataMap.get(player.getUniqueId()));
		playerDataMap.remove(player.getUniqueId());
	}
}
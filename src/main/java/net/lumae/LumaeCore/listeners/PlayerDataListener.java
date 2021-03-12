package net.lumae.LumaeCore.listeners;

import lombok.AllArgsConstructor;
import lombok.val;
import net.lumae.LumaeCore.DataManager;
import net.lumae.LumaeCore.Lumae;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class PlayerDataListener implements Listener {

	private final Lumae plugin;
	private final Map<UUID, PlayerData> playerDataMap;
	private final DataManager dataManager;


	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		val player = event.getPlayer();
		Optional<PlayerData> playerData = dataManager.loadPlayerData(player);
		playerData.ifPresentOrElse(
				data -> playerDataMap.put(player.getUniqueId(), data),
				() -> dataManager.initializePlayerData(player)
		);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		val secondsToAdd = (int) (((System.currentTimeMillis() - Lumae.LAST_START_TIME) / 1000) % 300);
		val playerData = dataManager.fetchPlayerData(player);
		playerData.setSecondsPlayed(playerData.getSecondsPlayed() + secondsToAdd);
		dataManager.savePlayerData(player, playerDataMap.get(player.getUniqueId()));
		playerDataMap.remove(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		val victimData = playerDataMap.get(event.getEntity().getUniqueId());
		victimData.setDeaths(victimData.getDeaths() + 1);

		val killer = event.getEntity().getKiller();
		if(Objects.nonNull(killer)) {
			val killerData = dataManager.fetchPlayerData(killer);
			killerData.setPlayerKills(killerData.getPlayerKills() + 1);
		}
	}

	@EventHandler
	public void onMobKill(EntityDeathEvent event) {
		if(event.getEntity() instanceof Player) {
			return;
		}

		val player = event.getEntity().getKiller();
		if(Objects.nonNull(player)) {
			val playerData = dataManager.fetchPlayerData(player);
			playerData.setMobKills(playerData.getMobKills() + 1);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		val player = event.getPlayer();

		if(Objects.nonNull(player)) {
			val playerData = dataManager.fetchPlayerData(player);
			playerData.setBlocksMined(playerData.getBlocksMined() + 1);
		}
	}
}
package net.lumae.LumaeCore.listeners;

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

public class PlayerDataListener implements Listener {

	private final Lumae plugin;
	private final Map<UUID, PlayerData> playerDataMap;
	private final DataManager dataManager;

	public PlayerDataListener(Lumae plugin, Map<UUID, PlayerData> playerDataMap, DataManager dataManager) {
		this.plugin = plugin;
		this.playerDataMap = playerDataMap;
		this.dataManager = dataManager;
	}


	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Optional<PlayerData> playerData = dataManager.loadPlayerData(player);
		playerDataMap.put(player.getUniqueId(), playerData.get());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final int secondsToAdd = (int) (((System.currentTimeMillis() - Lumae.LAST_START_TIME) / 1000) % 300);
		final PlayerData playerData = dataManager.fetchPlayerData(player);
		playerData.setSecondsPlayed(playerData.getSecondsPlayed() + secondsToAdd);
		dataManager.savePlayerData(player, playerDataMap.get(player.getUniqueId()));
		playerDataMap.remove(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		final PlayerData victimData = playerDataMap.get(event.getEntity().getUniqueId());
		victimData.setDeaths(victimData.getDeaths() + 1);

		final Player killer = event.getEntity().getKiller();
		if(Objects.nonNull(killer)) {
			final PlayerData killerData = dataManager.fetchPlayerData(killer);
			killerData.setPlayerKills(killerData.getPlayerKills() + 1);
		}
	}

	@EventHandler
	public void onMobKill(EntityDeathEvent event) {
		if(event.getEntity() instanceof Player) {
			return;
		}

		final Player player = event.getEntity().getKiller();
		if(Objects.nonNull(player)) {
			final PlayerData playerData = dataManager.fetchPlayerData(player);
			playerData.setMobKills(playerData.getMobKills() + 1);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		final Player player = event.getPlayer();

		if(Objects.nonNull(player)) {
			final PlayerData playerData = dataManager.fetchPlayerData(player);
			playerData.setBlocksMined(playerData.getBlocksMined() + 1);
		}
	}
}
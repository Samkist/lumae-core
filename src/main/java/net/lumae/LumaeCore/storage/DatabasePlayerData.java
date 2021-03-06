package net.lumae.LumaeCore.storage;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class DatabasePlayerData extends PlayerData {

	private Player player;
	private String uuid;

	public DatabasePlayerData(Player player, BigDecimal balance, Double lumiumBalance, Map<String, Location> homes,
							  Integer votes, Integer playerKills, Integer deaths, Integer mobKills, Integer blocksMined,
							  Integer secondsPlayed, String chatColor, String currentName, Map<String, Cooldown> kitCooldowns) {
		super(balance, lumiumBalance, homes, votes, playerKills, deaths, mobKills, blocksMined, secondsPlayed, chatColor, currentName, kitCooldowns);
		applyPlayer(player);
	}

	public DatabasePlayerData(Player player) {
		super(BigDecimal.valueOf(0), 0D, new HashMap<>(), 0, 0, 0, 0,
				0, 0, "", player.getName(), new HashMap<>());
		applyPlayer(player);
	}

	public DatabasePlayerData(Player player, PlayerData playerData) {
		super(playerData.getBalance(), playerData.getLumiumBalance(), playerData.getHomes(), playerData.getVotes(),
				playerData.getPlayerKills(), playerData.getDeaths(), playerData.getMobKills(),
				playerData.getBlocksMined(), playerData.getSecondsPlayed(), playerData.getChatColor(), playerData.getCurrentName(), playerData.getKitCooldowns());
		applyPlayer(player);
	}

	private void applyPlayer(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId().toString();
	}
}

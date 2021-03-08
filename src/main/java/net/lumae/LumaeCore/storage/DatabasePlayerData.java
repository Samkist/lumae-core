package net.lumae.LumaeCore.storage;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.Decimal128;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class DatabasePlayerData extends PlayerData {

	private String uuid;

	public DatabasePlayerData(Player player, Decimal128 balance, Double lumiumBalance,
							  Integer votes, Integer playerKills, Integer deaths, Integer mobKills, Integer blocksMined,
							  Integer secondsPlayed, String chatColor, String currentName, Map<String, Location> homes, Map<String, Cooldown> kitCooldowns) {
		super(balance, lumiumBalance, votes, playerKills, deaths, mobKills, blocksMined, secondsPlayed, chatColor, currentName, homes, kitCooldowns);
		applyPlayer(player);
	}

	public DatabasePlayerData(String uuid, Decimal128 balance, Double lumiumBalance,
							  Integer votes, Integer playerKills, Integer deaths, Integer mobKills, Integer blocksMined,
							  Integer secondsPlayed, String chatColor, String currentName, Map<String, Location> homes, Map<String, Cooldown> kitCooldowns) {
		super(balance, lumiumBalance, votes, playerKills, deaths, mobKills, blocksMined, secondsPlayed, chatColor, currentName, homes, kitCooldowns);
		this.uuid = uuid;
	}

	public DatabasePlayerData(Player player) {
		super(Decimal128.parse("0"), 0D, 0, 0, 0, 0,
				0, 0, "", player.getName(), new HashMap<>(), new HashMap<>());
		applyPlayer(player);
	}

	public DatabasePlayerData(Player player, PlayerData playerData) {
		super(playerData.getBalance(), playerData.getLumiumBalance(),  playerData.getVotes(),
				playerData.getPlayerKills(), playerData.getDeaths(), playerData.getMobKills(),
				playerData.getBlocksMined(), playerData.getSecondsPlayed(), playerData.getChatColor(), playerData.getCurrentName(), playerData.getHomes(), playerData.getKitCooldowns());
		applyPlayer(player);
	}

	private void applyPlayer(Player player) {
		this.uuid = player.getUniqueId().toString();
	}
}

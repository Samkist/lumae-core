package net.lumae.LumaeCore.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public abstract class PlayerData {

	private BigDecimal balance;
	private Double lumiumBalance;
	private Map<String, Location> homes;
	private Integer votes;
	private Integer playerKills;
	private Integer deaths;
	private Integer mobKills;
	private Integer blocksMined;
	private Integer secondsPlayed;
	private String chatColor;
	private String currentName;
	private Map<String, Cooldown> kitCooldowns;

}

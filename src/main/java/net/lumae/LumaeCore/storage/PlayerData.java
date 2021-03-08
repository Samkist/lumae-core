package net.lumae.LumaeCore.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@BsonDiscriminator
public abstract class PlayerData {

	private BigDecimal balance;
	private Double lumiumBalance;
	private Integer votes;
	private Integer playerKills;
	private Integer deaths;
	private Integer mobKills;
	private Integer blocksMined;
	private Integer secondsPlayed;
	private String chatColor;
	private String currentName;
	private Map<String, Location> homes;
	private Map<String, Cooldown> kitCooldowns;

}

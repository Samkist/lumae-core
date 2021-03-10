package net.lumae.LumaeCore.storage;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.Decimal128;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@Entity(value = "playerData", useDiscriminator = false)
public class PlayerData {

	@Id
	private String uuid;
	private Player player;
	@Reference(lazy = true)
	private ChatFormat chatFormat;
	private Decimal128 balance;
	private Double lumiumBalance;
	private Integer votes;
	private Integer playerKills;
	private Integer deaths;
	private Integer mobKills;
	private Integer blocksMined;
	private Integer secondsPlayed;
	private Map<String, Location> homes;
	private Map<String, Cooldown> kitCooldowns;
	private Date joinDate;

}

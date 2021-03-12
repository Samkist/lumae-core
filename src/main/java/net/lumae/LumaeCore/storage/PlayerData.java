package net.lumae.LumaeCore.storage;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import net.lumae.LumaeCore.Lumae;
import net.lumae.LumaeCore.listeners.JoinLeaveListener;
import org.bson.types.Decimal128;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Entity(value = "playerData", useDiscriminator = false)
public class PlayerData {

	private static final Lumae plugin = JavaPlugin.getPlugin(Lumae.class);

	@Id
	private String uuid;
	private Player player;
	@Reference(lazy = true)
	private ChatFormat chatFormat;
	private JoinLeaveFormat joinLeaveFormat;
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

	public PlayerData(Player player) {
		val dataManager = plugin.getDataManager();
		this.uuid = player.getUniqueId().toString();
		this.player = player;
		this.chatFormat = dataManager.loadChatFormats().stream().filter(c ->
				c.getName().equalsIgnoreCase("default")).findFirst().get();
		this.joinLeaveFormat = null;
		this.balance = new Decimal128(0);
		this.lumiumBalance = 0D;
		this.votes = 0;
		this.playerKills = 0;
		this.deaths = 0;
		this.mobKills = 0;
		this.blocksMined = 0;
		this.secondsPlayed = 0;
		this.homes = new HashMap<>();
		this.kitCooldowns = new HashMap<>();
		this.joinDate = Calendar.getInstance().getTime();
	}

}

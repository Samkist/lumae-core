package net.lumae.LumaeCore.storage;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import net.lumae.LumaeCore.DataManager;
import net.lumae.LumaeCore.Lumae;
import org.bson.types.Decimal128;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity(value = "playerData", useDiscriminator = false)
public class PlayerData {

	private static final Lumae plugin = JavaPlugin.getPlugin(Lumae.class);

	@Id
	private String uuid;
	@Reference(lazy = true)
	private ChatFormat chatFormat;
	@Reference(lazy = true)
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
		final DataManager dataManager = plugin.getDataManager();
		this.uuid = player.getUniqueId().toString();
		this.chatFormat = dataManager.loadChatFormats().stream().filter(c ->
				c.getName().equalsIgnoreCase("default")).findFirst().get();
		this.joinLeaveFormat = dataManager.loadJoinLeaveFormats().stream().filter(j ->
				j.getName().equalsIgnoreCase("default")).findFirst().get();
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

	//DUMMY CONSTRUCTOR
	public PlayerData() {

	}

	public PlayerData(String uuid, ChatFormat chatFormat, JoinLeaveFormat joinLeaveFormat, Decimal128 balance, Double lumiumBalance, Integer votes,
					  Integer playerKills, Integer deaths, Integer mobKills, Integer blocksMined, Integer secondsPlayed, Map<String, Location> homes,
					  Map<String, Cooldown> kitCooldowns, Date joinDate) {
		this.uuid = uuid;
		this.chatFormat = chatFormat;
		this.joinLeaveFormat = joinLeaveFormat;
		this.balance = balance;
		this.lumiumBalance = lumiumBalance;
		this.votes = votes;
		this.playerKills = playerKills;
		this.deaths = deaths;
		this.mobKills = mobKills;
		this.blocksMined = blocksMined;
		this.secondsPlayed = secondsPlayed;
		this.homes = homes;
		this.kitCooldowns = kitCooldowns;
		this.joinDate = joinDate;
	}

	public static Lumae getPlugin() {
		return plugin;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ChatFormat getChatFormat() {
		return chatFormat;
	}

	public void setChatFormat(ChatFormat chatFormat) {
		this.chatFormat = chatFormat;
	}

	public JoinLeaveFormat getJoinLeaveFormat() {
		return joinLeaveFormat;
	}

	public void setJoinLeaveFormat(JoinLeaveFormat joinLeaveFormat) {
		this.joinLeaveFormat = joinLeaveFormat;
	}

	public Decimal128 getBalance() {
		return balance;
	}

	public void setBalance(Decimal128 balance) {
		this.balance = balance;
	}

	public Double getLumiumBalance() {
		return lumiumBalance;
	}

	public void setLumiumBalance(Double lumiumBalance) {
		this.lumiumBalance = lumiumBalance;
	}

	public Integer getVotes() {
		return votes;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}

	public Integer getPlayerKills() {
		return playerKills;
	}

	public void setPlayerKills(Integer playerKills) {
		this.playerKills = playerKills;
	}

	public Integer getDeaths() {
		return deaths;
	}

	public void setDeaths(Integer deaths) {
		this.deaths = deaths;
	}

	public Integer getMobKills() {
		return mobKills;
	}

	public void setMobKills(Integer mobKills) {
		this.mobKills = mobKills;
	}

	public Integer getBlocksMined() {
		return blocksMined;
	}

	public void setBlocksMined(Integer blocksMined) {
		this.blocksMined = blocksMined;
	}

	public Integer getSecondsPlayed() {
		return secondsPlayed;
	}

	public void setSecondsPlayed(Integer secondsPlayed) {
		this.secondsPlayed = secondsPlayed;
	}

	public Map<String, Location> getHomes() {
		return homes;
	}

	public void setHomes(Map<String, Location> homes) {
		this.homes = homes;
	}

	public Map<String, Cooldown> getKitCooldowns() {
		return kitCooldowns;
	}

	public void setKitCooldowns(Map<String, Cooldown> kitCooldowns) {
		this.kitCooldowns = kitCooldowns;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
}

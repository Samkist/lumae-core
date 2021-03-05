package net.lumae.LumaeCore.storage;

import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.Map;

public class PlayerData {

	private BigDecimal balance;
	private Double premiumBalance;
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

	public PlayerData(BigDecimal balance, Double premiumBalance, Map<String, Location> homes, Integer votes, Integer playerKills, Integer deaths,
					  Integer mobKills, Integer blocksMined, Integer secondsPlayed, String chatColor, String currentName, Map<String, Cooldown> kitCooldowns) {
		this.balance = balance;
		this.premiumBalance = premiumBalance;
		this.homes = homes;
		this.votes = votes;
		this.playerKills = playerKills;
		this.deaths = deaths;
		this.mobKills = mobKills;
		this.blocksMined = blocksMined;
		this.secondsPlayed = secondsPlayed;
		this.chatColor = chatColor;
		this.currentName = currentName;
		this.kitCooldowns = kitCooldowns;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Double getPremiumBalance() {
		return premiumBalance;
	}

	public void setPremiumBalance(Double premiumBalance) {
		this.premiumBalance = premiumBalance;
	}

	public Map<String, Location> getHomes() {
		return homes;
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

	public String getChatColor() {
		return chatColor;
	}

	public void setChatColor(String chatColor) {
		this.chatColor = chatColor;
	}

	public String getCurrentName() {
		return currentName;
	}

	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}

	public Map<String, Cooldown> getKitCooldowns() {
		return kitCooldowns;
	}
}

package net.lumae.LumaeCore.listeners;

import lombok.val;
import net.lumae.LumaeCore.Lumae;
import net.lumae.LumaeCore.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinLeaveListener implements Listener {

	private static final Lumae plugin = JavaPlugin.getPlugin(Lumae.class);

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		val player = event.getPlayer();
		val legacyMessage = ColorUtil.colorMessage("<$#50c878>" + player.getName() + " has arrived!<$#bddeec>");
		event.joinMessage(null);
		Bukkit.getConsoleSender().sendMessage(player.getName() + " joined");
		Bukkit.getServer().getOnlinePlayers().forEach(p -> player.sendMessage(legacyMessage));
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		val player = event.getPlayer();
		val legacyMessage = ColorUtil.colorMessage("<$#50c878>" + player.getName() + " has departed!<$#bddeec>");
		event.quitMessage(null);
		Bukkit.getConsoleSender().sendMessage(player.getName() + " left");
		Bukkit.getServer().getOnlinePlayers().forEach(p -> player.sendMessage(legacyMessage));
	}


}

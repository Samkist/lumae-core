package net.lumae.LumaeCore.listeners;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import io.papermc.paper.chat.ChatFormatter;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.lumae.LumaeCore.Lumae;
import net.lumae.LumaeCore.storage.ChatFormat;
import net.lumae.LumaeCore.utils.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.List;

public class ChatListener implements Listener {

	private static final Lumae plugin = JavaPlugin.getPlugin(Lumae.class);
	private final List<ChatFormat> chatFormats;

	public ChatListener() {
		chatFormats = plugin.getDataManager().getChatFormats();
	}

	@EventHandler
	public void chatEvent(AsyncChatEvent event) {
		final Player player = event.getPlayer();
		final ChatFormat format = chatFormats.stream().filter(f ->
				player.hasPermission(f.getPermission()))
				.min(Comparator.comparing(ChatFormat::getPriority)).get();
		final ChatFormatter chatFormat = (displayName, msg) -> {

			final String metaApplied = PlaceholderAPI.setPlaceholders(player, format.getMessageFormat());
			final String message = metaApplied.replace("%lumae_message%", LegacyComponentSerializer.legacyAmpersand().serialize(msg));

			return GsonComponentSerializer.gson()
					.deserialize(ColorUtil.legacyToJson(IridiumColorAPI.process(message)));
		};
		event.formatter(chatFormat);

	}

}

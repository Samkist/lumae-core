package net.lumae.LumaeCore.listeners;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import io.papermc.paper.chat.ChatFormatter;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.extern.java.Log;
import lombok.val;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.lumae.LumaeCore.Lumae;
import net.lumae.LumaeCore.storage.ChatFormat;
import net.lumae.LumaeCore.utils.ColorUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

@Log(topic = "Lumae Chat Listener")
public class ChatListener implements Listener {

	private static final Lumae plugin = JavaPlugin.getPlugin(Lumae.class);
	private final List<ChatFormat> chatFormats;

	public ChatListener() {
		chatFormats = plugin.getDataManager().getChatFormats();
	}

	@EventHandler
	public void chatEvent(AsyncChatEvent event) {
		val player = event.getPlayer();
		val format = chatFormats.stream().filter(f ->
				player.hasPermission(f.getPermission()))
				.min(Comparator.comparing(ChatFormat::getPriority)).get();
		val chatFormat = new ChatFormatter() {
			@Override
			public @NotNull Component chat(@NotNull Component displayName, @NotNull Component msg) {

				val metaApplied = PlaceholderAPI.setPlaceholders(player, format.getMessageFormat());
				val message = metaApplied.replace("%lumae_message%", LegacyComponentSerializer.legacyAmpersand().serialize(msg));

				return GsonComponentSerializer.gson()
						.deserialize(ColorUtil.legacyToJson(IridiumColorAPI.process(message)));
			}
		};
		event.formatter(chatFormat);

	}

}

package net.lumae.LumaeCore.utils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import lombok.extern.java.Log;
import net.lumae.LumaeCore.Lumae;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

@Log(topic = "Lumae ColorUtil")
public class ColorUtil {
	private static final Lumae plugin = JavaPlugin.getPlugin(Lumae.class);

	/**
	 * Converts a legacy message (one you would read in chat) to a json message.
	 *
	 * @param legacyString the legacy message to convert
	 * @return the message converted to json
	 */
	public static String legacyToJson(String legacyString) {
		if (legacyString == null) return "";
		return ComponentSerializer.toString(TextComponent.fromLegacyText(legacyString));
	}


	/**
	 * Converts a json message to a legacy message.
	 *
	 * @param json the json message to convert
	 * @return the legacy message
	 */
	public static String jsonToLegacy(String json) {
		if (json == null) return "";
		return BaseComponent.toLegacyText(ComponentSerializer.parse(json));
	}



	public static String colorMessage(String legacyMsg) {

		return IridiumColorAPI.process(legacyMsg);
	}

}

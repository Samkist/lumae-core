package net.lumae.LumaeCore;

import com.mongodb.MongoCredential;
import lombok.Getter;
import lombok.val;
import net.lumae.LumaeCore.listeners.PlayerDataListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;

@Getter
public final class Lumae extends JavaPlugin {

	private final FileManager fileManager = new FileManager(this);
	private DBManager dbManager;
	private DataManager dataManager;

	@Override
	public void onEnable() {
		// Plugin startup logic
		fileManager.initialize();

		configureDatabase();

		dbManager.initialize();

		dataManager = new DataManager(fileManager, dbManager);

		val playerDataListener = new PlayerDataListener(this, dataManager.getPlayerDataMap(), dataManager);

		Arrays.asList(playerDataListener)
				.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic

	}

	private void configureDatabase() {
		val configYml = fileManager.getConfigYml();
		val connectionString = configYml.getString("lumae.database.connectionString");
		val database = configYml.getString("lumae.database.name");
		if(Objects.nonNull(connectionString)) {
			dbManager = new DBManager(this, connectionString,database);
		} else {
			String user = configYml.getString("lumae.database.user");
			String password = configYml.getString("lumae.database.password");
			String host = configYml.getString("lumae.database.host");
			Integer port = configYml.getInt("lumae.database.port");

			dbManager = new DBManager(this, MongoCredential.createCredential(user, database, password.toCharArray()), host, database, port);
		}
	}


}

package net.lumae.LumaeCore;

import com.mongodb.MongoCredential;
import lombok.Getter;
import lombok.val;
import net.lumae.LumaeCore.listeners.ChatListener;
import net.lumae.LumaeCore.listeners.JoinLeaveListener;
import net.lumae.LumaeCore.listeners.PlayerDataListener;
import net.lumae.LumaeCore.tasks.PlayerDataTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;

@Getter
public final class Lumae extends JavaPlugin {

	private final FileManager fileManager = new FileManager(this);
	@Getter
	public static final long LAST_START_TIME = System.currentTimeMillis();
	public  static String CHAT_FORMAT_COLLECTION_NAME = "";
	private DBManager dbManager;
	private DataManager dataManager;

	@Override
	public void onEnable() {
		// Plugin startup logic
		fileManager.initialize();

		configureDatabase();

		dbManager.initialize();

		dataManager = new DataManager(fileManager, dbManager);

		/*
		TASKS
		 */
		val playerDataTask = new PlayerDataTask();
		val playerDataTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, playerDataTask,6000L, 6000L);

		val playerDataListener = new PlayerDataListener(this, dataManager.getPlayerDataMap(), dataManager);

		val joinLeaveListener = new JoinLeaveListener();

		val chatListener = new ChatListener();

		Arrays.asList(playerDataListener, joinLeaveListener, chatListener)
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
			dbManager = new DBManager(this, connectionString, database);
		} else {
			String user = configYml.getString("lumae.database.user");
			String password = configYml.getString("lumae.database.password");
			String host = configYml.getString("lumae.database.host");
			Integer port = configYml.getInt("lumae.database.port");

			dbManager = new DBManager(this, MongoCredential.createCredential(user, database, password.toCharArray()), host, database, port);
		}
	}


}

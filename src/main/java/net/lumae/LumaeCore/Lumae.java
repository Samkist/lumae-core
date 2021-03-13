package net.lumae.LumaeCore;

import com.mongodb.MongoCredential;
import net.lumae.LumaeCore.listeners.ChatListener;
import net.lumae.LumaeCore.listeners.JoinLeaveListener;
import net.lumae.LumaeCore.listeners.PlayerDataListener;
import net.lumae.LumaeCore.tasks.PlayerDataTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;

public final class Lumae extends JavaPlugin {

	private final FileManager fileManager = new FileManager(this);
	public static final long LAST_START_TIME = System.currentTimeMillis();
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
		final PlayerDataTask playerDataTask = new PlayerDataTask();
		final int playerDataTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, playerDataTask,6000L, 6000L);

		final PlayerDataListener playerDataListener = new PlayerDataListener(this, dataManager.getPlayerDataMap(), dataManager);

		final JoinLeaveListener joinLeaveListener = new JoinLeaveListener();

		final ChatListener chatListener = new ChatListener();

		Arrays.asList(playerDataListener, joinLeaveListener, chatListener)
				.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic

	}

	private void configureDatabase() {
		final FileConfiguration configYml = fileManager.getConfigYml();
		final String connectionString = configYml.getString("database.connectionString");
		final String database = configYml.getString("database.name");
		if(Objects.nonNull(connectionString)) {
			dbManager = new DBManager(this, connectionString, database);
		} else {
			String user = configYml.getString("database.user");
			String password = configYml.getString("database.password");
			String host = configYml.getString("database.host");
			Integer port = configYml.getInt("database.port");

			dbManager = new DBManager(this, MongoCredential.createCredential(user, database, password.toCharArray()), host, database, port);
		}
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public DBManager getDbManager() {
		return this.dbManager;
	}

	public DataManager getDataManager() {
		return this.dataManager;
	}

}

package net.lumae.LumaeCore;

import com.mongodb.MongoCredential;
import lombok.Getter;
import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;

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

	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic

	}

	private void configureDatabase() {
		val configYml = fileManager.getConfigYml();
		String user = configYml.getString("lumae.database.user");
		String database = configYml.getString("lumae.database.name");
		String password = configYml.getString("lumae.database.password");
		String host = configYml.getString("lumae.database.host");
		Integer port = configYml.getInt("lumae.database.port");

		dbManager = DBManager.builder()
				.plugin(this)
				.credential(MongoCredential.createCredential(user, database, password.toCharArray()))
				.host(host)
				.port(port)
				.build();
	}


}

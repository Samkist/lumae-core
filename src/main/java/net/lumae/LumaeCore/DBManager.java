package net.lumae.LumaeCore;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class DBManager {

	private final Lumae plugin;
	private final MongoCredential credential;
	private final MongoClient mongoClient;


	public DBManager(Lumae plugin, String user, String database, String password, String host, short port) {
		this.plugin = plugin;
		this.credential = MongoCredential.createCredential(user, database, password.toCharArray());
		this.mongoClient = MongoClients.create(
				MongoClientSettings.builder()
						.applyToClusterSettings(builder ->
								builder.hosts(Arrays.asList(new ServerAddress(host, port))))
						.credential(credential)
						.build());
	}

	public DBManager(Lumae plugin, String user, String database, String password, String host) {
		this(plugin, user, database, password, host, (short) 27017);
	}

	public void savePlayersData() {

	}

	public void savePlayerData() {

	}

	public void loadPlayersData(Player player) {
		final String UUID = player.getUniqueId().toString();

	}
}

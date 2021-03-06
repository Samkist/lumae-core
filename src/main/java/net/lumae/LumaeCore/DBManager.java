package net.lumae.LumaeCore;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.WriteModel;
import lombok.Builder;
import net.lumae.LumaeCore.storage.DatabasePlayerData;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Builder
public class DBManager {

	private final Lumae plugin;
	private final MongoCredential credential;
	private final String host;
	private final String database;
	private final Integer port;
	private MongoClient mongoClient;
	private MongoDatabase db;
	private MongoCollection<DatabasePlayerData> playerDataCollection;
	private boolean init;
	//PLAIN OLD JAVA OBJECTS
	private CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
	private CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
			pojoCodecRegistry);

	public DBManager(Lumae plugin, String user, String database, String password, String host, Integer port) {
		this.plugin = plugin;
		this.credential = MongoCredential.createCredential(user, database, password.toCharArray());
		this.host = host;
		this.database = database;
		this.port = port;
	}

	public void initialize() {
		if(init) return;
		this.mongoClient = MongoClients.create(
				MongoClientSettings.builder()
						.applyToClusterSettings(builder ->
								builder.hosts(Arrays.asList(new ServerAddress(host, port))))
						.codecRegistry(codecRegistry)
						.credential(credential)
						.build());
		db = mongoClient.getDatabase(database);
		playerDataCollection = db.getCollection("playerData", DatabasePlayerData.class);
		init = true;
	}

	public void saveAllPlayers(Map<Player, PlayerData> playerData) {
		if(!init) return;
		List<DatabasePlayerData> playerDataList = playerData.entrySet().stream().map(e -> new DatabasePlayerData(e.getKey(), e.getValue())).collect(Collectors.toList());
		List<WriteModel<DatabasePlayerData>> writes = new ArrayList<>();
		playerDataList.forEach(d -> writes.add(
				new ReplaceOneModel<>(byUUIDField(d.getPlayer()), d)
		));
		BulkWriteResult bulkWriteResult = playerDataCollection.bulkWrite(writes);

	}

	public void savePlayerData(Player player, PlayerData playerData) {
		if(!init) return;
		playerDataCollection.findOneAndReplace(byUUIDField(player), new DatabasePlayerData(player, playerData));
	}

	public String topPlayerByField(String field) {
		return playerDataCollection.find().sort(eq(field, -1)).limit(1).first().getCurrentName();
	}

	public Optional<PlayerData> loadPlayerData(Player player) {
		if(!init) return Optional.empty();
		DatabasePlayerData playerData = playerDataCollection.find(eq("uuid", player.getUniqueId().toString())).first();
		if(Objects.nonNull(playerData)) {
			return Optional.of(playerData);
		}
		playerData = new DatabasePlayerData(player);
		playerDataCollection.insertOne(new DatabasePlayerData(player, playerData));
		return Optional.of(playerData);
	}

	private Bson byUUIDField(Player player) {
		return eq("uuid",  player.getUniqueId().toString());
	}
}

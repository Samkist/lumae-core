package net.lumae.LumaeCore;

import com.mongodb.ConnectionString;
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
import lombok.val;
import net.lumae.LumaeCore.storage.Cooldown;
import net.lumae.LumaeCore.storage.DatabasePlayerData;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

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
	private String connectionString;
	//PLAIN OLD JAVA OBJECTS
	private final CodecRegistry codecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

	public DBManager(Lumae plugin, String connectionString, String database) {
		this.plugin = plugin;
		this.connectionString = connectionString;
		this.credential = null;
		this.host = null;
		this.database = database;
		this.port = 0;
	}

	public DBManager(Lumae plugin, MongoCredential credential, String host, String database, Integer port) {
		this.plugin = plugin;
		this.credential = credential;
		this.host = host;
		this.database = database;
		this.port = port;
	}

	public void initialize() {
		if(init) return;
		if(Objects.nonNull(connectionString)) {
			this.mongoClient = MongoClients.create(MongoClientSettings.builder()
					.codecRegistry(codecRegistry)
					.applyConnectionString(new ConnectionString(connectionString))
					.build());

		} else {
			this.mongoClient = MongoClients.create(
					MongoClientSettings.builder()
							.applyToClusterSettings(builder ->
									builder.hosts(Arrays.asList(new ServerAddress(host, port))))
							.codecRegistry(codecRegistry)
							.credential(credential)
							.build());
		}
		db = mongoClient.getDatabase(database);
		playerDataCollection = db.getCollection("playerData", DatabasePlayerData.class);
		init = true;
	}

	public void saveAllPlayers(Map<Player, PlayerData> playerData) {
		if(!init) return;
		List<DatabasePlayerData> playerDataList = playerData.entrySet().stream().map(e -> new DatabasePlayerData(e.getKey(), e.getValue())).collect(Collectors.toList());
		List<WriteModel<DatabasePlayerData>> writes = new ArrayList<>();
		playerDataList.forEach(d -> writes.add(
				new ReplaceOneModel<>(eq("uuid", d.getUuid()), d)
		));
		BulkWriteResult bulkWriteResult = playerDataCollection.bulkWrite(writes);

	}

	public void savePlayerData(Player player, PlayerData playerData) {
		if(!init) return;
		playerDataCollection.findOneAndReplace(byUUIDField(player), new DatabasePlayerData(player, playerData));
	}

	public void initializePlayerData(Player player, PlayerData playerData) {
		if(!init) return;
		playerDataCollection.insertOne(new DatabasePlayerData(player,playerData));
	}

	public String topPlayerByField(String field) {
		return playerDataCollection.find().sort(eq(field, -1)).limit(1).first().getCurrentName();
	}

	public Optional<PlayerData> loadPlayerData(Player player) {
		if(!init) return Optional.empty();
		val playerDataCollection = db.getCollection("playerdata");
		val playerData = playerDataCollection.find(eq("uuid", player.getUniqueId().toString())).first();
		Decimal128 balance = new Decimal128(0);
		Double lumiumBalance = playerData.getDouble("lumiumBalance");
		Integer votes = playerData.getInteger("votes");
		Integer playerKills = playerData.getInteger("votes");
		Integer deaths = playerData.getInteger("deaths");
		Integer mobKills = playerData.getInteger("mobKills");
		Integer blocksMined = playerData.getInteger("blocksMined");
		Integer secondsPlayed = playerData.getInteger("secondsPlayed");
		String chatColor = playerData.getString("chatColor");
		String currentName = playerData.getString("currentName");
		Map<String, Location> homes = playerData.get("homes", Map.class);
		Map<String, Cooldown> kitCooldowns = playerData.get("homes", Map.class);
		return Optional.of(new DatabasePlayerData(player, balance, lumiumBalance, votes, playerKills,
				deaths, mobKills, blocksMined, secondsPlayed, chatColor, currentName, homes, kitCooldowns));
	}

	private Bson byUUIDField(Player player) {
		return eq("uuid",  player.getUniqueId().toString());
	}
}

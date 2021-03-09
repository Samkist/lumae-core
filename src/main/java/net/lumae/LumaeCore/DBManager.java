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
import dev.morphia.*;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import net.lumae.LumaeCore.storage.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
	private Datastore datastore;
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
		datastore = Morphia.createDatastore(mongoClient, database);
		playerDataCollection = db.getCollection("playerData", DatabasePlayerData.class);
		init = true;
	}

	@NonNull
	public Optional<BulkWriteResult> saveAllPlayers(Map<UUID, PlayerData> playerData) {
		if(!init) return Optional.empty();
		List<DatabasePlayerData> playerDataList = playerData.entrySet().stream().map(e -> new DatabasePlayerData(e.getKey(), e.getValue())).collect(Collectors.toList());
		List<WriteModel<DatabasePlayerData>> writes = new ArrayList<>();
		playerDataList.forEach(d -> writes.add(
				new ReplaceOneModel<>(eq("uuid", d.getUuid()), d)
		));
		return Optional.ofNullable(playerDataCollection.bulkWrite(writes));
	}

	@NonNull
	@SneakyThrows
	public void savePlayerData(Player player, PlayerData playerData) {
		if(!init) return;
		val playerDataCollection = db.getCollection("playerData");
		val data = new DatabasePlayerData(player, playerData);
		Document query = new Document();
		query.put("uuid", player.getUniqueId().toString());
		query.put("balance", data.getBalance());
		query.put("lumiumBalance", data.getLumiumBalance());
		query.put("votes", data.getVotes());
		query.put("playerKills", data.getPlayerKills());
		query.put("deaths", data.getDeaths());
		query.put("mobKills", data.getMobKills());
		query.put("blocksMined", data.getBlocksMined());
		query.put("secondsPlayed", data.getSecondsPlayed());
		query.put("chatColor", data.getChatColor());
		query.put("currentName", data.getCurrentName());
		query.put("homes", playerData.getHomes());
		query.put("kitCooldowns", playerData.getKitCooldowns());
		query.put("joinDate", playerData.getJoinDate());
		playerDataCollection.findOneAndReplace(byUUIDField(player), query);
	}

	@NonNull
	public void initializePlayerData(Player player, PlayerData playerData) {
		if(!init) return;
		playerDataCollection.insertOne(new DatabasePlayerData(player, playerData));
	}

	@NonNull
	public String topPlayerByField(String field) {
		return playerDataCollection.find().sort(eq(field, -1)).limit(1).first().getCurrentName();
	}

	@NonNull
	public Optional<PlayerData> loadPlayerData(Player player) {
		if(!init) return Optional.empty();
		val playerDataCollection = db.getCollection("playerData");
		val playerData = playerDataCollection.find(eq("uuid", player.getUniqueId().toString())).first();
		if(Objects.isNull(playerData)) return Optional.empty();
		Decimal128 balance = playerData.get("balance", Decimal128.class);
		Double lumiumBalance = playerData.getDouble("lumiumBalance");
		Integer votes = playerData.getInteger("votes");
		Integer playerKills = playerData.getInteger("playerKills");
		Integer deaths = playerData.getInteger("deaths");
		Integer mobKills = playerData.getInteger("mobKills");
		Integer blocksMined = playerData.getInteger("blocksMined");
		Integer secondsPlayed = playerData.getInteger("secondsPlayed");
		String chatColor = playerData.getString("chatColor");
		String currentName = playerData.getString("currentName");
		Map<String, Location> homes = playerData.get("homes", Map.class);
		Map<String, Cooldown> kitCooldowns = playerData.get("kitCooldowns", Map.class);
		Date joinDate = playerData.getDate("joinDate");
		return Optional.of(new DatabasePlayerData(player, balance, lumiumBalance, votes, playerKills,
				deaths, mobKills, blocksMined, secondsPlayed, chatColor, currentName, homes, kitCooldowns, joinDate));
	}

	private Bson byUUIDField(Player player) {
		return eq("uuid",  player.getUniqueId().toString());
	}

	@NonNull
	public void initializeChatFormats(ChatFormat chatFormats) {
		if(!init) return;
		val query = datastore.find("ChatFormats", ChatFormat.class).first();
		if(Objects.isNull(query)) {
			datastore.save(chatFormats);
		}
	}

	public List<ChatFormat> loadChatFormats() {
		if(!init) return new ArrayList<>();
		val query = datastore.find("ChatFormats", ChatFormat.class).first();
		if(Objects.nonNull(query)) {
			return datastore.find(ChatFormat.class)
					.iterator().toList();
		} else {
			initializeChatFormats(new ChatFormat("default", "lumae.chat.default",
					"%luckperms_prefix% %player_displayname% &8»&7 %lumae_message%", 99999));
			return loadChatFormats();
		}
	}

	public void initializeMessages(List<Message> messages) {
		if(!init) return;
		val query = datastore.find("messages", Message.class).first();
		if(Objects.nonNull(query)) {
			datastore.save(messages);
		}
	}

	public List<Message> loadMessages() {
		if(!init) return new ArrayList<>();
		val query = datastore.find("messages", Message.class).first();
		if(Objects.nonNull(query)) {
			return datastore.find(Message.class).iterator().toList();
		} else {
			//initialize message code then
			//return loadMessages();
		}
		return new ArrayList<>();
	}
}

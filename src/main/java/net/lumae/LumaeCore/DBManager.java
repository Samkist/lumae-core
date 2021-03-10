package net.lumae.LumaeCore;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperator;
import dev.morphia.query.experimental.updates.UpdateOperators;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import net.lumae.LumaeCore.storage.ChatFormat;
import net.lumae.LumaeCore.storage.Message;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;
import org.bukkit.entity.Player;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class DBManager {

	private final Lumae plugin;
	private final MongoCredential credential;
	private final String host;
	private final String database;
	private final Integer port;
	private MongoClient mongoClient;
	private MongoDatabase db;
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
		init = true;
	}

	@NonNull
	public Optional<BulkWriteResult> saveAllPlayers(Map<UUID, PlayerData> playerData) {
		return Optional.empty();
	}

	@NonNull
	@SneakyThrows
	public void savePlayerData(Player player, PlayerData playerData) {
		if(!init) return;

		val query = datastore.find(PlayerData.class).filter(Filters.eq("uuid",player.getUniqueId().toString()));

		ArrayList<UpdateOperator> updateOperators = new ArrayList<>();
		val playerUpdate = UpdateOperators.set("player", player);
		updateOperators.add(UpdateOperators.set("chatFormat", playerData.getChatFormat()));
		updateOperators.add(UpdateOperators.set("balance", playerData.getBalance()));
		updateOperators.add(UpdateOperators.set("lumiumBalance", playerData.getLumiumBalance()));
		updateOperators.add(UpdateOperators.set("votes", playerData.getVotes()));
		updateOperators.add(UpdateOperators.set("playerKills", playerData.getPlayerKills()));
		updateOperators.add(UpdateOperators.set("deaths", playerData.getDeaths()));
		updateOperators.add(UpdateOperators.set("mobKills", playerData.getMobKills()));
		updateOperators.add(UpdateOperators.set("blocksMined", playerData.getBlocksMined()));
		updateOperators.add(UpdateOperators.set("secondsPlayed", playerData.getSecondsPlayed()));
		updateOperators.add(UpdateOperators.set("homes", playerData.getHomes()));
		updateOperators.add(UpdateOperators.set("kitCooldowns", playerData.getKitCooldowns()));
		updateOperators.add(UpdateOperators.set("joinDate", playerData.getJoinDate()));


		if(Objects.nonNull(query)) {
			query.update(playerUpdate, updateOperators.toArray(new UpdateOperator[updateOperators.size()]));
		} else {
			initializePlayerData(player);
		}
	}

	@NonNull
	public void initializePlayerData(Player player) {
		if(!init) return;
		val query = datastore.find(PlayerData.class).filter(Filters.eq("uuid", player.getUniqueId().toString()));
		if(Objects.isNull(query)) {
			val chatFormat = loadChatFormats().get(0);
			val data = new PlayerData(player.getUniqueId().toString(), player, chatFormat,
					new Decimal128(0), 0D, 0, 0, 0, 0, 0, 0, new HashMap<>(), new HashMap<>(), Calendar.getInstance().getTime());
			savePlayerData(player, data);
		}
	}

	@NonNull
	public String topPlayerByField(String field) {
		return null;
	}

	@NonNull
	public Optional<PlayerData> loadPlayerData(Player player) {
		if(!init) return Optional.empty();
		val query = datastore.find("playerData", ChatFormat.class).first();
		return Optional.empty();
	}

	private Bson byUUIDField(Player player) {
		return eq("uuid",  player.getUniqueId().toString());
	}

	@NonNull
	public void initializeChatFormats(ChatFormat chatFormats) {
		if(!init) return;
		val query = datastore.find("chatFormats", ChatFormat.class).first();
		if(Objects.isNull(query)) {
			datastore.save(chatFormats);
		}
	}

	public List<ChatFormat> loadChatFormats() {
		if(!init) return new ArrayList<>();
		val query = datastore.find("chatFormats", ChatFormat.class).first();
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

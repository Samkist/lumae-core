package net.lumae.LumaeCore;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.aggregation.experimental.stages.Group;
import dev.morphia.aggregation.experimental.stages.Sort;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import net.lumae.LumaeCore.storage.ChatFormat;
import net.lumae.LumaeCore.storage.Message;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.Decimal128;
import org.bukkit.entity.Player;

import java.util.*;

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
	public void saveAllPlayers(Map<UUID, PlayerData> playerData) {
		datastore.withTransaction(s
				-> s.save(new ArrayList<>(playerData.values())));
	}

	@NonNull
	@SneakyThrows
	public void savePlayerData(Player player, PlayerData playerData) {
		if(!init) return;

		val query = datastore.find(PlayerData.class).filter(Filters.eq("uuid",player.getUniqueId().toString()));


		if(Objects.nonNull(query)) {
			query.update(UpdateOperators.set(playerData)).execute();
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
		if(!init) return "";
		val aggregation = datastore.aggregate(PlayerData.class).group(Group.of(Group.id(field)).field(field)
		).sort(Sort.on().ascending(field));
		return aggregation.execute(Player.class).toList().get(0).getName();
	}

	@NonNull
	public Optional<PlayerData> loadPlayerData(Player player) {
		if(!init) return Optional.empty();
		val query = datastore.find(PlayerData.class).filter(Filters.eq("uuid", player.getUniqueId().toString())).first();
		return Optional.ofNullable(query);
	}

	@NonNull
	public void initializeChatFormats(ChatFormat chatFormats) {
		if(!init) return;
		val query = datastore.find(ChatFormat.class).first();
		if(Objects.isNull(query)) {
			datastore.save(chatFormats);
		}
	}

	public List<ChatFormat> loadChatFormats() {
		if(!init) return new ArrayList<>();
		val query = datastore.find(ChatFormat.class).first();
		if(Objects.nonNull(query)) {
			return datastore.find(ChatFormat.class)
					.iterator().toList();
		} else {
			val config = plugin.getFileManager().getConfigYml();
			val name = config.getString("defaults.chatFormats.name");
			val permission = config.getString("defaults.chatFormats.permission");
			val format = config.getString("default.chatFormats.messageFormat");
			val priority = config.getInt("default.chatFormats.priority");
			initializeChatFormats(new ChatFormat(name, permission, format, priority));
			return loadChatFormats();
		}
	}

	public void initializeMessages(List<Message> messages) {
		if(!init) return;
		val query = datastore.find(Message.class).first();
		if(Objects.nonNull(query)) {
			datastore.save(messages);
		}
	}

	public List<Message> loadMessages() {
		if(!init) return new ArrayList<>();
		val query = datastore.find(Message.class).first();
		if(Objects.nonNull(query)) {
			return datastore.find(Message.class).iterator().toList();
		} else {
			val config = plugin.getFileManager().getConfigYml();
			val messages = new ArrayList<Message>();
			messages.add(new Message(config.getString("defaults.pluginMessages.motd.id"), config.getString("defaults.pluginMessages.motd.format")));
			initializeMessages(messages);
			return loadMessages();
		}
	}
}

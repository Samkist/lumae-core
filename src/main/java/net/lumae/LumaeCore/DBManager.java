package net.lumae.LumaeCore;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.aggregation.experimental.Aggregation;
import dev.morphia.aggregation.experimental.stages.Group;
import dev.morphia.aggregation.experimental.stages.Sort;
import dev.morphia.query.Query;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import net.lumae.LumaeCore.storage.ChatFormat;
import net.lumae.LumaeCore.storage.JoinLeaveFormat;
import net.lumae.LumaeCore.storage.Message;
import net.lumae.LumaeCore.storage.PlayerData;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class DBManager {

	private final Lumae plugin;
	private final MongoCredential credential;
	private final String host;
	private final String database;
	private final Integer port;
	private boolean init;
	private MongoClient mongoClient;
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
		datastore = Morphia.createDatastore(mongoClient, database);
		init = true;
	}

	public void saveAllPlayers(Map<UUID, PlayerData> playerData) {
		datastore.withTransaction(s
				-> s.save(new ArrayList<>(playerData.values())));
	}

	public String topPlayerByField(String field) {
		if(!init) return "";
		final Aggregation<PlayerData> aggregation = datastore.aggregate(PlayerData.class).group(Group.of(Group.id(field)).field(field)
		).sort(Sort.on().descending(field));
		return aggregation.execute(Player.class).toList().get(0).getName();
	}

	public void savePlayerData(Player player, PlayerData playerData) {
		if(!init) return;

		final Query<PlayerData> query = datastore.find(PlayerData.class).filter(Filters.eq("uuid", player.getUniqueId().toString()));


		if(Objects.nonNull(query)) {
			query.update(UpdateOperators.set(playerData)).execute();
		} else {
			plugin.getLogger().info("Initializing from savePlayer");
			initializePlayerData(player);
		}
	}

	public void initializePlayerData(Player player) {
		if(!init) return;
		final PlayerData data = new PlayerData(player);
		datastore.save(data);
	}

	public Optional<PlayerData> loadPlayerData(Player player) {
		if(!init) return Optional.empty();
		final PlayerData result = datastore.find("playerData", PlayerData.class)
				.filter(Filters.eq("uuid", player.getUniqueId().toString()))
				.first();
		if(Objects.isNull(result)) {
			initializePlayerData(player);
			loadPlayerData(player);
		}
		return Optional.ofNullable(result);
	}

	public void initializeChatFormats(ChatFormat chatFormat) {
		if(!init) return;
		final ChatFormat result = datastore.find(ChatFormat.class).first();
		if(Objects.isNull(result)) {
			datastore.save(chatFormat);
		}
	}

	public List<ChatFormat> loadChatFormats() {
		if(!init) return new ArrayList<>();
		final ChatFormat result = datastore.find(ChatFormat.class).first();
		if(Objects.nonNull(result)) {
			return datastore.find(ChatFormat.class)
					.iterator().toList();
		} else {
			final FileConfiguration config = plugin.getFileManager().getConfigYml();
			final String name = "default";
			final String permission = config.getString("defaults.chatFormats.permission");
			final String format = config.getString("default.chatFormats.messageFormat");
			final Integer priority = config.getInt("default.chatFormats.priority");
			initializeChatFormats(new ChatFormat(name, permission, format, priority));
			return loadChatFormats();
		}
	}

	public void initializeMessages(List<Message> messages) {
		if(!init) return;
		datastore.save(messages);
	}

	public List<Message> loadMessages() {
		if(!init) return new ArrayList<>();
		final Message result = datastore.find(Message.class).first();
		if(Objects.nonNull(result)) {
			return datastore.find(Message.class).iterator().toList();
		} else {
			final FileConfiguration config = plugin.getFileManager().getConfigYml();
			final ArrayList<Message> messages = new ArrayList<>();
			messages.add(new Message("lumae-motd", config.getString("defaults.pluginMessages.motd.format")));
			initializeMessages(messages);
			return loadMessages();
		}
	}

	public void initializeJoinLeaveFormats(JoinLeaveFormat format) {
		if(!init) return;
		datastore.save(format);
	}

	public List<JoinLeaveFormat> loadJoinLeaveFormats() {
		if(!init) return new ArrayList<>();
		final JoinLeaveFormat result = datastore.find(JoinLeaveFormat.class).first();
		if(Objects.nonNull(result)) {
			return datastore.find(JoinLeaveFormat.class).iterator().toList();
		} else {
			final FileConfiguration config = plugin.getFileManager().getConfigYml();
			final String name = "default";
			final String permission = config.getString("defaults.joinLeaveFormats.permission");
			final String messageFormat = config.getString("defaults.joinLeaveFormats.messageFormat");
			final int priority = config.getInt("defaults.joinLeaveFormats.priority");
			final JoinLeaveFormat format = new JoinLeaveFormat(name, permission, messageFormat, priority);
			initializeJoinLeaveFormats(format);
			return loadJoinLeaveFormats();
		}
	}
}

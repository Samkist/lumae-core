package net.lumae.LumaeCore;

public class DataManager {

	private final FileManager fileManager;
	private final DBManager dbManager;

	public DataManager(FileManager fileManager, DBManager dbManager) {
		this.fileManager = fileManager;
		this.dbManager = dbManager;
	}
}

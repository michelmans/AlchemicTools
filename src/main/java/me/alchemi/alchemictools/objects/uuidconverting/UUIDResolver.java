package me.alchemi.alchemictools.objects.uuidconverting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.Restart;
import me.alchemi.alchemictools.objects.events.OnlineUUIDApplyEvent;
import me.alchemi.alchemictools.objects.placeholder.Stringer;

public class UUIDResolver implements Listener{
	
	private Map<String, UUID> onlineUUIDS = new HashMap<String, UUID>();
	private List<String> failedPlayers = new ArrayList<String>();
	private BukkitTask task;
	
	private static final File configFile = new File(Tools.getInstance().getDataFolder(), "custom-uuids.yml");
	
	public void onEnable(Server server) {
		if (!configFile.exists())
			try {
				configFile.createNewFile();
			} catch (IOException e) {}
		
		onlineUUIDS.clear();
		failedPlayers.clear();
		for (OfflinePlayer player : server.getOfflinePlayers()) {
			if (UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes()).equals(player.getUniqueId())) requestUUIDWrapper(player.getName(), onlineUUIDS::put, failedPlayers::add);
		}
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		for (Entry<String, Object> entry : config.getValues(true).entrySet()) {
			onlineUUIDS.put(entry.getKey(), UUID.fromString(entry.getValue().toString()));
		}
	}
	
	private UUID requestUUID(String playername) {
		try {
			String url = "https://api.mojang.com/users/profiles/minecraft/" + playername;
			String jsonString = IOUtils.toString(new URL(url), Charset.defaultCharset());
			
			JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(jsonString);
			String uuidString = UUIDObject.get("id").toString();
			
			String newUUIDString = uuidString.substring(0, 8) + "-" + uuidString.substring(8, 12) + "-" + uuidString.substring(12, 16) + "-" + uuidString.substring(16, 20) + "-" + uuidString.substring(20);
		
			UUID id = UUID.fromString(newUUIDString);
			
			Tools.getInstance().getMessenger().print(new Stringer(Messages.UUID_FETCHED)
					.player(playername)
					.create());
			Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.UUID_FETCHED)
					.player(playername)
					.create(), true, Player -> Player.hasPermission("alchemictools.tools.notify"));
			
			return id;
		} catch (IOException | ParseException | IllegalArgumentException | IndexOutOfBoundsException e) {
			Tools.getInstance().getMessenger().print(new Stringer(Messages.UUID_FAILED)
					.player(playername)
					.create());
			Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.UUID_FAILED)
					.player(playername)
					.create(), true, Player -> Player.hasPermission("alchemictools.tools.notify"));
		}
		return null;
	}
	
	public void requestUUIDWrapper(String playername, BiConsumer<String, UUID> function, Consumer<String> failedFunction) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				UUID id = requestUUID(playername);
				if (id != null) function.accept(playername, id);
				else failedFunction.accept(playername);
			}
		}.runTaskAsynchronously(Tools.getInstance());
	}
	
	public void requestCustomUUIDWrapper(OfflinePlayer oldPlayer, String newPlayername, BiConsumer<String, UUID> function, Consumer<String> failedFunction) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				UUID id = requestUUID(newPlayername);
				if (id != null) {
					function.accept(oldPlayer.getName(), id);
					FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
					config.set(oldPlayer.getName(), id.toString());
					try {
						config.save(configFile);
					} catch (IOException e) {}
				}
				else failedFunction.accept(oldPlayer.getName());
				
			}
		}.runTaskAsynchronously(Tools.getInstance());
	}
	
	public void apply() {
		Bukkit.getPluginManager().callEvent(new OnlineUUIDApplyEvent(onlineUUIDS));
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onNewUUIDApply(OnlineUUIDApplyEvent e) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.kickPlayer(new Stringer(Messages.UUID_KICKED)
					.player(player)
					.parse(player)
					.create());
		}
		new BukkitRunnable() {
			
			@Override
			public void run() {
				File worldFile = new File(Bukkit.getWorldContainer(), Bukkit.getServer().getWorlds().get(0).getName());
				File dataFile = new File(worldFile, "playerdata");
				IConverter.backupDir(dataFile);
				Tools.getInstance().getMessenger().print("Preparing to rename .dat files...");
				for (UUID old : e.getOldUUIDs()) {
					Tools.getInstance().setUuidConverting(true);
					File datFile = new File(dataFile, old.toString() + ".dat");
					datFile.renameTo(new File(dataFile, e.getNewUUID(old).toString() + ".dat"));
					Tools.getInstance().getMessenger().print("Renamed " + old.toString() + ".dat to " + e.getNewUUID(old) + ".dat");
				}
				Tools.getInstance().setUuidConverting(false);
			}
		}.runTaskAsynchronously(Tools.getInstance());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void afterNewUUIDApply(OnlineUUIDApplyEvent e) {
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!Tools.getInstance().isUuidConverting()) {				
					
					FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
					for (String key : config.getValues(true).keySet()) {
						config.set(key, null);
					}
					try {
						config.save(configFile);
					} catch (IOException e) {}
					
					new Restart(Bukkit.getConsoleSender(), 10, "UUID migration.");
					task.cancel();
				}
			}
		}.runTaskTimer(Tools.getInstance(), 40, 80);
	}
	
	public void putOnlineUUID(String playername, UUID uuid) {
		onlineUUIDS.put(playername, uuid);
	}
	
	public void addFailedPlayer(String playername) {
		failedPlayers.add(playername);
	}

}

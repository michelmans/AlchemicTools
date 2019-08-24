package me.alchemi.alchemictools.objects.uuidconverting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.events.OnlineUUIDApplyEvent;

public class UUIDResolver implements Listener{
	
	private Map<String, UUID> onlineUUIDS = new HashMap<String, UUID>();
	private List<String> failedPlayers = new ArrayList<String>();	
	
	public void onEnable(Server server) {
		onlineUUIDS.clear();
		failedPlayers.clear();
		for (OfflinePlayer player : server.getOfflinePlayers()) {
			if (UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes()).equals(player.getUniqueId())) requestUUIDWrapper(player.getName(), onlineUUIDS::put, failedPlayers::add);
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
			
			Tools.getInstance().getMessenger().print("&aGotten online UUID for " + playername);
			Tools.getInstance().getMessenger().print("&a" + id.toString());
			
			return id;
		} catch (IOException | ParseException | IllegalArgumentException | IndexOutOfBoundsException e) {
			Tools.getInstance().getMessenger().print("&cFailed to get online UUID for " + playername);
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
	
	public void apply() {
		if (Bukkit.getOnlineMode())	Bukkit.getPluginManager().callEvent(new OnlineUUIDApplyEvent(onlineUUIDS));
	}
	
	@EventHandler
	public void onNewUUIDApply(OnlineUUIDApplyEvent e) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				File worldFile = new File(Bukkit.getWorldContainer(), Bukkit.getServer().getWorlds().get(0).getName());
				File dataFile = new File(worldFile, "playerdata");
				IConverter.backupDir(dataFile);
				Tools.getInstance().getMessenger().print("Preparing to rename .dat files...");
				for (UUID old : e.getOldUUIDs()) {
					File datFile = new File(dataFile, old.toString() + ".dat");
					datFile.renameTo(new File(dataFile, e.getNewUUID(old).toString() + ".dat"));
					Tools.getInstance().getMessenger().print("Renamed " + old.toString() + ".dat to " + e.getNewUUID(old) + ".dat");
				}
			}
		}.runTaskAsynchronously(Tools.getInstance());
	}

}

package me.alchemi.alchemictools.objects.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OnlineUUIDApplyEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Set<OfflinePlayer> players;
	private final Map<UUID, UUID> oldToNewUUIDs;
	
	public OnlineUUIDApplyEvent(Map<String, UUID> newUUIDs) {
		
		HashSet<OfflinePlayer> players = new HashSet<OfflinePlayer>();
		HashMap<UUID, UUID> oldToNewUUIDs = new HashMap<UUID, UUID>();
		
		for (Entry<String, UUID> playerid : newUUIDs.entrySet()) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerid.getKey()).getBytes()));
			
			if (player != null) {
				players.add(player);
				oldToNewUUIDs.put(player.getUniqueId(), playerid.getValue());
			}
		}
		
		this.players = players;
		this.oldToNewUUIDs = oldToNewUUIDs;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * @return the players
	 */
	public Set<OfflinePlayer> getPlayers() {
		return players;
	}

	/**
	 * @return the newUUIDs
	 */
	public Collection<UUID> getNewUUIDs() {
		return oldToNewUUIDs.values();
	}

	/**
	 * @return the oldUUIDs
	 */
	public Collection<UUID> getOldUUIDs() {
		return oldToNewUUIDs.keySet();
	}

	/**
	 * @return the oldToNewUUIDs
	 */
	public Map<UUID, UUID> getOldToNewUUIDs() {
		return oldToNewUUIDs;
	}
	
	public UUID getNewUUID(UUID old) {
		if (oldToNewUUIDs.containsKey(old)) return oldToNewUUIDs.get(old);
		throw new IllegalArgumentException(old.toString() + " doesn't exist.");
	}

}

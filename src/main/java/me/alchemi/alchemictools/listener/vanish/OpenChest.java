package me.alchemi.alchemictools.listener.vanish;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.alchemi.alchemictools.Tools;

public class OpenChest implements Listener {

	private static Map<Player, Chest> chests = new HashMap<Player, Chest>();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRightClickBlock(PlayerInteractEvent e) {
		if (Tools.getInstance().getVanishedPlayers().contains(e.getPlayer())
				&& e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& e.getClickedBlock().getState() instanceof Chest) {
			
			chests.put(e.getPlayer(), (Chest) e.getClickedBlock().getState());
			
			ProtocolLibrary.getProtocolManager().addPacketListener(noAnimationOpen);
			
			ProtocolLibrary.getProtocolManager().addPacketListener(silentOpen);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (chests.keySet().contains(e.getPlayer())) {
			Bukkit.getScheduler().runTaskLater(Tools.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					chests.remove(e.getPlayer());
					
				}
			}, 5);
		}
	}
	
	PacketAdapter noAnimationOpen = new PacketAdapter(Tools.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Server.BLOCK_ACTION) {
		
		@Override
		public void onPacketSending(PacketEvent e) {
			
			if (e.getPacketType() == PacketType.Play.Server.BLOCK_ACTION
					&& Tools.getInstance().getVanishedPlayers().contains(e.getPlayer())) {
				e.setCancelled(true);
			}
			
		}
		
	};
	
	PacketAdapter silentOpen = new PacketAdapter(Tools.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
		
		@Override
		public void onPacketSending(PacketEvent e) {
			
			PacketContainer packet = e.getPacket();
			
			double x = packet.getIntegers().readSafely(0)/8.0;
			double y = packet.getIntegers().readSafely(1)/8.0;
			double z = packet.getIntegers().readSafely(2)/8.0;
				
			Location location = new Location(e.getPlayer().getWorld(), x, y, z);
			
			if (location.getBlock().getState() instanceof Chest
					&& chests.values().contains(location.getBlock().getState())) {
					e.setCancelled(true);
			}
			
		}
		
	};
	
}

package me.alchemi.alchemictools.objects;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.events.PlayerPreInteractEvent;

public class ProtocolUtil {
	
	public static void updateGameModeTab(Player player, boolean vanish) {
		
		PacketContainer packet = createGameModePacket(player, vanish);
		
        try {
        	
        	for (Player receiver : Bukkit.getOnlinePlayers()) {
        		if (receiver.hasPermission("alchemictools.vanish.see") && !receiver.equals(player)) ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packet);
        	}
        	
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet", e);
        }
		
	}
	
	public static void sendGameModePacket(Player player, Player receiver, boolean vanish) {
		PacketContainer packet = createGameModePacket(player, vanish);
		
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packet);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Cannot send packet", e);
		}
	}
	
	public static void listenChatPackets() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Tools.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.CHAT) {
			
			@Override
			public void onPacketReceiving(PacketEvent e) {
				if (Tools.getInstance().getStaffchat().isListening(e.getPlayer())) {
					PacketContainer packet = e.getPacket();
					String string = packet.getStrings().readSafely(0);
					
					if (string == null || string.startsWith("/") || string.startsWith("!")) return;
					e.setCancelled(true);
					Tools.getInstance().getStaffchat().send(e.getPlayer(), string);
				}
			}
			
		});
	}
	
	public static void listenPreInteractPackets() {
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Tools.getInstance(), ListenerPriority.LOWEST, PacketType.Play.Client.USE_ITEM) {
			
			@Override
			public void onPacketReceiving(PacketEvent e) {
				Hand hand = e.getPacket().getHands().readSafely(0);
				
				switch(hand) {
				case MAIN_HAND:
					Bukkit.getPluginManager().callEvent(new PlayerPreInteractEvent(e.getPlayer(), e.getPlayer().getInventory().getItemInMainHand()));
					break;
				case OFF_HAND:
					Bukkit.getPluginManager().callEvent(new PlayerPreInteractEvent(e.getPlayer(), e.getPlayer().getInventory().getItemInOffHand()));
					break;
				}
			}
			
		});
	}
	
	public static PacketContainer createGameModePacket(Player player, boolean vanish) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE);
        List<PlayerInfoData> data = new ArrayList<>();
        int ping = ThreadLocalRandom.current().nextInt(20) + 15;
        data.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(player), ping,
                vanish ? EnumWrappers.NativeGameMode.SPECTATOR
                        : EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
                WrappedChatComponent.fromText(player.getPlayerListName())));
        packet.getPlayerInfoDataLists().write(0, data);
        return packet;
	}

}

package me.alchemi.alchemictools.bungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.Permissions;

public class BungeeReceiver implements PluginMessageListener{

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		
		if (!channel.equals("BungeeCord")) return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		
		String subchannel = in.readUTF();
		if (subchannel.endsWith("@" + Tools.getInstance().getName())) {
			switch(BungeeMessage.Channel.valueOf(subchannel.replace("@" + Tools.getInstance().getName(), ""))) {
			case STAFFCHAT:
				
				//read message
				short len = in.readShort();
				byte[] msgbytes = new byte[len];
				in.readFully(msgbytes);
				
				String server = "Unknown Server";
				String sender = null;
				boolean staff = false;
				
				String msg = null;
				DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
				try {
					server = msgin.readUTF();
					sender = msgin.readUTF();
					staff = msgin.readBoolean();
					msg = msgin.readUTF();
				} catch(IOException e) {
					e.printStackTrace();
				}
				
				if (!(sender == null && msg == null)) Tools.getInstance().getStaffchat().send(server, sender, staff, msg);
				break;
			case VANISH:
				break;
			default:
				break;			
			}
		} else if (subchannel.equalsIgnoreCase("VanishedPlayerList")) {
			
			OfflinePlayer oplayer = Bukkit.getOfflinePlayer(UUID.fromString(in.readUTF()));
			
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("VanishedPlayerList");
			out.writeUTF(onlinePlayers(oplayer));
			
			player.sendPluginMessage(Tools.getInstance(), "BungeeCord", out.toByteArray());
		}
		
	}
	
	private String onlinePlayers(OfflinePlayer oPlayer) {
		Iterator<? extends Player> players = Bukkit.getOnlinePlayers().iterator();
		
		String onlinePlayers = "";
		
		while (players.hasNext()) {
			Player player = players.next();
			if (Tools.getInstance().getVanishedPlayers().contains(player)
					&& !Tools.getInstance().getPerm().playerHas(Bukkit.getServer().getWorlds().get(0).getName(), 
							oPlayer, Permissions.VANISH_SEE.toString())) continue;
			else if (Tools.getInstance().getVanishedOPPlayers().contains(player)
					&& !Tools.getInstance().getPerm().playerHas(Bukkit.getServer().getWorlds().get(0).getName(), 
							oPlayer, Permissions.VANISH_SPECIAL_SEE.toString())) continue;
			else {
				
				onlinePlayers = onlinePlayers.concat(", " + player.getName());
				
			}
		}
		return onlinePlayers.replaceFirst(", ", "");
	}
	
}

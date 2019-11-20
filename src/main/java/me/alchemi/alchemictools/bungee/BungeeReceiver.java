package me.alchemi.alchemictools.bungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.alchemictools.Config.Options;
import me.alchemi.alchemictools.Tools;

public class BungeeReceiver implements PluginMessageListener{

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		
		if (!channel.equals("BungeeCord")) return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		
		String subchannel = in.readUTF();
		Messenger.printStatic(subchannel);
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
		} else if (subchannel.equalsIgnoreCase("PlayerList")) {
			in.readUTF();
			if (in.readBoolean()) {
			
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("PlayerList");
				out.writeUTF(Options.SERVERNAME.asString());
				
				player.sendPluginMessage(Tools.getInstance(), "BungeeCord", out.toByteArray());
				
			}
		}
		
	}
	
}

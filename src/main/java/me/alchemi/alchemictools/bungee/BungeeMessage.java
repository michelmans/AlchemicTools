package me.alchemi.alchemictools.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.alchemi.alchemictools.Tools;

public class BungeeMessage {
	
	private ByteArrayDataOutput out;
	ByteArrayOutputStream msgbytes;
	private DataOutputStream msgout;
	
	public static enum Channel {
		STAFFCHAT,VANISH;
	}
	
	public BungeeMessage(Channel channel, String server) {
		out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF(server);
		out.writeUTF(channel.toString() + "@" + Tools.getInstance().getName());
		
		msgbytes = new ByteArrayOutputStream();
		msgout = new DataOutputStream(msgbytes);
	}
	
	public BungeeMessage integer(int i) {
		try {
			msgout.writeInt(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public BungeeMessage bool(boolean b) {
		try {
			msgout.writeBoolean(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public BungeeMessage doub(double d) {
		try {
			msgout.writeDouble(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public BungeeMessage string(String s) {
		try {
			msgout.writeUTF(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public BungeeMessage msg(String msg) {
		try {
			msgout.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public boolean send(Player player) {
		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());
		
		try {
			player.sendPluginMessage(Tools.getInstance(), "BungeeCord", out.toByteArray());
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean sendAny() {
		return send(Iterables.getFirst(Bukkit.getOnlinePlayers(), null));
	}
	
	public void print() {
		Tools.getInstance().getMessenger().print(Arrays.toString(out.toByteArray()));
	}
}

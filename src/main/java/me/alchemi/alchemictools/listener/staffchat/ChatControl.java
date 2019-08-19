package me.alchemi.alchemictools.listener.staffchat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.mineacademy.chatcontrol.api.ChatControlAPI;
import org.mineacademy.chatcontrol.api.EventCancelledException;
import org.mineacademy.chatcontrol.api.event.ChatChannelEvent;

import me.alchemi.alchemictools.Tools;

public class ChatControl implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChannelChat(ChatChannelEvent e) {
		if (Tools.getInstance().getStaffchat().isListening(e.getSender())) {
			e.setCancelled(true);
		} 
	}
	
	public static String checkMessage(Player sender, String message) {
		try {
			return ChatControlAPI.checkMessage(sender, message).getMessage();
		} catch (EventCancelledException e) {
			return message;
		}
	}

}

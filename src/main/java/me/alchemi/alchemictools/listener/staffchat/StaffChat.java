package me.alchemi.alchemictools.listener.staffchat;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.alchemi.al.Library;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.alchemictools.Config.Hooks;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Config.Options;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.bungee.BungeeMessage;
import me.alchemi.alchemictools.bungee.BungeeMessage.Channel;
import me.alchemi.alchemictools.objects.Permissions;
import me.alchemi.alchemictools.objects.placeholder.Stringer;

public class StaffChat implements Listener{

	private Set<CommandSender> listeners = new HashSet<CommandSender>();
	
	public void addListener(CommandSender sender) {
		listeners.add(sender);
		Tools.getInstance().getMessenger().sendMessage(Messages.STAFFCHAT_START.value(), sender);
	}
	
	public void removeListener(CommandSender sender) {
		listeners.remove(sender);
		Tools.getInstance().getMessenger().sendMessage(Messages.STAFFCHAT_STOP.value(), sender);
	}
	
	public boolean isListening(CommandSender sender) {
		return listeners.contains(sender);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (Options.MESSAGE_MENTIONTAG.asString() != "" && e.getMessage().contains(Options.MESSAGE_MENTIONTAG.asString())) {
			Matcher m = Pattern.compile(Options.MESSAGE_MENTIONTAG.asString() + "\\w*").matcher(e.getMessage());
			
			while(m.find()) {
				Player player = Library.getPlayer(m.group().replace(Options.MESSAGE_MENTIONTAG.asString(), ""));
				
				if (player != null) {
					if (e.getMessage().contains("&")) {
						
						Matcher m1 = Pattern.compile("&[1234567890abcdefklnor]").matcher(e.getMessage());
						if (m1.find() && m1.start() < m.end()) {							
							e.setMessage(e.getMessage().replace(m.group(), Messenger.formatString(Options.MESSAGE_MENTIONCOLOUR.asString() + Options.MESSAGE_MENTIONTAG.asString() + ChatColor.stripColor(player.getDisplayName()) + "&r" + m1.group())));
						}
					} else e.setMessage(e.getMessage().replace(m.group(), Messenger.formatString(Options.MESSAGE_MENTIONCOLOUR.asString() + Options.MESSAGE_MENTIONTAG.asString() + ChatColor.stripColor(player.getDisplayName()) + "&r")));
					player.playSound(player.getLocation(), Options.MESSAGE_MENTIONSOUND.asSound(), 1.0F, 1.0F);
					
				}
			}
		}
		
		if (listeners.contains(e.getPlayer()) && !e.getMessage().startsWith(Options.MESSAGE_BYPASSSTAFFCHATCHARACTER.asString())) {
			send(e.getPlayer(), e.getMessage());
			e.setMessage("");
			e.setFormat("");
			e.setCancelled(true);
		} else if (e.getMessage().startsWith(Options.MESSAGE_BYPASSSTAFFCHATCHARACTER.asString())) {
			e.setMessage(e.getMessage().substring(1));
		}
	}
	
	public void send(CommandSender sender, String message) {
		String toSend;
		
		if (Permissions.STAFFCHAT.check(sender)) {
			toSend = new Stringer(Messages.STAFFCHAT_STAFF)
					.player(sender instanceof Player ? ((Player)sender).getDisplayName() : "Console")
					.message(message)
					.parse(sender)
					.create();
		} else {
			toSend = new Stringer(Messages.STAFFCHAT_NONSTAFF)
					.player(sender instanceof Player ? ((Player)sender).getDisplayName() : "Console")
					.message(message)
					.parse(sender)
					.create();
		}
		
		if (Tools.chatControlPresent) toSend = ChatControl.checkMessage((Player) sender, toSend);
		
		Tools.getInstance().getMessenger().broadcast(toSend, false, player -> Permissions.STAFFCHAT.check(player));
		
		if (Hooks.BUNGEE.asBoolean()) {
			new BungeeMessage(Channel.STAFFCHAT, "ONLINE")
			.string(Options.SERVERNAME.asString())
			.string(sender instanceof Player ? ((Player)sender).getDisplayName() : "Console")
			.bool(Permissions.STAFFCHAT.check(sender))
			.msg(message)
			.sendAny();
		}
	}
	
	public void send(String server, String sender, boolean staff, String message) {
		String toSend;
		
		if (staff) {
			toSend = new Stringer(Messages.STAFFCHAT_STAFFBUNGEE)
					.player(sender)
					.server(server)
					.message(message)
					.create();
		} else {
			toSend = new Stringer(Messages.STAFFCHAT_NONSTAFFBUNGEE)
					.player(sender)
					.server(server)
					.message(message)
					.create();
		}
		
		Tools.getInstance().getMessenger().broadcast(toSend, false, player -> Permissions.STAFFCHAT.check(player));
				
	}
}

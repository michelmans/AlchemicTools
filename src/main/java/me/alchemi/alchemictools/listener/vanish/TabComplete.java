package me.alchemi.alchemictools.listener.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Config.Vanish;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.placeholder.Stringer;

public class TabComplete implements Listener {
	
	@EventHandler
	public void onTabComplete(TabCompleteEvent e) {
		
		if (!e.getSender().hasPermission("alchemictools.vanish.see") && e.getSender() instanceof Player) {
			for (String suggestion : e.getCompletions()) {
				if (Tools.getInstance().getVanishedNames().contains(suggestion)) {
					e.getCompletions().remove(suggestion);
				}
			}
		}
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		
		if (Vanish.CANCEL_COMMANDS.asBoolean() && !e.getPlayer().hasPermission("alchemictools.vanish.see")
				&& !e.isCancelled()) {
			
			for (String sub : e.getMessage().split(" ")) {
				
				if (Tools.getInstance().getVanishedNames().contains(sub)) {
					
					Tools.getInstance().getMessenger().sendMessage(new Stringer(Messages.VANISH_CANCELCOMMANDMESSAGE)
							.player(e.getPlayer())
							.command(e.getMessage())
							.create(), e.getPlayer());
					e.setCancelled(true);
					
				}
				
			}
			
		}
		
	}

}

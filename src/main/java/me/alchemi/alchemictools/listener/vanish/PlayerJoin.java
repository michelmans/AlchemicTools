package me.alchemi.alchemictools.listener.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.alchemi.alchemictools.Config;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.Vanish;
import me.alchemi.alchemictools.objects.Vanish.INDICATION;
import me.alchemi.alchemictools.objects.hooks.ProtocolUtil;

public class PlayerJoin implements Listener{

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		for (Player vanished : Tools.getInstance().getVanishedPlayers()) {
			
			if (e.getPlayer().hasPermission("alchemictools.vanish.see") 
					&& Vanish.INDICATION.valueOf(Config.Vanish.INDICATION.asString()) == INDICATION.SPECTATOR
					&& Tools.protocolPresent) ProtocolUtil.sendGameModePacket(vanished, e.getPlayer(), true);
			else e.getPlayer().hidePlayer(Tools.getInstance(), vanished);
			
		}

		for (Player vanished : Tools.getInstance().getVanishedOPPlayers()) {

			if (e.getPlayer().hasPermission("alchemictools.vanish.special.see") 
					&& Vanish.INDICATION.valueOf(Config.Vanish.INDICATION.asString()) == INDICATION.SPECTATOR
					&& Tools.protocolPresent) ProtocolUtil.sendGameModePacket(vanished, e.getPlayer(), true);
			else e.getPlayer().hidePlayer(Tools.getInstance(), vanished);
			
		}		
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		
		if (Tools.getInstance().getVanishedPlayers().contains(e.getPlayer())
				|| Tools.getInstance().getVanishedOPPlayers().contains(e.getPlayer())) Tools.getInstance().removeVanishedPlayer(e.getPlayer());
		
	}
	
}

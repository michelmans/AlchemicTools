package me.alchemi.alchemictools.listener.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.alchemi.al.objects.meta.PersistentMeta;
import me.alchemi.alchemictools.Config;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.meta.VanishMeta;
import me.alchemi.alchemictools.objects.Vanish;
import me.alchemi.alchemictools.objects.Vanish.INDICATION;
import me.alchemi.alchemictools.objects.hooks.ProtocolUtil;

public class PlayerJoin implements Listener{

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		PersistentMeta.initializePlayer(e.getPlayer(), Tools.getInstance().getName());
		
		for (Player vanished : Tools.getInstance().getVanishedPlayers()) {

			if (e.getPlayer().hasPermission("alchemictools.vanish.see") 
					&& Vanish.INDICATION.valueOf(Config.Vanish.INDICATION.asString()) == INDICATION.SPECTATOR
					&& Tools.protocolPresent) ProtocolUtil.sendGameModePacket(vanished, e.getPlayer(), true);
			else e.getPlayer().hidePlayer(Tools.getInstance(), vanished);
			
		}
		
		if (e.getPlayer().hasPermission("alchemictools.vanish")
				&& PersistentMeta.hasMeta(e.getPlayer(), VanishMeta.class)) {
			
			if (PersistentMeta.getMeta(e.getPlayer(), VanishMeta.class).asBoolean()) {
				Vanish.vanish(e.getPlayer(), PersistentMeta.getMeta(e.getPlayer(), VanishMeta.class).asBoolean());
			}
		}
		
	}
	
}

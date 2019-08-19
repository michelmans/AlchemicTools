package me.alchemi.alchemictools.listener.vanish;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.Invsee;

public class RightClickEntity implements Listener {

	@EventHandler
	public void onEvent(PlayerInteractEntityEvent e) {
		
		if (Tools.getInstance().getVanishedPlayers().contains(e.getPlayer())
				&& e.getRightClicked().getType() == EntityType.PLAYER) {
			if (e.getPlayer().hasPermission("alchemictools.invsee")) Invsee.inventory((Player) e.getRightClicked(), e.getPlayer());
		}
		
	}
	
}

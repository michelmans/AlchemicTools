package me.alchemi.alchemictools.listener.vanish;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import me.alchemi.alchemictools.Tools;

public class PlayerTarget implements Listener {

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent e) {
		if (e.getTarget() == null ||
				Tools.getInstance().getVanishedPlayers() == null
				|| Tools.getInstance().getVanishedPlayers().isEmpty()) return;
		
		if (e.getTarget().getType() == EntityType.PLAYER
				&& Tools.getInstance().getVanishedPlayers().contains((Player)e.getTarget())) {
			e.setCancelled(true);			
		}
	}
	
}

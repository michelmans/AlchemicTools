package me.alchemi.alchemictools.listener.vanish;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PigZombieAngerEvent;

import me.alchemi.alchemictools.Tools;

public class PlayerTarget implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTarget(EntityTargetLivingEntityEvent e) {
		if (e.getTarget() == null ||
				(Tools.getInstance().getVanishedPlayers().isEmpty()
				&& Tools.getInstance().getVanishedOPPlayers().isEmpty())) return;
		
		if (e.getTarget().getType() == EntityType.PLAYER
				&& (Tools.getInstance().getVanishedPlayers().contains((Player)e.getTarget())
						|| Tools.getInstance().getVanishedOPPlayers().contains((Player)e.getTarget()))) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onTarget(EntityTargetEvent e) {
		if (e.getTarget() == null ||
				(Tools.getInstance().getVanishedPlayers().isEmpty()
						&& Tools.getInstance().getVanishedOPPlayers().isEmpty())) return;
		
		if (e.getTarget().getType() == EntityType.PLAYER
				&& (Tools.getInstance().getVanishedPlayers().contains((Player)e.getTarget())
						|| Tools.getInstance().getVanishedOPPlayers().contains((Player)e.getTarget()))) {
			e.setCancelled(true);			
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAnger(PigZombieAngerEvent e) {
		if (e.getTarget() == null ||
				(Tools.getInstance().getVanishedPlayers().isEmpty()
						&& Tools.getInstance().getVanishedOPPlayers().isEmpty())) return;
		
		if (e.getTarget().getType() == EntityType.PLAYER
				&& (Tools.getInstance().getVanishedPlayers().contains((Player)e.getTarget())
						|| Tools.getInstance().getVanishedOPPlayers().contains((Player)e.getTarget()))) {
			e.setCancelled(true);			
		}
	}
	
}

package me.alchemi.alchemictools.objects.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerPreInteractEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final ItemStack item;
	
	public PlayerPreInteractEvent(Player player, ItemStack hand) {
		super(true);
		this.player = player;
		
		this.item = hand.clone();
		
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the item
	 */
	public ItemStack getItem() {
		return item;
	}
	
	

}

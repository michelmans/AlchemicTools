package me.alchemi.alchemictools.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.Permissions;
import me.alchemi.alchemictools.objects.events.PlayerPreInteractEvent;

public class AutoRefill implements Listener {

	private Map<Player, ItemStack> items = new HashMap<Player, ItemStack>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemUse(PlayerItemConsumeEvent e) {
		if (e.getItem().getAmount() == 1
				&& !e.isCancelled()) refill(e.getItem(), e.getPlayer(),
						e.getPlayer().getInventory().getItemInMainHand().equals(e.getItem()) ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemUse(PlayerItemBreakEvent e) {
		refill(e.getBrokenItem(), e.getPlayer(), 
				e.getPlayer().getInventory().getItemInMainHand().equals(e.getBrokenItem()) ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);
	}
	
	@EventHandler
	public void onPreItemInteract(PlayerPreInteractEvent e) {
		items.put(e.getPlayer(), e.getItem().clone());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void itemInteract(PlayerInteractEvent e) {
		
		ItemStack fromKey = items.containsKey(e.getPlayer()) ? items.get(e.getPlayer()) : null;
		ItemStack item = e.getHand() == EquipmentSlot.HAND ? e.getPlayer().getInventory().getItemInMainHand() : e.getPlayer().getInventory().getItemInOffHand();
		
		if (e.getAction() == Action.LEFT_CLICK_AIR 
				|| e.getAction() == Action.LEFT_CLICK_BLOCK
				|| MaterialWrapper.getWrapper(item).isEdible()
				|| (e.getHand() == EquipmentSlot.HAND 
						&& (item == null || !MaterialWrapper.getWrapper(item).isBlock()
								|| MaterialWrapper.getWrapper(item) == MaterialWrapper.AIR.getMaterial())
						&& (e.getPlayer().getInventory().getItemInOffHand() != null
								&& MaterialWrapper.getWrapper(e.getPlayer().getInventory().getItemInOffHand()).isBlock()))) return;
		
		if (fromKey != null
				&& item != null
				&& item.getMaxStackSize() != 1
				&& item.getAmount() <= 1
				&& !e.isCancelled()) {
			refill(fromKey, e.getPlayer(), e.getHand());
		}
		items.put(e.getPlayer(), null);
		
	}
	
	protected void refill(ItemStack item, Player player, EquipmentSlot slot) {
		
		if (player.getGameMode() == GameMode.CREATIVE ||
				player.getGameMode() == GameMode.SPECTATOR ||
				!Permissions.TOOLS_AUTOREFILL.check(player)) return; 
		
		Bukkit.getScheduler().runTaskLater(Tools.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				refillSchedule(item, player, slot);
				
			}
		}, 1);
	}
	
	protected void refillSchedule(ItemStack item, Player player, EquipmentSlot slot) {
		
		PlayerInventory inventory = player.getInventory();
		Material m = MaterialWrapper.getWrapper(item);
		
		if (inventory.contains(m)) {
			int ind = -1;
			for (int i : inventory.all(m).keySet()) {
				if (inventory.getItem(i) != item) ind = i;
			}
			if (ind != -1) {
				ItemStack newItem = inventory.getItem(ind).clone();
				
				switch(slot) {
				case HAND:
					if (inventory.getItemInMainHand() == null
						|| inventory.getItemInMainHand().getAmount() == 0) {
						inventory.setItemInMainHand(newItem);
						inventory.setItem(ind, null);
					}
					break;
				case OFF_HAND:
					if (inventory.getItemInOffHand() == null
					|| inventory.getItemInOffHand().getAmount() == 0) {
						inventory.setItemInOffHand(newItem);
						inventory.setItem(ind, null);
					}
					break;
				default:
					break;
				}
				player.updateInventory();
				player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP, 0.25F, 1.0F);
			}
		}		
	}
}

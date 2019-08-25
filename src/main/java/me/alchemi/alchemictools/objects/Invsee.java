package me.alchemi.alchemictools.objects;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.placeholder.Stringer;
import net.md_5.bungee.api.ChatColor;

public class Invsee {
	
	private static Map<UUID, Integer> tasks = new HashMap<UUID, Integer>();
	
	public static void inventory(Player inventory, Player viewer) {
	
		viewer.openInventory(inventory.getInventory());
		
	}
	
	public static void armour(Player inventory, Player viewer) {
		
		Inventory armourInventory = Bukkit.createInventory(inventory, 9, "Armour");
		armourInventory.setContents(Lists.reverse(Arrays.asList(inventory.getInventory().getArmorContents())).toArray(new ItemStack[4]));
		
		armourInventory.setItem(8, inventory.getInventory().getItemInOffHand());
		
		viewer.openInventory(armourInventory);
				
	}
	
	public static void potions(Player inventory, Player viewer) {
		
		Collection<PotionEffect> effects = inventory.getActivePotionEffects();
		
		if (effects.isEmpty()) {
			Tools.getInstance().getMessenger().sendMessage(new Stringer(Messages.INVSEE_NOPOTIONS)
					.player(inventory)
					.parse(inventory)
					.create(), viewer);
			return;
		}
				
		
		int size = effects.size()%9 == 0 ? effects.size()/9 : Integer.valueOf(String.valueOf(effects.size()/9.0).replaceFirst("\\.\\d+", "") + 1); 
		Inventory potionsInventory = Bukkit.createInventory(inventory, size * 9, "Potions");
		
		for (PotionEffect effect : effects) {
			ItemStack potionStack = new ItemStack(MaterialWrapper.POTION.getMaterial(), 1);
			PotionMeta potionMeta = (PotionMeta) potionStack.getItemMeta();
			potionMeta.setColor(effect.getType().getColor());
			
			String name2 = effect.getType().getName().toLowerCase();
			String name = "";
			for (String n : name2.split(" ")) {
				name = name + n.substring(0, 1).toUpperCase() + n.substring(1) + " ";
			}
			name = name.trim();
			
			potionMeta.setDisplayName(name + " " + RomanNumber.toRoman(effect.getAmplifier() + 1));
			
			String minutes = String.valueOf(effect.getDuration()/1200.0).replaceFirst("\\.\\d+", "");
			String seconds = String.valueOf(effect.getDuration()/20 - Integer.valueOf(minutes) * 60);
			
			if (seconds.length() < 2) {
				seconds = "0" + seconds;
			}
			
			potionMeta.setLore(Arrays.asList(Messenger.formatString("&7" + minutes + ":" + seconds)));
			potionStack.setItemMeta(potionMeta);
			potionsInventory.addItem(potionStack);
		}
		
		tasks.put(viewer.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(Tools.getInstance(), new Runnable() {
			
			@Override
			public void run() {				
				
				int count = 0;
				int index = -1;
				
				for (ItemStack stack : potionsInventory) {
					index ++;
					if (stack == null) continue;
					
					count ++;
					
					ItemMeta meta = stack.getItemMeta();
					
					String duration = ChatColor.stripColor(meta.getLore().get(0));
					
					int minutes = Integer.valueOf(duration.replaceFirst(":..", ""));
					int seconds = Integer.valueOf(duration.replaceFirst(".+:", ""));
					
					if (seconds == 0 && minutes == 0) {
						potionsInventory.clear(index);
						continue;
					
					} else if (seconds == 0) {
						minutes --;
						seconds = 59;
					
					} else {
						seconds --;
					}
					
					String secondsString = String.valueOf(seconds);
					if (secondsString.length() < 2) {
						secondsString = "0" + secondsString;
					}
					
					meta.setLore(Arrays.asList(Messenger.formatString("&7" + minutes + ":" + secondsString)));
					stack.setItemMeta(meta);
				}
				
				if (count == 0) {
					Bukkit.getScheduler().cancelTask(tasks.get(viewer.getUniqueId()));
				}
				
			}
		}, 20, 20));
		
		viewer.openInventory(potionsInventory);
		
		Bukkit.getPluginManager().registerEvents(new Invsee().new InventoryListener(viewer, tasks.get(viewer.getUniqueId())), Tools.getInstance());
		
	}

	private class InventoryListener implements Listener {
		
		Player player;
		int task;
		
		public InventoryListener(Player player, int task) {
			this.player = player;
			this.task = task;
		}
		
		@EventHandler
		public void onInventoryClose(InventoryCloseEvent e) {
			if (e.getPlayer().equals(player)) {
				Bukkit.getScheduler().cancelTask(task);
				HandlerList.unregisterAll(this);
			}
		}
		
		@EventHandler
		public void onNewEffect(EntityPotionEffectEvent e) {
			switch(e.getAction()) {
			case ADDED:
				ItemStack potionStack = new ItemStack(MaterialWrapper.POTION.getMaterial(), 1);
				PotionMeta potionMeta = (PotionMeta) potionStack.getItemMeta();
				potionMeta.setColor(e.getNewEffect().getType().getColor());
				
				String name2 = e.getNewEffect().getType().getName().toLowerCase();
				String name = "";
				for (String n : name2.split(" ")) {
					name = name + n.substring(0, 1).toUpperCase() + n.substring(1) + " ";
				}
				name = name.trim();
				
				potionMeta.setDisplayName(name + " " + RomanNumber.toRoman(e.getNewEffect().getAmplifier() + 1));
				
				String minutes = String.valueOf(e.getNewEffect().getDuration()/1200.0).replaceFirst("\\.\\d+", "");
				String seconds = String.valueOf(e.getNewEffect().getDuration()/20 - Integer.valueOf(minutes) * 60);
				
				if (seconds.length() < 2) {
					seconds = "0" + seconds;
				}
				
				potionMeta.setLore(Arrays.asList(Messenger.formatString("&7" + minutes + ":" + seconds)));
				potionStack.setItemMeta(potionMeta);
				player.getOpenInventory().getTopInventory().addItem(potionStack);
				break;
			case CHANGED:
				break;
			case CLEARED:
				player.getOpenInventory().getTopInventory().clear();
				break;
			case REMOVED:
				for (ItemStack stack : player.getOpenInventory().getTopInventory()) {
					if (stack.getItemMeta().getDisplayName().toLowerCase().contains(e.getModifiedType().getName().toLowerCase())) {
						player.getOpenInventory().getTopInventory().remove(stack);
					}
				}
				break;
			
			}
		}
		
	}
	
}
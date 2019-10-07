package me.alchemi.alchemictools.objects;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.alchemi.al.api.MaterialWrapper;

public class Sorter {

	private PlayerInventory inventory;
	private Player player;
	
	public Sorter(Player player) {
		this.inventory = player.getInventory();
		this.player = player;
	}
	
	public enum SortMethod {
		ABC, AMOUNT, ENCHANT;
	}
	
	public void sort(boolean ignoreHotbar) {

		TreeMap<SortingItem, Integer> tree = new TreeMap<SortingItem, Integer>();
		
		
		for (int slot = 0; slot < 36; slot++) {
			if (ignoreHotbar && slot <= 8) continue;
			
			if (inventory.getItem(slot) != null) tree.put(new SortingItem(inventory.getItem(slot)), slot);
		}
		
		ItemStack[] contents = new ItemStack[36];
		Iterator<Entry<SortingItem, Integer>> iter = tree.entrySet().iterator();
		for (int slot = 0; slot < 36; slot++) {
			
			if (ignoreHotbar && slot <= 8) {
				contents[slot] = inventory.getItem(slot);
				continue;
			}
			
			if (iter.hasNext()) {
				contents[slot] = iter.next().getKey().getItem();
			}
			
		}
		inventory.setContents(contents);
		player.updateInventory();
		
	}
	
	public static String getItemDisplay(ItemStack item) {
		return item.hasItemMeta() 
				? item.getItemMeta().hasDisplayName() 
						? item.getItemMeta().getDisplayName() 
								: MaterialWrapper.getWrapper(item).getKey().getKey().toLowerCase() 
								: MaterialWrapper.getWrapper(item).getKey().getKey().toLowerCase();
	}
	
	public class SortingItem implements Comparable<SortingItem>{

		private final ItemStack item;
		private final String displayName;
		private final UUID uuid;
		
		public SortingItem(ItemStack item) {
			this.item = item.clone();
			this.displayName = getItemDisplay(item);
			uuid = UUID.randomUUID();
		}

		@Override
		public int compareTo(SortingItem o) {
			int name = displayName.compareTo(o.getDisplayName());
			int amount = item.getAmount() < o.getItem().getAmount() ? 1 : -1;
			int enchant = 0;
			
			if (item.hasItemMeta()
					&& item.getItemMeta().hasEnchants()
					&& !(o.getItem().hasItemMeta() 
							&& o.getItem().getItemMeta().hasEnchants())) {
				int enchantsO = o.getItem().getItemMeta().getEnchants().size();
				int enchants = item.getItemMeta().getEnchants().size();
				
				int[] integers = new int[enchants * enchantsO];
				System.out.println(integers.length);
				int i = 0;
				for (Entry<Enchantment, Integer> ench : item.getItemMeta().getEnchants().entrySet()) {
					SortingEnchant enchantS = new SortingEnchant(ench.getKey(), ench.getValue());
					for (Entry<Enchantment, Integer> enchO : o.getItem().getItemMeta().getEnchants().entrySet()) {
						System.out.println(i);
						
						integers[i] = enchantS.compareTo(new SortingEnchant(enchO.getKey(), enchO.getValue()));
						
						i++;
					}
				}
				
			}
			
			if (displayName.compareTo(o.getDisplayName()) == 0) {
				if (item.getAmount() == o.getItem().getAmount()) {
					return new Random().nextBoolean() ? -1 : 1;
				}
				else return item.getAmount() < o.getItem().getAmount() ? 1 : -1;
			}
			return displayName.compareTo(o.getDisplayName());
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public ItemStack getItem() {
			return item;
		}
		
		@Override
		public String toString() {
			return item.toString();
		}

		/**
		 * @return the uuid
		 */
		public UUID getUuid() {
			return uuid;
		}
		
	}

	public class SortingEnchant implements Comparable<SortingEnchant>{

		private final Enchantment enchant;
		private final int level;
		
		public SortingEnchant(Enchantment enchant, int level) {
			this.enchant = enchant;
			this.level = level;
		}
		
		@Override
		public int compareTo(SortingEnchant o) {
			int enchantName = enchant.getKey().getKey().compareTo(o.getEnchant().getKey().getKey());
			if (enchantName == 0) return level < o.getLevel() ? 1: -1;
			return enchantName;
		}
		
		public Enchantment getEnchant() {
			return enchant;
		}
		public int getLevel() {
			return level;
		}
		
	}
}

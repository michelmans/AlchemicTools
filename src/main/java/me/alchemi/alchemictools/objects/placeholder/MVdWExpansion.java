package me.alchemi.alchemictools.objects.placeholder;

import java.util.function.BiFunction;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.alchemi.alchemictools.Tools;

public class MVdWExpansion {

	public MVdWExpansion() {
		
		registerPlaceholder("playercount", (player, oPlayer) -> {
			
			if (player == null) {
				return String.valueOf(Bukkit.getOnlinePlayers().size() - Tools.getInstance().getVanishedPlayers().size());
			} else {
				return player.hasPermission("alchemictools.vanish.see") ? String.valueOf(Bukkit.getOnlinePlayers().size()) 
						: String.valueOf(Bukkit.getOnlinePlayers().size() - Tools.getInstance().getVanishedPlayers().size());
			}
			
		});
		
		registerPlaceholder("vanished", (player, oPlayer) -> {
			
			if (player == null) {
				return "false";
			} else {
				return String.valueOf(Tools.getInstance().getVanishedPlayers().contains(player));
			}
			
		});
		
		registerPlaceholder("vanishedcount", (Player, oPlayer) -> {
			return String.valueOf(Tools.getInstance().getVanishedPlayers().size());
		});
		
	}
	
	public void registerPlaceholder(String id, BiFunction<Player, OfflinePlayer, String> bif) {
		
		PlaceholderAPI.registerPlaceholder(Tools.getInstance(), "alchemictools_" + id, new PlaceholderReplacer() {
			
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if (e.getPlaceholder().startsWith("alchemictools_") && e.getPlaceholder().replace("alchemictools_", "").equals(id)) {
					if (e.isOnline()) return bif.apply(e.getPlayer(), null);
					else return bif.apply(null, e.getOfflinePlayer());
				}
				return null;
			}
		});
		
	}
	
}

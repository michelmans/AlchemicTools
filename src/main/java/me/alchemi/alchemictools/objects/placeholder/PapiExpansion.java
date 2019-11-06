package me.alchemi.alchemictools.objects.placeholder;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.Permissions;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PapiExpansion extends PlaceholderExpansion{

	@Override
	public boolean canRegister() {
		return true;
	}
	
	@Override
	public String getAuthor() {
		return "Alchemi";
	}

	@Override
	public String getIdentifier() {
		return Tools.getInstance().getName();
	}

	@Override
	public String getVersion() {
		return Tools.getInstance().getDescription().getVersion();
	}
	
	@Override
	public boolean persist() {
		return true;
	}
	
	@Override
	public String onPlaceholderRequest(Player player, String id) {
		
		if (id.equalsIgnoreCase("playercount")) {
			return player.hasPermission("alchemictools.vanish.see") 
					? Permissions.VANISH_SPECIAL_SEE.check(player) 
							? String.valueOf(Bukkit.getOnlinePlayers().size())
									: String.valueOf(Tools.getInstance().getOnlineOPPlayers())
									: String.valueOf(Tools.getInstance().getOnlinePlayers());
		} else if (id.equalsIgnoreCase("vanished")) {
			return String.valueOf(Tools.getInstance().getVanishedPlayers().contains(player));
		} else if (id.equalsIgnoreCase("vanishedplayers")) {
			return String.valueOf(Tools.getInstance().getVanishedPlayers().size());
		}
		
		return null;
	}
	
	@Override
	public String onRequest(OfflinePlayer p, String id) {
		
		if (id.equalsIgnoreCase("playercount")) {
			return String.valueOf(Tools.getInstance().getOnlinePlayers());
		} else if (id.equalsIgnoreCase("vanished")) {
			return "false";
		} else if (id.equalsIgnoreCase("vanishedplayers")) {
			return String.valueOf(Tools.getInstance().getVanishedPlayers().size());
		}
		
		return null;
	}
	
}

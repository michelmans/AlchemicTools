package me.alchemi.alchemictools.command.tabcomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.objects.base.TabCompleteBase;
import me.alchemi.alchemictools.objects.Permissions;

public class InvseeTabComplete extends TabCompleteBase{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

		List<Object> list = new ArrayList<Object>();

		if (!(sender instanceof Player 
				|| Permissions.INVSEE.check(sender)))
			return Arrays.asList("");

		if (args.length == 1) {
			list.addAll(Bukkit.getOnlinePlayers().stream()
					.map(player -> player.getDisplayName())
					.collect(Collectors.toSet()));
			
		} else if (args.length > 1) {
			list.add("inventory");
			list.add("potions");
			list.add("armour");
		}
		
		return returnSortSuggest(list, args);
	}
	
}

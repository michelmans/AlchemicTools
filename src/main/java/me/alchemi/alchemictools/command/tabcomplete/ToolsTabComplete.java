package me.alchemi.alchemictools.command.tabcomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.objects.base.TabCompleteBase;

public class ToolsTabComplete extends TabCompleteBase {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		List<Object> list = new ArrayList<Object>();

		if (!(sender instanceof Player && sender.hasPermission(command.getPermission())))
			return Arrays.asList("");

		if (args.length <= 1) {
			if (sender.hasPermission("alchemictools.tools.reload")) list.add("reload");
			if (sender.hasPermission("alchemictools.tools.changeuuid")) list.add("migrate-uuid");
			if (sender.hasPermission("alchemictools.tools.restore")) list.add("restore-backups");
			if (sender.hasPermission("alchemictools.tools.getuuid")) list.add("get-uuid");
		} else if (args.length <= 2) {
			if (args[0].equals("get-uuid")) {
				list.addAll(Arrays.stream(Bukkit.getOfflinePlayers())
						.map(OfflinePlayer::getName)
						.collect(Collectors.toSet()));
				
			}
		}
		
		return returnSortSuggest(list, args);
	}
	
}

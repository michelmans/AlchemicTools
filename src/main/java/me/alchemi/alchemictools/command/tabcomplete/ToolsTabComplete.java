package me.alchemi.alchemictools.command.tabcomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		}
		
		return returnSortSuggest(list, args);
	}
	
}

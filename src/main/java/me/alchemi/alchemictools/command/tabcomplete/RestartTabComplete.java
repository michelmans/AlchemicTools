package me.alchemi.alchemictools.command.tabcomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.alchemi.al.objects.base.TabCompleteBase;

public class RestartTabComplete extends TabCompleteBase{
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		List<Object> list = new ArrayList<Object>();

		if (!sender.hasPermission(command.getPermission()))
			return Arrays.asList("");
		
		if (args.length <= 1) {
			list.add("countermanded");
			list.add("now");
			list.add(30);
			list.add(25);
			list.add(10);
		}

		return returnSortSuggest(list, args);
	}
	
}

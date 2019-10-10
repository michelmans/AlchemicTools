package me.alchemi.alchemictools.command.tabcomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.objects.base.TabCompleteBase;
import me.alchemi.alchemictools.objects.Permissions;

public class VanishTabComplete extends TabCompleteBase{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

		List<Object> list = new ArrayList<Object>();

		if (sender instanceof Player
				&& (!Permissions.VANISH.check(sender)
						|| !Permissions.VANISH_SPECIAL.check(sender)))
			return Arrays.asList("");
		
		if (args.length >= 0) {
			list.add("special");
		}
		
		return returnSortSuggest(list, args);
	}
	
}

package me.alchemi.alchemictools.command;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.report.bug.ReporterManager;

public class BugCommand extends CommandBase {

	public BugCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value(), Messages.COMMANDS_WRONGFORMAT.value());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission(command.getPermission())) {
			sendNoPermission(sender, command);
			return true;
		} else if (args.length < 1) {
			sendUsage(sender, command.getUsage());
			return true;
		}
		
		String title = compileArgs(args);
		
		int i = 0;
		for (String s : args) {
			if (s.endsWith("\"")) break;
			i++;
		}
		String desc = compileArgs(Arrays.copyOfRange(args, i + 1, args.length));
		
		if (desc.isEmpty()) ReporterManager.getInstance().report(title, sender);
		else ReporterManager.getInstance().report(title, desc, sender);
		
		return true;
	}
	
	@Override
	protected String compileArgs(String[] args) {
		String string = "";
		for (String s : args) {
			if (string.trim().startsWith("\"") 
					&& string.trim().endsWith("\"")) break;
			
			string = string.concat(" " + s);
		}
		return string.trim().replaceAll("((?<!\\\\)\\\")", "");
	}
	
}

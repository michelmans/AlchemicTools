package me.alchemi.alchemictools.command;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import me.alchemi.al.Library;
import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Config.Options;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.RestartService;

public class RestartCommand extends CommandBase implements Listener {

	public RestartCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value(), Messages.COMMANDS_WRONGFORMAT.value());
		Bukkit.getPluginManager().registerEvents(this, Tools.getInstance());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission(command.getPermission())) {
			
			if (args.length == 0) {
				try {
					new RestartService(sender);
				} catch (NoClassDefFoundError e) {
					e.printStackTrace();
					System.err.println(System.getProperty("java.classpath"));
				}
			} else if (args.length == 1 && Arrays.asList("stop", "cancel", "halt", "countermanded").contains(args[0])) {
				RestartService.cancel(sender);
			} else if (args.length == 1 && Library.testIfNumber(args[0])) {
				new RestartService(sender, Integer.valueOf(args[0]));
			} else if (args.length == 1 && args[0].equals("now")) {
				new RestartService(sender, 0);				
			} else if (args.length >= 1 && Library.testIfNumber(args[0])) {
				new RestartService(sender, Integer.valueOf(args[0]), compileArgs(Arrays.copyOfRange(args, 1, args.length)));
			} else if (args.length >= 1 && args[0].equals("now")) {
				new RestartService(sender, 0, compileArgs(Arrays.copyOfRange(args, 1, args.length)));
			} else if (args.length >= 1) {
				new RestartService(sender, Options.RESTART_DEFAULTDELAY.asInt(), compileArgs(args));
			} else {
				new RestartService(sender);
			}
			
		} else {
			sendNoPermission(sender, command);
		}
		return true;
	}
}

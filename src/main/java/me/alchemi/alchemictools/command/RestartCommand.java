package me.alchemi.alchemictools.command;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.alchemi.al.Library;
import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Config.Options;
import me.alchemi.alchemictools.objects.Restart;

public class RestartCommand extends CommandBase implements Listener {

	public RestartCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value());
		Bukkit.getPluginManager().registerEvents(this, Tools.getInstance());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission(command.getPermission())) {
			
			if (args.length == 0) {
				new Restart(sender);
			} else if (args.length == 1 && Arrays.asList("stop", "cancel", "halt", "countermanded").contains(args[0])) {
				Restart.instance.cancel(sender);
			} else if (args.length == 1 && Library.testIfNumber(args[0])) {
				new Restart(sender, Integer.valueOf(args[0]));
			} else if (args.length == 1 && args[0].equals("now")) {
				new Restart(sender, 0);				
			} else if (args.length >= 1 && Library.testIfNumber(args[0])) {
				new Restart(sender, Integer.valueOf(args[0]), compileArgs(Arrays.copyOfRange(args, 1, args.length)));
			} else if (args.length >= 1 && args[0].equals("now")) {
				new Restart(sender, 0, compileArgs(Arrays.copyOfRange(args, 1, args.length)));
			} else if (args.length >= 1) {
				new Restart(sender, Options.RESTART_DEFAULTDELAY.asInt(), compileArgs(args));
			} else {
				new Restart(sender);
			}
			
		} else {
			sendNoPermission(sender, command);
		}
		return true;
	}
	
	@EventHandler
	public void onPreCommand(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().startsWith("/restart")) {
			e.setMessage(e.getMessage().replace("/restart", "/alchemictools:restart"));
		}
	}	
}

package me.alchemi.alchemictools.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.alchemi.al.Library;
import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.listener.commandstuff.DummyCommand;
import me.alchemi.alchemictools.objects.placeholder.Stringer;
import me.alchemi.alchemictools.objects.uuidconverting.IConverter;
import me.alchemi.alchemictools.objects.uuidconverting.UUIDResolver;

public class ToolsCommand extends CommandBase{

	public ToolsCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender.hasPermission(command.getPermission())) {
			if (args.length == 1) {
				if (args[0].equals("reload")) {
					if (!sender.hasPermission("alchemictools.tools.reload")) {
						sendNoPermission(sender, new DummyCommand("tools reload"));
						return true;
					}
					Tools.getInstance().getConf().reload();
					messenger.sendMessage(Messages.COMMANDS_RELOAD.value(), sender);
					
				} else if (args[0].equals("migrate-uuid")) {
					
					if (!sender.hasPermission("alchemictools.tools.migrateuuid")) {
						sendNoPermission(sender, new DummyCommand("tools migrate-uuid"));
						return true;
					}
					Tools.getInstance().getIdResolver().apply();
					
				} else if (args[0].equals("restore-backups")) {
					if (!sender.hasPermission("alchemictools.tools.restore")) {
						sendNoPermission(sender, new DummyCommand("tools restore-backups"));
						return true;
					}
					IConverter.restoreBackups();
				} else return false;
			} else if (args.length == 3) {
				if (args[0].equals("get-uuid")) {
					if (!sender.hasPermission("alchemictools.tools.getuuid")) {
						sendNoPermission(sender, new DummyCommand("tools get-uuid"));
						return true;
					}
					
					OfflinePlayer oldPlayer = Library.getOfflinePlayer(args[1]);
					if (oldPlayer == null) {
						oldPlayer = Bukkit.getOfflinePlayer(UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[1]).getBytes()));
					}
					
					
					if (oldPlayer != null) {
						UUIDResolver idResolver = Tools.getInstance().getIdResolver();
						idResolver.requestCustomUUIDWrapper(oldPlayer, args[2], idResolver::putOnlineUUID, idResolver::addFailedPlayer);
					} else {
						messenger.sendMessage(new Stringer(Messages.UUID_FAILED)
								.player(args[1]), sender);
					}
					
				}
			}
		} else {
			sendNoPermission(sender, command);
		}
		return true;
		
	}
	
}

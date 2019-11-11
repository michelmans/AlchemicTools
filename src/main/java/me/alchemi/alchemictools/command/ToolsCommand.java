package me.alchemi.alchemictools.command;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.alchemi.al.Library;
import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Config.Bugs;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Config.Options;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.listener.commandstuff.DummyCommand;
import me.alchemi.alchemictools.objects.Permissions;
import me.alchemi.alchemictools.objects.placeholder.Stringer;
import me.alchemi.alchemictools.objects.report.bug.ReporterManager;
import me.alchemi.alchemictools.objects.report.bug.Trello;
import me.alchemi.alchemictools.objects.uuidconverting.IConverter;
import me.alchemi.alchemictools.objects.uuidconverting.UUIDResolver;

public class ToolsCommand extends CommandBase{

	public ToolsCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value(), Messages.COMMANDS_WRONGFORMAT.value());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender.hasPermission(command.getPermission())) {
			if (args.length >= 1) {
				if (args[0].equals("reload")) {
					
					return reload(sender);
					
				} else if (args[0].equals("migrate-uuid") && Options.UUIDCONVERSION.asBoolean()) {
					
					return migrateUuid(sender);
					
				} else if (args[0].equals("restore-backups")) {
					
					return restoreBackups(sender);

				} else if (args[0].equals("get-uuid") && Options.UUIDCONVERSION.asBoolean()) {
					
					return getUuid(sender, args);
					
				} else if (args[0].equals("bugs")) {
					
					return bugs(sender, args);
					
				} else return false;
		
			}
		} else {
			sendNoPermission(sender, command);
		}
		return true;
		
	}
	
	private boolean bugs(CommandSender sender, String[] args) {
		
		if (args.length > 1) {
			if (args[1].equals("activate")) return activate(sender, args);
			
			else if (args[1].equals("deactivate")) return deactivate(sender, args);
			
			else if (args[1].equals("trello")) return trello(sender, args);
		}
		
		return true;
	}
	
	private boolean activate(CommandSender sender, String[] args) {

		if (!Permissions.TOOLS_BUGS_ACTIVATE.check(sender)) {
			sendNoPermission(sender, new DummyCommand("tools bugs activate <reporter>"));
			return true;
		} else if (args.length != 3) {
			sendUsage(sender, "/tools bugs activate <reporter>");
			return true;
		}
		
		Entry<Boolean, String> result = ReporterManager.getInstance().activate(args[2]);
		
		if (!result.getKey()) {
		
			Tools.getInstance().getMessenger().sendMessage(result.getValue(), sender);
			
		}
		
		return true;
		
	}
	
	private boolean deactivate(CommandSender sender, String[] args) {
	
		if (!Permissions.TOOLS_BUGS_DEACTIVATE.check(sender)) {
			sendNoPermission(sender, new DummyCommand("tools bugs deactivate <reporter>"));
			return true;
		} else if (args.length != 3) {
			sendUsage(sender, "/tools bugs deactivate <reporter>");
			return true;
		}
		
		ReporterManager.getInstance().deactivate(args[2]);
		Tools.getInstance().getMessenger().sendMessage("&cDeactivated " + args[2], sender);
		
		return true;
		
	}
	
	private boolean trello(CommandSender sender, String[] args) {
		
		if (args.length < 3) {
			sendUsage(sender, "/tools bugs trello token|generateToken");
			return true;
		}
		
		if (args[2].equals("token")) {
			
			if (args.length != 4) {
				sendUsage(sender, "/tools bugs trello token <token>");
				return true;
			} else if (!Permissions.TOOLS_BUGS_TRELLO_TOKEN.check(sender)) {
				sendNoPermission(sender, new DummyCommand("tools bugs trello token"));
				return true;
			}
			
			try {
				Bugs.TRELLO_TOKEN.set(args[3]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			ReporterManager.getInstance().activate("trello");
			
		} else if (args[2].equals("generateToken")) {
			
			if (!Permissions.TOOLS_BUGS_TRELLO_TOKEN_GENERATE.check(sender)) {
				sendNoPermission(sender, new DummyCommand("tools bugs trello generateToken"));
				return true;
			}
			
			((Trello) ReporterManager.getInstance().get("trello")).generateToken(sender);
			
		}
		return true;
	}

	private boolean reload(CommandSender sender) {
		if (!Permissions.TOOLS_RELOAD.check(sender)) {
			sendNoPermission(sender, new DummyCommand("tools reload"));
			return true;
		}
		
		Tools.getInstance().getConf().reload();
		Tools.getInstance().reload();
		
		messenger.sendMessage(Messages.COMMANDS_RELOAD.value(), sender);
		return true;
	}
	
	private boolean migrateUuid(CommandSender sender) {
		if (!Permissions.TOOLS_MIGRATEUUID.check(sender)) {
			sendNoPermission(sender, new DummyCommand("tools migrate-uuid"));
			return true;
		}
		Tools.getInstance().getIdResolver().apply();
		return true;
	}
	
	private boolean restoreBackups(CommandSender sender) {
		if (!Permissions.TOOLS_RESTORE.check(sender)) {
			sendNoPermission(sender, new DummyCommand("tools restore-backups"));
			return true;
		}
		IConverter.restoreBackups();
		return true;
	}
	
	private boolean getUuid(CommandSender sender, String[] args) {
		if (!Permissions.TOOLS_GETUUID.check(sender)) {
			sendNoPermission(sender, new DummyCommand("tools get-uuid"));
			return true;
		} else if (args.length < 3) return false;
		
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
		return true;
	}
}

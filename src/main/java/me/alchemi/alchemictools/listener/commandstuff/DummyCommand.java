package me.alchemi.alchemictools.listener.commandstuff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DummyCommand extends Command {

	public DummyCommand(String name) {
		super(name);
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		return false;
	}

}

package me.alchemi.alchemictools.objects.report.bug;

import java.util.Map;

import org.bukkit.command.CommandSender;

public interface Reporter {

	Map.Entry<Boolean, String> activate();
	
	void deactivate();
	
	String getId();
	
	boolean isActive();
	
	void report(String title, String description, CommandSender reporter);
	
	void report(String title, CommandSender reporter);
	
}

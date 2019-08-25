package me.alchemi.alchemictools.objects.uuidconverting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.events.OnlineUUIDApplyEvent;

public class GlobalConverter implements IConverter {

	@Override
	@EventHandler
	public void onNewUUIDApply(OnlineUUIDApplyEvent e) {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				List<File> files = getFiles(new File(Bukkit.getWorldContainer(), "plugins"));
				
				Pattern regex = Pattern.compile("(........-....-....-....-............)");
				Matcher m;
				File newFile;
				IConverter.backupDir(new File(Bukkit.getWorldContainer(), "plugins"));
				Tools.getInstance().getMessenger().print("&cStarting GLOBAL uuid migration!\n&4--DO NOT JOIN UNTIL COMPLETE--");				
				for (File file : files) {
					Tools.getInstance().getMessenger().print("&9Trying to migrate " + file.getPath());
					m = regex.matcher(file.getName());
					if (m.find()) {
						try {
							UUID id = UUID.fromString(m.group());
							if (e.getOldUUIDs().contains(id)) {
								newFile = new File(file.getParentFile(), file.getName().replace(id.toString(), e.getNewUUID(id).toString()));
							} else newFile = file;
						} catch (IllegalArgumentException ex) {
							newFile = file;
						}
					} else newFile = file;
					
					IConverter.migrateFile(e, file, newFile);
				}
				Tools.getInstance().getMessenger().print("&aUUID migration complete!");
			}
		}.runTaskAsynchronously(Tools.getInstance());

	}
	
	private List<File> getFiles(File path){
		List<File> files = new ArrayList<File>();
		for (File file : path.listFiles()) {
			if (file.isDirectory()) {
				files.addAll(getFiles(file));
			} else {
				if (file.getName().endsWith(".yml")
						|| file.getName().endsWith(".json")
						|| file.getName().endsWith(".txt")) files.add(file);
			}
		}
		return files;
	}

}

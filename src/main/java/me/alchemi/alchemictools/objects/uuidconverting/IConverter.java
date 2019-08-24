package me.alchemi.alchemictools.objects.uuidconverting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.events.OnlineUUIDApplyEvent;

public interface IConverter extends Listener {

	public static final String FOLDER_SUFFIX = "-old";
	
	@EventHandler
	public void onNewUUIDApply(OnlineUUIDApplyEvent e);
	
	public static void restoreBackups() {
		File root = Bukkit.getWorldContainer();
		for (File f : root.listFiles()) {
			if (f.isDirectory()) {
				
				checkDirs(f);
				
			}
		}
		Tools.getInstance().getMessenger().print("&2Backup restore done.");
		Tools.getInstance().getIdResolver().onEnable(Bukkit.getServer());
	}
	
	public static void checkDirs(File parent) {
		for (File f : parent.listFiles()) {
			if (f.isDirectory() 
					&& f.getName().endsWith(FOLDER_SUFFIX)) {
				Tools.getInstance().getMessenger().print("Restoring from " + f.getName());
				String fName = f.getName().replace(FOLDER_SUFFIX, "");
				File newDir = new File(f.getParentFile(), fName);
				try {
					Files.walk(newDir.toPath())
						.sorted(Comparator.reverseOrder())
						.map(Path::toFile)
						.forEach(File::delete);
					
					FileUtils.copyDirectory(f, newDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if (f.isDirectory()) {
				checkDirs(f);
			}
		}
		
	}
	
	public static File migrateFile(OnlineUUIDApplyEvent e, File file, File newFile) { 
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			
			boolean edited = false;
			
			String newLines = "";
			String line;
			while ((line = br.readLine()) != null) {
				Matcher m = Pattern.compile("(........-....-....-....-............)").matcher(line);
				while (m.find()) {
					String group = m.group();
					try {
						UUID possibleUUID = UUID.fromString(group);
						if (e.getOldUUIDs().contains(possibleUUID)) {
							 line = line.replace(group, e.getNewUUID(possibleUUID).toString());
							 edited = true;
						}
												
					} catch (IllegalArgumentException e2) {}
					
				}
				newLines = newLines.concat(line + "\n");
								
			}
			
			if (edited) {
				newFile.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
				bw.write(newLines);
				bw.close();
				file.delete();
				Tools.getInstance().getMessenger().print("&aMigrated " + file.getName() + " to " + newFile.getName());
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return newFile;
	}

	public static void backupPluginDir(Plugin plugin) {
		backupDir(plugin.getDataFolder());
	}
	
	public static void backupDir(File dir) {
		File newDir = new File(dir.getParentFile(), dir.getName() + FOLDER_SUFFIX);
		int i = 1;
		while (newDir.exists()) {
			newDir = new File(dir.getParentFile(), dir.getName() + FOLDER_SUFFIX + i);
		}
		try {
			FileUtils.copyDirectory(dir, newDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

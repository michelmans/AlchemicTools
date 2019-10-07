package me.alchemi.alchemictools.objects.uuidconverting;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import me.alchemi.al.database.sqlite.SQLiteDatabase;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.events.OnlineUUIDApplyEvent;

public class SQLiteConverter implements ISQLConverter {

	@Override
	public void onDatabase(OnlineUUIDApplyEvent e) {
		
		List<File> files = getFiles(new File(Bukkit.getWorldContainer(), "plugins"));
		
		for (File f : files) {
			try {
				SQLiteDatabase db = SQLiteDatabase.newConnection(Tools.getInstance(), f);
				db.getFile();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	private List<File> getFiles(File path){
		List<File> files = new ArrayList<File>();
		for (File file : path.listFiles()) {
			if (file.isDirectory()) {
				files.addAll(getFiles(file));
			} else {
				if (file.getName().endsWith(".db")) files.add(file);
			}
		}
		return files;
	}

}

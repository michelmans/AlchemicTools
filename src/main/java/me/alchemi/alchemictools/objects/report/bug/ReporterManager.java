package me.alchemi.alchemictools.objects.report.bug;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import me.alchemi.al.objects.AlchemicEntry;

public class ReporterManager {

	public Map<String, Reporter> active = new HashMap<String, Reporter>();
	private Map<String, Reporter> inactive = new HashMap<String, Reporter>();
	private static ReporterManager instance;
	
	public ReporterManager() {
		instance = this;
	}
	
	public final void addReporter(Reporter report){
	
		if (report.isActive()) active.put(report.getId(), report);
		else inactive.put(report.getId(), report);
		
	}
	
	public final Map.Entry<Boolean, String> activate(String reporter) {
		
		if (inactive.containsKey(reporter)) {
			
			Entry<Boolean, String> result = inactive.get(reporter).activate();
			if (result.getKey()) {
				active.put(reporter, inactive.remove(reporter));
				return new AlchemicEntry<Boolean, String>(true);
			} else {
				return new AlchemicEntry<Boolean, String>(false, result.getValue());
			}
			
		} else if (active.containsKey(reporter)) {
			return new AlchemicEntry<Boolean, String>(false, "&9Reporter is already active.");
		} else {
			return new AlchemicEntry<Boolean, String>(false, "&4Reporter doesn't exist.");
		}
		
	}
	
	public final void deactivate(String reporter) {
		
		if (active.containsKey(reporter)) {
			active.get(reporter).deactivate();
			inactive.put(reporter, active.remove(reporter));
		}
		
	}
	
	public final void deactivateAll() {
		
		for (Reporter reporter : active.values()) {
			reporter.deactivate();
		}
		
	}

	public Reporter get(String reporter) {
		if (active.containsKey(reporter)) {
			return active.get(reporter);
		} else if (inactive.containsKey(reporter)) {
			return inactive.get(reporter);
		}
		return null;
	}
	
	public void report(String title, String description, CommandSender reporter) {
		active.values().forEach(report ->{
			report.report(title, description, reporter);
		});
	}
	
	public void report(String title, CommandSender reporter) {
		active.values().forEach(report ->{
			report.report(title, reporter);
		});
	}
	
	/**
	 * @return the instance
	 */
	public static ReporterManager getInstance() {
		return instance;
	}
	
}

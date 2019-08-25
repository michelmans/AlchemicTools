package me.alchemi.alchemictools.objects.uuidconverting;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.event.EventHandler;

import com.github.intellectualsites.plotsquared.api.PlotAPI;
import com.github.intellectualsites.plotsquared.plot.object.Plot;

import me.alchemi.alchemictools.objects.events.OnlineUUIDApplyEvent;

public class PlotSquaredConverter implements IConverter {

	@Override
	@EventHandler
	public void onNewUUIDApply(OnlineUUIDApplyEvent e) {
		PlotAPI api = new PlotAPI();
		for (Plot plot : api.getAllPlots()) {
			
			//owner
			for (UUID id : convertSet(plot.getOwners(), e)) {
				plot.setOwner(id);
			}
			
			//denied players
			plot.setDenied(convertSet(plot.getDenied(), e));
			
			//members
			plot.setMembers(convertSet(plot.getMembers(), e));
			
			//trusted
			plot.setTrusted(convertSet(plot.getTrusted(), e));
		}
	}
	
	private Set<UUID> convertSet(Set<UUID> ids, OnlineUUIDApplyEvent e) {
		Set<UUID> returnSet = new HashSet<UUID>();
		for (UUID uuid : ids) {
			if (e.getOldUUIDs().contains(uuid)) {
				returnSet.add(e.getNewUUID(uuid));
			} else {
				returnSet.add(uuid);
			}
		}
		return returnSet;
	}
 
}

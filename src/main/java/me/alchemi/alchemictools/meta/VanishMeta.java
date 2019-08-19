package me.alchemi.alchemictools.meta;


import me.alchemi.al.objects.base.PluginBase;
import me.alchemi.al.objects.meta.BaseMeta;

public class VanishMeta extends BaseMeta {

	private boolean vanish;
	
	public static final String NAME = "vanish";
	
	public VanishMeta(PluginBase owningPlugin, boolean vanish) {
		super(owningPlugin, vanish);
		this.vanish = vanish;
	}

	@Override
	public Object value() {

		return vanish;
	}
	
	@Override
	public void invalidate() {}
	
}

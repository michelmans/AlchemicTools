package me.alchemi.alchemictools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.SexyConfiguration;
import me.alchemi.al.objects.base.ConfigBase;
import me.alchemi.alchemictools.listener.commandstuff.AdvancedCommand;
import me.alchemi.alchemictools.listener.commandstuff.AdvancedTabComplete;

public class Config extends ConfigBase {
	public Config() throws FileNotFoundException, IOException, InvalidConfigurationException {
		super(Tools.getInstance());		
	}
	
	public static enum ConfigEnum implements IConfigEnum{
		CONFIG(new File(Tools.getInstance().getDataFolder(), "config.yml"), 6),
		MESSAGES(new File(Tools.getInstance().getDataFolder(), "messages.yml"), 7),
		COMMANDS(new File(Tools.getInstance().getDataFolder(), "commands.yml"), 2);

		final File file;
		final int version;
		SexyConfiguration config;
		
		private ConfigEnum(File file, int version) {
			this.file = file;
			this.version = version;
			this.config = SexyConfiguration.loadConfiguration(file);
		}
		
		@Override
		public SexyConfiguration getConfig() {
			return config;
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public int getVersion() {
			return version;
		}
	}
	
	public static SexyConfiguration config;
	
	public static enum Options implements IConfig {
		MESSAGE_MENTIONTAG("AlchemicTools.Message.mentionTag"),
		MESSAGE_MENTIONCOLOUR("AlchemicTools.Message.mentionColour"),
		MESSAGE_RECEIVESOUND("AlchemicTools.Message.receiveSound"),
		MESSAGE_MENTIONSOUND("AlchemicTools.Message.mentionSound"),
		RESTART_DEFAULTDELAY("AlchemicTools.Restart.defaultDelay"),
		RESTART_WARNINGS("AlchemicTools.Restart.warnings"),
		RESTART_DEFAULTMESSAGE("AlchemicTools.Restart.defaultMessage");
		
		private Object value;
		public final String key;
		
		Options(String key){
			this.key = key;
			get();
		}
				
		@Override
		public void get() {
			value = ConfigEnum.CONFIG.getConfig().get(key);
		}
		
		@Override
		public Object value() {
			return value;
		}
		
		@Override
		public boolean asBoolean() {
			return Boolean.parseBoolean(asString());
		}
		
		@Override
		public String asString() {
			return String.valueOf(value);
		}
		
		@Override
		public Sound asSound() {
			
			return Sound.valueOf(asString());
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public List<String> asStringList() {
			try {
				return (List<String>) value;
			} catch (ClassCastException e) { return null; }
		}
		
		@Override
		public int asInt() {
			return Integer.valueOf(asString());
		}
		
		public double asDouble() {
			return Double.valueOf(asString());
		}
		
		@Override
		public ItemStack asItemStack() {
			try {
				return (ItemStack) value;
			} catch (ClassCastException e) { return null; }
		}
		
		@Override
		public Material asMaterial() {
			return MaterialWrapper.getWrapper(asString());
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public List<Integer> asIntList(){
			try {
				return (List<Integer>) value;
			} catch (ClassCastException e) { return null; }
		}

		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.CONFIG.getConfig();
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Float> asFloatList() {
			try {
				return (List<Float>) value;
			} catch (ClassCastException e) { return null; }
		}
		
	}
	
	public static enum Vanish implements IConfig {
		
		ENABLE_POTION_EFFECT("AlchemicTools.Vanish.enablePotionEffect"),
		NOTIFY_ADMINS("AlchemicTools.Vanish.notifyAdmins"),
		ACTION_BAR("AlchemicTools.Vanish.actionBar"),
		INDICATION("AlchemicTools.Vanish.indication"),
		TAB_PREFIX("AlchemicTools.Vanish.tabPrefix"),
		RIGHT_CLICK_INVENTORY("AlchemicTools.Vanish.rightClickInventory"),
		LEFT_CLICK_EFFECTS("AlchemicTools.Vanish.leftClickEffects"),
		CANCEL_COMMANDS("AlchemicTools.Vanish.cancelCommands");
		
		private Object value;
		public final String key;
		
		Vanish(String key){
			this.key = key;
			get();
		}
				
		@Override
		public void get() {
			value = ConfigEnum.CONFIG.getConfig().get(key);
		}
		
		@Override
		public Object value() {
			return value;
		}
		
		@Override
		public boolean asBoolean() {
			return Boolean.parseBoolean(asString());
		}
		
		@Override
		public String asString() {
			return String.valueOf(value);
		}
		
		@Override
		public Sound asSound() {
			
			return Sound.valueOf(asString());
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public List<String> asStringList() {
			try {
				return (List<String>) value;
			} catch (ClassCastException e) { return null; }
		}
		
		@Override
		public int asInt() {
			return Integer.valueOf(asString());
		}
		
		public double asDouble() {
			return Double.valueOf(asString());
		}
		
		@Override
		public ItemStack asItemStack() {
			try {
				return (ItemStack) value;
			} catch (ClassCastException e) { return null; }
		}
		
		@Override
		public Material asMaterial() {
			return MaterialWrapper.getWrapper(asString());
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public List<Integer> asIntList(){
			try {
				return (List<Integer>) value;
			} catch (ClassCastException e) { return null; }
		}

		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.CONFIG.getConfig();
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Float> asFloatList() {
			try {
				return (List<Float>) value;
			} catch (ClassCastException e) { return null; }
		}
	}
	
	public static enum Hooks implements IConfig {
		
		MVDWPLACEHOLDERAPI("AlchemicTools.Hooks.mvdwplaceholderapi"),
		CHATCONTROL("AlchemicTools.Hooks.chatcontrol"),
		BUNGEE("AlchemicTools.Hooks.bungee");
		
		private Object value;
		public final String key;
		
		Hooks(String key){
			this.key = key;
			get();
		}
				
		@Override
		public void get() {
			value = ConfigEnum.CONFIG.getConfig().get(key);
		}
		
		@Override
		public Object value() {
			return value;
		}
		
		@Override
		public boolean asBoolean() {
			return Boolean.parseBoolean(asString());
		}
		
		@Override
		public String asString() {
			return String.valueOf(value);
		}
		
		@Override
		public Sound asSound() {
			
			return Sound.valueOf(asString());
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public List<String> asStringList() {
			try {
				return (List<String>) value;
			} catch (ClassCastException e) { return null; }
		}
		
		@Override
		public int asInt() {
			return Integer.valueOf(asString());
		}
		
		public double asDouble() {
			return Double.valueOf(asString());
		}
		
		@Override
		public ItemStack asItemStack() {
			try {
				return (ItemStack) value;
			} catch (ClassCastException e) { return null; }
		}
		
		@Override
		public Material asMaterial() {
			return MaterialWrapper.getWrapper(asString());
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public List<Integer> asIntList(){
			try {
				return (List<Integer>) value;
			} catch (ClassCastException e) { return null; }
		}

		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.CONFIG.getConfig();
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Float> asFloatList() {
			try {
				return (List<Float>) value;
			} catch (ClassCastException e) { return null; }
		}
	}
	
	public static enum Messages implements IMessage{
		COMMANDS_NOPERMISSION("AlchemicTools.Commands.NoPermission"),
		COMMANDS_RELOAD("AlchemicTools.Commands.Reload"),
		COMMANDS_WRONGFORMAT("AlchemicTools.Commands.WrongFormat"),
		COMMANDS_UNKNOWN("AlchemicTools.Commands.Unknown"),
		VANISH_START("AlchemicTools.Vanish.Start"),
		VANISH_STOP("AlchemicTools.Vanish.Stop"),
		VANISH_NOTIFYSTART("AlchemicTools.Vanish.NotifyStart"),
		VANISH_NOTIFYSTOP("AlchemicTools.Vanish.NotifyStop"),
		VANISH_PERSISTENTNOTIFICATION("AlchemicTools.Vanish.PersistentNotification"),
		VANISH_CANCELCOMMANDMESSAGE("AlchemicTools.Vanish.CancelCommandMessage"),
		INVSEE_PLAYEROFFLINE("AlchemicTools.Invsee.PlayerOffline"),
		INVSEE_NOPOTIONS("AlchemicTools.Invsee.NoPotions"),
		STAFFCHAT_NONSTAFF("AlchemicTools.StaffChat.NonStaff"),
		STAFFCHAT_STAFF("AlchemicTools.StaffChat.Staff"),
		STAFFCHAT_START("AlchemicTools.StaffChat.Start"),
		STAFFCHAT_STOP("AlchemicTools.StaffChat.Stop"),
		SMITE_PLAYEROFFLINE("AlchemicTools.Smite.PlayerOFfline"),
		SMITE_PROVIDE("AlchemicTools.Smite.Provide"),
		SMITE_SMITTEN("AlchemicTools.Smite.Smitten"),
		SMITE_SENT("AlchemicTools.Smite.Sent"),
		RESTART_WARNING("AlchemicTools.Restart.Warning"),
		RESTART_NOW("AlchemicTools.Restart.Now"),
		RESTART_STOPPED("AlchemicTools.Restart.Stopped"),
		RESTART_REASONING("AlchemicTools.Restart.Reasoning"),
		SUDO_PLAYEROFFLINE("AlchemicTools.Sudo.PlayerOffline"),
		SUDO_RUN("AlchemicTools.Sudo.Run");
		
		String value;
		String key;

		private Messages(String key) {
			this.key = key;
		}

		public void get() {
			value = getConfig().getString(key, "PLACEHOLDER - STRING NOT FOUND");

		}
  
		public String value() { return value; }

		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.MESSAGES.getConfig();
		} 
	
	}
	 
	@Override
	protected IConfigEnum[] getConfigs() {
		return ConfigEnum.values();
	}

	@Override
	protected Set<IConfig> getEnums() {
		return new HashSet<ConfigBase.IConfig>() {
			{
				addAll(Arrays.asList(Options.values()));
				addAll(Arrays.asList(Hooks.values()));
				addAll(Arrays.asList(Vanish.values()));
			}
		};
	}

	@Override
	protected Set<IMessage> getMessages() {
		return new HashSet<ConfigBase.IMessage>() {
			{
				addAll(Arrays.asList(Messages.values()));
			}
		};
	}
	
	public void registerCommands() {
		for (Entry<String, Object> entry : ConfigEnum.COMMANDS.getConfig().getConfigurationSection("commands").getValues(false).entrySet()) {
			
			PluginCommand command = Bukkit.getPluginCommand(entry.getKey());
			JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin((String) entry.getValue());
			
			if (plugin != null && command != null) {
				PluginCommand newCommand = plugin.getCommand(entry.getKey());
				
				if (newCommand != null) {
					command.setTabCompleter(new AdvancedTabComplete(command.getTabCompleter(), newCommand.getTabCompleter(), plugin.getDescription().getName()));
					command.setExecutor(new AdvancedCommand(command.getExecutor(), newCommand.getExecutor(), plugin.getDescription().getName()));
				}
				
			}
			
		}
	}
}

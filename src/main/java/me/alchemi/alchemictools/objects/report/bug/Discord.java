package me.alchemi.alchemictools.objects.report.bug;

import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import me.alchemi.al.objects.AlchemicEntry;
import me.alchemi.alchemictools.Tools;

public class Discord implements Reporter{

	private DiscordClient api;
	private Guild server;
	private TextChannel channel;
	
	private boolean active;
	
	private static final String oAuth = "https://discordapp.com/api/oauth2/authorize?client_id=642467140133322761&permissions=19456&scope=bot";
	
	public Discord() {
//		Entry<Boolean, String> result = activate();
//		active = result.getKey();
//		if (!active) {
//			Tools.getInstance().getMessenger().print(result.getValue());
//		}
		active = false;
	}
	
	@Override
	public Entry<Boolean, String> activate() {
		
		api = new DiscordClientBuilder("NjQyNDY3MTQwMTMzMzIyNzYx.XcXYyQ.fgE42EAQDQhYnbeZqLEXL7Va_Sw").build();
		api.login().subscribe();
		server = api.getGuildById(Snowflake.of("237253012756758529")).block();
		channel = server.getChannelById(Snowflake.of("386555075822223370")).cast(TextChannel.class).block();
		return new AlchemicEntry<Boolean, String>(false, "Yo momma");
	}

	@Override
	public void deactivate() {
		
		api.logout().block();
		api = null;
		server = null;
		channel = null;
		active = false;
		
	}

	@Override
	public String getId() {
		return "discord";
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void report(String title, String description, CommandSender reporter) {
		channel.createMessage("```------Bug------\n==" + title + "==\n" + description + "\nReported by: " + reporter.getName() + "```").block();
	}

	@Override
	public void report(String title, CommandSender reporter) {
		channel.createMessage("```------Bug-----\n==" + title + "==\nReported by: " + reporter.getName() + "```").block();
	}

	
}

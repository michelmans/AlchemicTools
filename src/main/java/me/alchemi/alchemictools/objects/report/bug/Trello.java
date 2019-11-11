package me.alchemi.alchemictools.objects.report.bug;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.Label;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.AsyncTrelloHttpClient2;

import me.alchemi.al.objects.AlchemicEntry;
import me.alchemi.alchemictools.Config.Bugs;
import me.alchemi.alchemictools.Config.Options;
import me.alchemi.alchemictools.Tools;
import net.md_5.bungee.api.ChatColor;

public class Trello implements Reporter {

	public static final String[] labelColours = new String[] {"yellow", "purple", "blue", "red", "green", "orange", "black", "sky", "pink", "lime"}; 
	private String key;
	private String token;
	private com.julienvey.trello.Trello api;
	private Board board;
	private Label label;
	private TList list;
	
	private boolean active;
	
	public Trello() {
		Entry<Boolean, String> result = activate();
		active = result.getKey();
		if (!active) {
			Tools.getInstance().getMessenger().print(result.getValue());
		}
	}
	
	@Override
	public Map.Entry<Boolean, String> activate() {
		key = Bugs.TRELLO_KEY.asString();
		token = Bugs.TRELLO_TOKEN.asString();
		
		if (key.isEmpty()) {
			return new AlchemicEntry<Boolean, String>(false, "&4Please put your API key in the config.yml .\nYou can get it here: https://trello.com/app-key/");
			
		} else if (token.isEmpty()) {
			generateToken(Bukkit.getConsoleSender());
			return new AlchemicEntry<Boolean, String>(false, "&4You need to generate an access token.");
			
		}
		api = new TrelloImpl(key, token, new AsyncTrelloHttpClient2());
		
		board = api.getBoard(getBoardID(Bugs.TRELLO_BOARDNAME.asString()));
		
		if (!hasLabel(api)) {
			label = new Label()
					.setColor(labelColours[new Random().nextInt(labelColours.length - 1)])
					.setName(Options.SERVERNAME.asString())
					.setIdBoard(board.getId());
			label = api.createLabel(label);
		}
		
		for (TList list : board.fetchLists()) {
			
			if (Bugs.TRELLO_LISTNAME.asString().isEmpty() || list.getName().equals(Bugs.TRELLO_LISTNAME.asString())) {
				this.list = list;
				break;
			}
		}
		
		Tools.getInstance().notify("&9Trello bug reporting service is &nactive&9.");
		active = true;
		return new AlchemicEntry<Boolean, String>(true);
	}
	
	public void generateToken(CommandSender sender) {
		String url = "https://trello.com/1/authorize?expiration=never&name=AlchemicTools&scope=read,write&response_type=token&key=" + key;
		sender.sendMessage("Go to the following link:\n" + url + "\nClick \"Allow\", copy the token and paste it in with /tools bugs trello token <token>");
	}
	
	private String getBoardID(String name) {
		try {
			String jsonString = IOUtils.toString(new URL("https://api.trello.com/1/members/me/boards?fields=name,id&key={apiKey}&token={apiToken}"
					.replace("{apiKey}", key)
					.replace("{apiToken}", token)), Charset.defaultCharset());
			
			for (Object o : (JSONArray) JSONValue.parseWithException(jsonString)) {
				JSONObject ob = (JSONObject)o;
				if (name.isEmpty() || ob.containsValue(name)) {
					return (String) ob.get("id");
				}
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private boolean hasLabel(com.julienvey.trello.Trello api) {
		for (Label l : api.getBoardLabels(board.getId())) {
			if (l.getName().equals(Options.SERVERNAME.asString())) {
				label = l;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void report(String title, String description, CommandSender reporter) {
		Card c = new Card();
		c.setName(ChatColor.stripColor(title));
		c.setDesc(ChatColor.stripColor(description));
		
		c = list.createCard(c);
		api.addLabelToCard(c.getId(), label.getId());
		api.addCommentToCard(c.getId(), "Reported by: " + reporter.getName());
	}

	@Override
	public void report(String title, CommandSender reporter) {
		Card c = new Card();
		c.setName(ChatColor.stripColor(title));
		
		c = list.createCard(c);
		api.addLabelToCard(c.getId(), label.getId());
		api.addCommentToCard(c.getId(), "Reported by: " + reporter.getName());
	}

	@Override
	public boolean isActive() {
		return active;
	}
	
	@Override
	public String getId() {
		return "trello";
	}

	@Override
	public void deactivate() {

		key = null;
		token = null;
		api = null;
		board = null;
		label = null;
		list = null;
		active = false;
		
	}

}

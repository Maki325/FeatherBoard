package maki325.featherboard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import maki325.featherboard.commands.CommandBoard;
import maki325.featherboard.commands.tabcompleters.TabCompleterBoard;
import maki325.featherboard.utils.DB;
import net.md_5.bungee.api.ChatColor;

public class Featherboard extends JavaPlugin implements Listener {
	
	private String host = null;
	private int port = -1;
	private String username = null;
	private String database = null;
	private String password = null;
	private Connection connection = null;
	
	public static Featherboard instance;
	public HashMap<UUID, Boolean> isActivated = new HashMap<UUID, Boolean>();
	
	@Override
	public void onEnable() {
		instance = this;

		saveDefaultConfig();
		
		getCommand("board").setExecutor(new CommandBoard());
		getCommand("board").setPermission("featherboard");
		getCommand("board").setTabCompleter(new TabCompleterBoard());

		setupDB();
		
		getServer().getPluginManager().registerEvents(this, this);
		
		load();
		
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			
			@Override
			public void run() {
				save();
			}
			
		}, 36000, 0);
		
	}
	
	public void load() {
		isActivated.clear();
		ResultSet set = DB.query("SELECT * FROM players");
		while(DB.next(set)) {
			isActivated.put(UUID.fromString((String) DB.value(set, "uuid")),((Integer) DB.value(set, "featherboard")==1) ? true : false);
		}
	}
	
	public void save() {
		for(UUID id:isActivated.keySet()) {
			DB.update("UPDATE players SET featherboard=" + isActivated.get(id) + " WHERE uuid='" + id.toString() + "'");
		}
	}
	
	@Override
	public void onDisable() {
		saveConfig();
		save();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
		if(!isActivated.containsKey(event.getPlayer().getUniqueId())) {
			isActivated.put(event.getPlayer().getUniqueId(), true);
		}
		if(getConfig().getBoolean("active", true)) {
			if(isActivated.get(event.getPlayer().getUniqueId())) {
				setScoreboad(event.getPlayer());
			}
		}
	}
	
	public void setScoreboad(Player p) {
		ResultSet set = DB.query("SELECT * FROM `playerData` WHERE uuid='" + p.getUniqueId().toString() + "'");
		if(!DB.next(set)) {
			return;
		}
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = board.registerNewObjective("featherboard", "dummy");
		String name = (getConfig().getString("name") == null) ? "Stats" : getConfig().getString("name");
		obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', code(name, p)));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		int i = getConfig().getStringList("board").size();
		for(String s:getConfig().getStringList("board")) {
			s = s.replaceAll("\\{p\\}", p.getName())
					.replaceAll("\\{k\\}", Integer.toString((Integer) DB.value(set, "kills")))
					.replaceAll("\\{d\\}", Integer.toString((Integer) DB.value(set, "deaths")))
					.replaceAll("\\{j\\}", Integer.toString((Integer) DB.value(set, "joins")))
					.replaceAll("\\{bb\\}", Integer.toString((Integer) DB.value(set, "blocks_broken")))
					.replaceAll("\\{bp\\}", Integer.toString((Integer) DB.value(set, "blocks_placed")))
					.replaceAll("\\{ce\\}", Integer.toString((Integer) DB.value(set, "commands_executed")))
					.replaceAll("\\{ph\\}", Integer.toString((Integer) DB.value(set, "projectiles_hit")))
					.replaceAll("\\{ps\\}", Integer.toString((Integer) DB.value(set, "projectiles_shot")))
					.replaceAll("\\{s\\}", Bukkit.getServer().getServerName());
			obj.getScore(ChatColor.translateAlternateColorCodes('&', s)).setScore(i--);
		}
		p.setScoreboard(board);
	}
	
	public String code(String input, Player p) {
		return input.replaceAll("\\{p\\}", p.getName())
					.replaceAll("\\{s\\}", Bukkit.getServer().getServerName());
	}
	
	public void setupDB() {
		
		synchronized (this){
			try {
				
				synchronized(this) {
					if(getConnection()!=null && !getConnection().isClosed()) {
						return;
					}
					Class.forName("com.mysql.jdbc.Driver");
					setConnection(DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database,username,password));
				}
				
			} catch (SQLException e) {
			} catch (ClassNotFoundException e) {
			}
		}
		
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
}

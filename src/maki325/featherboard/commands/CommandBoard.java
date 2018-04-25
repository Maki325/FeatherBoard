package maki325.featherboard.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import maki325.featherboard.Featherboard;

public class CommandBoard implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length < 1) {
			sendErrorMessage(sender);
		}
		
		if(!sender.hasPermission("featherboard")) {
			sender.sendMessage(ChatColor.RED + "You dont have permission to use this command");
		}
		
		Featherboard instance = Featherboard.instance;
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(new String[]{ "Help:", 
												"/board help [command] - prints every command and its purpose or if specified then prints only that one", 
												"/board reload - reloads the config file and scoreboard - it will lag a lot", 
												"/board save - saves the config to the files", 
												"/board activate/deactivate [player] - activates/deactivates the scoreboard on the side.If there is a [player] in the command it does it only for them", 
												"/board remove [line] - removes line at [line] position.Counting starts at 1", 
												"/board name [name] - changes the name of the scoreboard", 
												"/board add/edit [line] [text] - adds,removes or edits the line at the [line] position on the board to value of [text]. Counting starts at 1.If [line] is -1 it will add a line at the end", 
												"/board line [pos1] [pos2] - sets the position of the like at position [pos1] to position [pos2].Counting starts at 1",
												"/board tags - displayes all of the custom variables"
												});
			} else if(args[0].equalsIgnoreCase("activate")) {
				for(Player p:Bukkit.getOnlinePlayers()) {
					if(instance.isActivated.get(p.getUniqueId())) {
						instance.setScoreboad(p);
					}
				}
				instance.getConfig().set("active", true);
				instance.saveConfig();
				sender.sendMessage(ChatColor.GREEN + "Featherboard activated");
			} else if(args[0].equalsIgnoreCase("deactivate")) {
				for(Player p:Bukkit.getOnlinePlayers()) {
					if(instance.isActivated.get(p.getUniqueId())) {
						p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					}
				}
				instance.getConfig().set("active", false);
				instance.saveConfig();
				sender.sendMessage(ChatColor.GREEN + "Featherboard deactivated");
 			} else if(args[0].equalsIgnoreCase("save")) {
				instance.save();
				instance.saveConfig();
				sender.sendMessage(ChatColor.GREEN + "Everything saved");
			}  else if(args[0].equalsIgnoreCase("reload")) {
				instance.save();
				instance.load();
				instance.reloadConfig();
				for(Player p:Bukkit.getOnlinePlayers()) {
					if(instance.isActivated.get(p.getUniqueId())) {
						instance.setScoreboad(p);
					}
				}
				sender.sendMessage(ChatColor.GREEN + "Reloaded!!");
			}  else if(args[0].equalsIgnoreCase("tags")) {
				sender.sendMessage(new String[] {
						"Custom variables are:",
						"{p} - player name",
						"{k} - player kills",
						"{d} - player deaths",
						"{j} - how many times the player joined",
						"{bb} - blocks broken",
						"{bp} - blocks placed",
						"{ce} - commands executed",
						"{ph} - projectils hit",
						"{ps} - projectils shoot",
						"{s} - server name"
				});
			} else {
				sender.sendMessage(ChatColor.RED + "Command you try to use does not exist");
			}
		} else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("help")) {
				if(args[1].equalsIgnoreCase("help")) {
					sender.sendMessage("/board help [command] - prints every command and its purpose or if specified then prints only that one");
				} else if(args[1].equalsIgnoreCase("activate") || args[1].equalsIgnoreCase("deactivate")) {
					sender.sendMessage("/board activate/deactivate [player] - activates/deactivates the scoreboard on the side.If there is a [player] in the command it does it only for them");
				} else if(args[1].equalsIgnoreCase("remove")) {
					sender.sendMessage("/board remove [line] - removes line at [line] position.Counting starts at 1");
				} else if(args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("edit")) {
					sender.sendMessage("/board add/edit [line] [text] - adds,removes or edits the line at the [line] position on the board to value of [text]. Counting starts at 1.If [line] is -1 it will add a line at the end");
				} else if(args[1].equalsIgnoreCase("line")) {
					sender.sendMessage("/board line [pos1] [pos2] - sets the position of the like at position [pos1] to position [pos2].Counting starts at 1");
				} else if(args[1].equalsIgnoreCase("save")) {
					sender.sendMessage("/board save - saves the config to the file");
				} else if(args[1].equalsIgnoreCase("reload")) {
					sender.sendMessage("/board reload - reloads the config file and scoreboard - it will lag a lot");
				} else if(args[1].equalsIgnoreCase("tags")) {
					sender.sendMessage("/board tags - displayes all of the custom variables");
				}  else if(args[1].equalsIgnoreCase("name")) {
					sender.sendMessage("/board name [name] - changes the name of the scoreboard");
				} else {
					sender.sendMessage(ChatColor.RED + "Command you try to use does not exist");
				}
			} else if(args[0].equalsIgnoreCase("remove")) {
				int line = Integer.parseInt(args[1]);
				List<String> bits = instance.getConfig().getStringList("board");
				if(line < 1 || line > bits.size()) {
					sender.sendMessage(ChatColor.RED + "You cant put numbers under 1 or over " + bits.size() + " currently");
				} else {
					bits.remove(line-1);
					instance.getConfig().set("board", bits);
					instance.saveConfig();
					for(Player p:Bukkit.getOnlinePlayers()) {
						if(instance.isActivated.get(p.getUniqueId())) {
							instance.setScoreboad(p);
						}
					}
					sender.sendMessage(ChatColor.GREEN + "Line removed");
				}
			} else if(args[0].equalsIgnoreCase("deactivate")) {
				Player p;
				if((p = Bukkit.getPlayer(args[1])) != null) {
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					instance.isActivated.put(p.getUniqueId(), false);
					sender.sendMessage(ChatColor.GREEN + "Featherboard deactivated for player " + p.getName());
				} else {
					sender.sendMessage(ChatColor.RED + "The player you requested doesnt exist.");
				}
			} else if(args[0].equalsIgnoreCase("activate")) {
				Player p;
				if((p = Bukkit.getPlayer(args[1])) != null) {
					instance.setScoreboad(p);
					instance.isActivated.put(p.getUniqueId(), true);
					sender.sendMessage(ChatColor.GREEN + "Featherboard activated for player " + p.getName());
				} else {
					sender.sendMessage(ChatColor.RED + "The player you requested doesnt exist.");
				}
			} else if(args[0].equalsIgnoreCase("name")) {
				instance.getConfig().set("name", args[1]);
				instance.save();
				for(Player p:Bukkit.getOnlinePlayers()) {
					instance.setScoreboad(p);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Command you try to use does not exist");
			}
		} else if(args.length >= 2 && args[0].equalsIgnoreCase("name")) {
			String add = "";
			for(int i = 1;i < args.length;i++) {
				add += " " + args[i];
			}
			instance.getConfig().set("name", add.substring(1));
			instance.save();
			for(Player p:Bukkit.getOnlinePlayers()) {
				instance.setScoreboad(p);
			}
		} else if(args.length >= 3) {
			if(args[0].equalsIgnoreCase("add")) {
				int line = Integer.parseInt(args[1]);
				List<String> bits = instance.getConfig().getStringList("board");
				if(line < 1 || line > bits.size()) {
					String add = "";
					for(int i = 2;i < args.length;i++) {
						add += " " + args[i];
					}
					bits.add(add.substring(1));
					instance.getConfig().set("board", bits);
					instance.saveConfig();
					for(Player p:Bukkit.getOnlinePlayers()) {
						if(instance.isActivated.get(p.getUniqueId())) {
							instance.setScoreboad(p);
						}
					}
					sender.sendMessage(ChatColor.GREEN + "Line added");
				} else {
					String add = "";
					for(int i = 2;i < args.length;i++) {
						add += " " + args[i];
					}
					bits.add(line-1, add.substring(1));
					instance.getConfig().set("board", bits);
					instance.saveConfig();
					for(Player p:Bukkit.getOnlinePlayers()) {
						if(instance.isActivated.get(p.getUniqueId())) {
							instance.setScoreboad(p);
						}
					}
					sender.sendMessage(ChatColor.GREEN + "Line added");
				}
			} else if(args[0].equalsIgnoreCase("edit")) {
				int line = Integer.parseInt(args[1]);
				List<String> bits = instance.getConfig().getStringList("board");
				if(line < 1 || line > bits.size()) {
					sender.sendMessage(ChatColor.RED + "You cant put numbers under 1 or over " + bits.size() + " currently");
				} else {
					String add = "";
					for(int i = 2;i < args.length;i++) {
						add += " " + args[i];
					}
					if(line > bits.size()) {
						line = bits.size();
					}
					bits.set(line-1, add.substring(1));
					instance.getConfig().set("board", bits);
					instance.saveConfig();
					for(Player p:Bukkit.getOnlinePlayers()) {
						if(instance.isActivated.get(p.getUniqueId())) {
							instance.setScoreboad(p);
						}
					}
					sender.sendMessage(ChatColor.GREEN + "Line edited");
				}
			} else if(args[0].equalsIgnoreCase("line")) {
				int line1 = Integer.parseInt(args[1]);
				int line2 = Integer.parseInt(args[1]);
				List<String> bits = instance.getConfig().getStringList("board");
				if(line1 < 1 || line1 > bits.size() || line2 < 1 || line2 > bits.size()) {
					sender.sendMessage(ChatColor.RED + "You cant put numbers under 1 or over " + bits.size() + " currently");
				} else {
					if(line1 == line2) {
						return true;
					}
					if(line1 > line2) {
						int t = line2;
						line2 = line1;
						line1 = t;
					}
					String s = bits.get(line1);
					bits.remove(bits.indexOf(s));
					bits.add(line2-1, s);
					
					instance.getConfig().set("board", bits);
					instance.saveConfig();
					for(Player p:Bukkit.getOnlinePlayers()) {
						if(instance.isActivated.get(p.getUniqueId())) {
							instance.setScoreboad(p);
						}
					}
					sender.sendMessage(ChatColor.GREEN + "Changed line position");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Command you try to use does not exist");
			}
		}
		
		return true;
	}

	private void sendErrorMessage(CommandSender sender) {
		sender.sendMessage("Usage: /board help");
	}
	
}

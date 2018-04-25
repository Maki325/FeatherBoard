package maki325.featherboard.commands.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabCompleterBoard implements TabCompleter {

	private String[] commands = { "help", "reload", "save", "activate", "deactivate", "name", "edit", "add", "remove", "line" };
	private String[] codes = { "{p}", "{k}", "{d}", "{j}", "{bb}", "{bp}", "{ce}", "{ph}", "{ps}", "{s}" };
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> ret = new ArrayList<String>();
		if(args.length == 1) {
			for(String s:commands) {
				if(s.startsWith(args[0].replaceAll(" ", ""))) {
					ret.add(s);
				}
			}
		} else if(args.length >= 2 && args[0].equalsIgnoreCase("name")) {
			for(String s:codes) {
				if(s.startsWith(args[args.length-1].replaceAll(" ", ""))) {
					ret.add(s);
				}
			}
		} else if(args.length >= 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("edit")) && !args[args.length-1].equalsIgnoreCase("")) {
			for(String s:codes) {
				if(s.startsWith(args[args.length-1].replaceAll(" ", ""))) {
					ret.add(s);
				}
			}
		} 
		
		return ret;
	}

}

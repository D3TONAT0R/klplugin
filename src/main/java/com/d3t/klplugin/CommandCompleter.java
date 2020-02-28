package com.d3t.klplugin;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.event.server.TabCompleteEvent;

public class CommandCompleter extends TabCompleteEvent {

	public CommandCompleter(CommandSender sender, String buffer, List<String> completions) {
		super(sender, buffer, completions);
		// TODO Auto-generated constructor stub
	}

}

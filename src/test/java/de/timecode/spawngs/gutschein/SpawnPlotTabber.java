package de.timecode.spawngs.gutschein;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class SpawnPlotTabber implements TabCompleter {
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    List<String> c = new ArrayList<>();
    if (sender.hasPermission(FileManager.getPermission("AutoCompleter")) && 
      args.length == 1) {
      c.add("add");
      c.add("remove");
      c.add("give");
      c.add("givevoucher");
      c.add("reload");
      c.add("rl");
      c.add("help");
    } 
    return c;
  }
}

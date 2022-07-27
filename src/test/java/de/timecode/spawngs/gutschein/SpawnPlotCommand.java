package de.timecode.spawngs.gutschein;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;

public class SpawnPlotCommand implements CommandExecutor {
  private SpawnGS pl;
  
  public SpawnPlotCommand(SpawnGS pl) {
    this.pl = pl;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player)sender;
      PlotAPI api = new PlotAPI();
      PlotPlayer pp = api.wrapPlayer(p.getUniqueId());
      Plot plot = pp.getCurrentPlot();
      if (args.length == 1) {
        if (args[0].equalsIgnoreCase("add")) {
          if (p.hasPermission(FileManager.getPermission("Add")) || p.hasPermission(FileManager.getPermission("Admin"))) {
            if (plot != null) {
              List<String> list = FileManager.plots.getStringList("Plots");
              if (list.contains(String.valueOf(plot.getId()))) {
                p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("PlotIsSpawnPlot").replace("&", "§"));
              } else {
                if (FileManager.cfg.getBoolean("SetOwnerOnAddPlot"))
                  plot.setOwner(p.getUniqueId()); 
                PlayerAddSpawnPlotEvent paspe = new PlayerAddSpawnPlotEvent(p, String.valueOf(plot.getId()), plot);
                Bukkit.getPluginManager().callEvent(paspe);
                if (!paspe.isCancelled()) {
                  list.add(String.valueOf(plot.getId()));
                  FileManager.plots.set("Plots", list);
                  FileManager.savePlots();
                  p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("PlotAdded").replace("&", "§").replace("{id}", String.valueOf(plot.getId())));
                } 
              } 
            } else {
              p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("NotOnPlot").replace("&", "§"));
            } 
          } else {
            p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("NoPerm").replace("&", "§"));
          } 
        } else if (args[0].equalsIgnoreCase("remove")) {
          if (p.hasPermission(FileManager.getPermission("Remove")) || p.hasPermission(FileManager.getPermission("Admin"))) {
            if (plot != null) {
              List<String> list = FileManager.plots.getStringList("Plots");
              if (list.contains(String.valueOf(plot.getId()))) {
                PlayerRemoveSpawnPlotEvent prspe = new PlayerRemoveSpawnPlotEvent(p, String.valueOf(plot.getId()), plot);
                Bukkit.getPluginManager().callEvent(prspe);
                if (!prspe.isCancelled()) {
                  list.remove(String.valueOf(plot.getId()));
                  FileManager.plots.set("Plots", list);
                  FileManager.savePlots();
                  p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("PlotRemoved").replace("&", "§").replace("{id}", String.valueOf(plot.getId())));
                } 
              } else {
                p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("PlotNotSpawnPlot").replace("&", "§"));
              } 
            } else {
              p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("NotOnPlot").replace("&", "§"));
            } 
          } else {
            p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("NoPerm").replace("&", "§"));
          } 
        } else if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
          if (p.hasPermission(FileManager.getPermission("Reload")) || p.hasPermission(FileManager.getPermission("Admin"))) {
            p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("Reload").replace("&", "§"));
            this.pl.reloadConfig();
            this.pl.saveDefaultConfig();
            FileManager.loadAll();
          } else {
            p.sendMessage(FileManager.getMessage("NoPerm").replace("&", "§"));
          } 
        } else if (args[0].equalsIgnoreCase("give")) {
          if (p.hasPermission(FileManager.getPermission("GivePlot")) || p.hasPermission(FileManager.getPermission("Admin"))) {
            p.sendMessage(String.valueOf(FileManager.getPrefix()) + "§e/spawnplot give <Player>");
          } else {
            p.sendMessage(FileManager.getMessage("NoPerm").replace("&", "§"));
          } 
        } else if (args[0].equalsIgnoreCase("givevoucher")) {
          if (p.hasPermission(FileManager.getPermission("GiveVoucher")) || p.hasPermission(FileManager.getPermission("Admin"))) {
            p.sendMessage(String.valueOf(FileManager.getPrefix()) + "§e/spawnplot givevoucher <Player>");
          } else {
            p.sendMessage(FileManager.getMessage("NoPerm").replace("&", "§"));
          } 
        } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("hilfe")) {
          if (p.hasPermission(FileManager.getPermission("Help"))) {
            List<String> help = FileManager.cfg.getStringList("Helpsite");
            for (String lines : help)
              p.sendMessage(lines.replace("&", "§")); 
          } else {
            p.sendMessage("§bSpawnPlot Voucher v" + this.pl.getDescription().getVersion() + " §aPlugin by TimeCode");
          } 
        } else {
          p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("SyntaxError").replace("&", "§"));
        } 
      } else if (args.length != 0 && args.length != 1 && args.length <= 2) {
        if (args[0].equalsIgnoreCase("givevoucher")) {
          if (p.hasPermission(FileManager.getPermission("GiveVoucher")) || p.hasPermission(FileManager.getPermission("Admin"))) {
            Player t = Bukkit.getPlayer(args[1]);
            PlayerGiveVoucherEvent pgve = new PlayerGiveVoucherEvent(p, t);
            Bukkit.getPluginManager().callEvent(pgve);
            if (t != null) {
              SpawnGS.giveVoucher(t);
              if (t.getName() == p.getName()) {
                t.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("GiveSpawnPlotVoucher").replace("&", "§").replace("{player}", p.getName()));
              } else {
                p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("GetSpawnPlotVoucher").replace("&", "§").replace("{player}", t.getName()));
                t.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("GiveSpawnPlotVoucher").replace("&", "§").replace("{player}", p.getName()));
              } 
            } else {
              p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("PlayerNotOnline").replace("&", "§"));
            } 
          } else {
            p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("NoPerm").replace("&", "§"));
          } 
        } else if (args[0].equalsIgnoreCase("give")) {
          if (p.hasPermission(FileManager.getPermission("GivePlot")) || p.hasPermission(FileManager.getPermission("Admin"))) {
            if (plot != null) {
              List<String> list = FileManager.plots.getStringList("Plots");
              if (list.contains(String.valueOf(plot.getId()))) {
                PlayerGiveSpawnPlotEvent pgspe = new PlayerGiveSpawnPlotEvent(p, Bukkit.getPlayer(args[1]), String.valueOf(plot.getId()), plot);
                Bukkit.getPluginManager().callEvent(pgspe);
                if (!pgspe.isCancelled()) {
                  Player t = Bukkit.getPlayer(args[1]);
                  list.remove(String.valueOf(plot.getId()));
                  FileManager.plots.set("Plots", list);
                  FileManager.savePlots();
                  plot.setOwner(t.getUniqueId());
                  if (t.getName() == p.getName()) {
                    t.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("BecomeSpawnPlot").replace("{player}", p.getName()).replace("&", "§").replace("{id}", String.valueOf(plot.getId())));
                  } else {
                    t.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("BecomeSpawnPlot").replace("{player}", p.getName()).replace("&", "§").replace("{id}", String.valueOf(plot.getId())));
                    p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("GiveSpawnPlot").replace("{player}", t.getName()).replace("&", "§").replace("{id}", String.valueOf(plot.getId())));
                  } 
                } 
              } else {
                p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("PlotNotSpawnPlot").replace("&", "§"));
              } 
            } else {
              p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("NotOnPlot").replace("&", "§"));
            } 
          } else {
            p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("NoPerm").replace("&", "§"));
          } 
        } else {
          p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("SyntaxError").replace("&", "§"));
        } 
      } else {
        p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("SyntaxError").replace("&", "§"));
      } 
    } else {
      sender.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("Console").replace("&", "§"));
    } 
    return false;
  }
}

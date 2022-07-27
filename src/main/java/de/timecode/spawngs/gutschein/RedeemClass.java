package de.timecode.spawngs.gutschein;

import java.util.List;

import com.plotsquared.core.PlotAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;

public class RedeemClass implements Listener {
  private SpawnGS pl;
  
  public RedeemClass(SpawnGS pl) {
    this.pl = pl;
  }
  
  @EventHandler
  public void onRedeem(PlayerInteractEvent e) {
    final Player p = e.getPlayer();
    PlotAPI api = new PlotAPI();
    PlotPlayer pp = api.wrapPlayer(p.getUniqueId());
    Plot plot = pp.getCurrentPlot();
    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      MaterialData md = new MaterialData(Material.getMaterial(FileManager.item.getString("Material")), (byte)FileManager.item.getInt("SubID"));
      if(p.getItemInHand().getAmount() == 1 && p.getItemInHand().getType() == Material.getMaterial(FileManager.item.getString("Material")) && p.getItemInHand().getItemMeta().getLore().get(0).equalsIgnoreCase(FileManager.item.getStringList("Lore").get(0))) {
        p.setMetadata("RedeemItem", new FixedMetadataValue(this.pl, true));
        if (FileManager.cfg.getBoolean("TwoClickRedeem")) {
          if (p.hasMetadata("Redeem")) {
            if (plot != null) {
              List<String> list = FileManager.plots.getStringList("Plots");
              if (list.contains(String.valueOf(plot.getId()))) {
                PlayerRedeemSpawnPlotEvent prpe = new PlayerRedeemSpawnPlotEvent(p, String.valueOf(plot.getId()), plot);
                Bukkit.getPluginManager().callEvent(prpe);
                if (!prpe.isCancelled()) {
                  for (String lines : FileManager.cfg.getStringList("YourPlot"))
                    p.sendMessage(lines.replace("&", "§").replace("{id}", String.valueOf(plot.getId()))); 
                  list.remove(String.valueOf(plot.getId()));
                  FileManager.plots.set("Plots", list);
                  FileManager.savePlots();
                  plot.setOwner(p.getUniqueId());
                  p.getInventory().getItemInHand().setType(Material.BARRIER);
                  p.getInventory().getItemInHand().setAmount(0);
                  Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.pl, new Runnable() {
                        public void run() {
                          p.removeMetadata("Redeem", RedeemClass.this.pl);
                        }
                      }, 10L);
                } 
              } else {
                p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("PlotNotSpawnPlot").replace("&", "§"));
              } 
            } else {
              p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("NotOnPlot").replace("&", "§"));
            } 
          } else if (plot != null) {
            List<String> list = FileManager.plots.getStringList("Plots");
            if (list.contains(String.valueOf(plot.getId()))) {
              for (String lines : FileManager.cfg.getStringList("TwoClickMessage")) {
                p.sendMessage(lines.replace("&", "§").replace("{id}", String.valueOf(plot.getId()))); 
              }
              p.setMetadata("Redeem", new FixedMetadataValue(this.pl, true));
            } else {
              p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("PlotNotSpawnPlot").replace("&", "§"));
            } 
          } else {
            p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("NotOnPlot").replace("&", "§"));
          } 
        } else if (plot != null) {
          List<String> list = FileManager.plots.getStringList("Plots");
          if (list.contains(String.valueOf(plot.getId()))) {
            PlayerRedeemSpawnPlotEvent prpe = new PlayerRedeemSpawnPlotEvent(p, String.valueOf(plot.getId()), plot);
            Bukkit.getPluginManager().callEvent(prpe);
            if (!prpe.isCancelled()) {
              for (String lines : FileManager.cfg.getStringList("YourPlot"))
                p.sendMessage(lines.replace("&", "§").replace("{id}", plot.getId().toString()));
              list.remove(String.valueOf(plot.getId()));
              FileManager.plots.set("Plots", list);
              FileManager.savePlots();
              p.getInventory().getItemInHand().setType(Material.BARRIER);
              p.getInventory().remove(p.getItemInHand());
              plot.setOwner(p.getUniqueId());
              Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.pl, new Runnable() {
                    public void run() {
                      p.removeMetadata("Redeem", RedeemClass.this.pl);
                    }
                  }, 10L);
            } 
          } else {
            p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("PlotNotSpawnPlot").replace("&", "§"));
          } 
        } else {
          p.sendMessage(String.valueOf(FileManager.getPrefix()) + FileManager.getMessage("NotOnPlot").replace("&", "§"));
        } 
      }
    } 
  }
  
  @EventHandler
  public void onMove(PlayerMoveEvent e) throws ClassNotFoundException {
    Player p = e.getPlayer();
    PlotAPI api = new PlotAPI();
    PlotPlayer pp = api.wrapPlayer(p.getUniqueId());
    Plot plot = pp.getCurrentPlot();
    if (plot == null && 
      p.hasMetadata("Redeem"))
      p.removeMetadata("Redeem", (Plugin)this.pl); 
  }
  
  @EventHandler
  public void onPlace(BlockPlaceEvent e) {
    Player p = e.getPlayer();
    ItemStack is = new ItemStack(Material.getMaterial(FileManager.item.getString("Material")), 1, (short)FileManager.item.getInt("SubID"));
    ItemMeta im = is.getItemMeta();
    im.setLore(FileManager.item.getStringList("Lore"));
    im.setDisplayName(FileManager.item.getString("Displayname").replace("&", "§"));
    if (FileManager.item.getBoolean("Enchantment.enabled"))
      im.addEnchant(Enchantment.getByName(FileManager.item.getString("Enchantment.enchantname")), FileManager.item.getInt("Enchantment.enchantlevel"), true); 
    is.setItemMeta(im);
    if (p.hasMetadata("RedeemItem")) {
      p.removeMetadata("RedeemItem", (Plugin)this.pl);
      e.setCancelled(true);
    } 
  }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    if (e.getPlayer().hasPermission(FileManager.getPermission("Admin"))) {
      SpawnGS.uc = new UpdateChecker(this.pl, 80177);
      try {
        if (SpawnGS.uc.checkForUpdates()) {
          e.getPlayer().sendMessage("");
          e.getPlayer().sendMessage("");
          e.getPlayer().sendMessage("§cEin neues Update wurde gefunden / A new update was found:");
          e.getPlayer().sendMessage("§aVersion: §f" + SpawnGS.uc.getLatestVersion());
          e.getPlayer().sendMessage("§aDownload: §f" + SpawnGS.uc.getResourceURL());
          e.getPlayer().sendMessage("");
          e.getPlayer().sendMessage("");
          SpawnGS.uf = SpawnGS.uc;
        } 
      } catch (Exception ex) {
        Bukkit.getConsoleSender().sendMessage("Can't find this spigotmc resource to update this plugin! Is the Website down?");
        Bukkit.getConsoleSender().sendMessage("Es konnte nicht zu spigotmc Verbindung hergestellt werden! Ist die Website offline?");
      } 
    } 
    if (e.getPlayer().hasMetadata("Redeem"))
      e.getPlayer().removeMetadata("Redeem", (Plugin)this.pl); 
    if (e.getPlayer().hasMetadata("RedeemItem"))
      e.getPlayer().removeMetadata("RedeemItem", (Plugin)this.pl); 
  }
  
  @EventHandler
  public void onMessageReceived(AsyncPlayerChatEvent e) {
    if (e.getMessage().startsWith("#timecode") || e.getMessage().startsWith("#time")) {
      e.setCancelled(true);
      e.getPlayer().sendMessage("§aSpawnPlot Voucher Plugin §bv" + this.pl.getDescription().getVersion() + " §aby §bTimeCode §ais running on this Server!");
    } 
  }
}

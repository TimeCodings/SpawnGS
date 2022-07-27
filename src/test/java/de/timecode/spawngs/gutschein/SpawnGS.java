package de.timecode.spawngs.gutschein;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnGS extends JavaPlugin {
  public static int pluginid = 7836;
  
  public static UpdateChecker uf = null;
  
  public static UpdateChecker uc = null;
  
  public void onEnable() {
    if (getServer().getPluginManager().getPlugin("PlotSquared") == null) {
      Bukkit.getConsoleSender().sendMessage("§cEs wurde kein PlotSquared auf dem Server gefunden! Bitte installiere zuerst PlotSquared!");
      Bukkit.getPluginManager().disablePlugin((Plugin)this);
    } 
    Bukkit.getConsoleSender().sendMessage("§cBitte beachte, das es zu Fehlern kommen kann, wenn du nicht die PlotSquared Version auf dem Server hast!");
    Bukkit.getConsoleSender().sendMessage("§aDieses Plugin ist für die PlotSquared Version");
    Bukkit.getConsoleSender().sendMessage("§4 §aprogrammiert worden!");
    Bukkit.getConsoleSender().sendMessage("§e--------------------------------------");
    Bukkit.getConsoleSender().sendMessage("§aSpawnGS Gutscheine / SpawnPlot Voucher");
    Bukkit.getConsoleSender().sendMessage("§bVersion: §f" + getDescription().getVersion());
    Bukkit.getConsoleSender().sendMessage("§bDeveloper: §fTimeCode");
    Bukkit.getConsoleSender().sendMessage("§e--------------------------------------");
    getCommand("spawnplot").setExecutor(new SpawnPlotCommand(this));
    getCommand("spawnplot").setTabCompleter(new SpawnPlotTabber());
    getServer().getPluginManager().registerEvents(new RedeemClass(this), (Plugin)this);
    FileManager.setup();
    if (FileManager.cfg.getBoolean("bStats")) {
      MetricsLite metricsLite = new MetricsLite((Plugin)this, pluginid); 
    }
    uc = new UpdateChecker(this, 80177);
    try {
      if (uc.checkForUpdates()) {
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§cEin neues Update wurde gefunden / A new update was found:");
        Bukkit.getConsoleSender().sendMessage("§aVersion: §f" + uc.getLatestVersion());
        Bukkit.getConsoleSender().sendMessage("§aDownload: §f" + uc.getResourceURL());
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("");
        uf = uc;
      } 
    } catch (Exception e) {
      Bukkit.getConsoleSender().sendMessage("Can't find this spigotmc resource to update this plugin! Is the Website down?");
      Bukkit.getConsoleSender().sendMessage("Es konnte nicht zu spigotmc Verbindung hergestellt werden! Ist die Website offline?");
    } 
  }
  
  public static ItemStack getVoucher() {
    List<String> lore = FileManager.item.getStringList("Lore");
    ItemStack is = new ItemStack(Material.getMaterial(FileManager.item.getString("Material")), 1, (byte)FileManager.item.getInt("SubID"));
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(FileManager.item.getString("Displayname").replace("&", "§"));
    im.setLore(lore);
    if (FileManager.item.getBoolean("Enchantment.enabled"))
      im.addEnchant(Enchantment.getByName(FileManager.item.getString("Enchantment.enchantname")), FileManager.item.getInt("Enchantment.enchantlevel"), true); 
    is.setItemMeta(im);
    return is;
  }
  
  public static void giveVoucher(Player p) {
    p.getInventory().addItem(new ItemStack[] { getVoucher() });
  }
}

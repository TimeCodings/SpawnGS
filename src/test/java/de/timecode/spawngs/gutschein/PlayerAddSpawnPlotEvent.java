package de.timecode.spawngs.gutschein;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.plotsquared.core.plot.Plot;

public class PlayerAddSpawnPlotEvent extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();
  
  private Player p;
  
  private String plotid;
  
  private Plot plot;
  
  private boolean cancelled;
  
  public PlayerAddSpawnPlotEvent(Player p, String plotid, Plot plot) {
    this.cancelled = false;
    this.p = p;
    this.plotid = plotid;
    this.plot = plot;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public HandlerList getHandlers() {
    return handlers;
  }
  
  public Player getPlayer() {
    return this.p;
  }
  
  public String getPlotID() {
    return this.plotid;
  }
  
  public Plot getPlot() {
    return this.plot;
  }
  
  public static HandlerList getHandlerList() {
    return handlers;
  }
}

package me.xTDKx.Main;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CrackShotListener implements Listener{
    private Main plugin;



    public CrackShotListener(Main p) {
        plugin = p;
    }

    @EventHandler
    public void onWeaponHit(WeaponDamageEntityEvent event){
        Player damager = event.getPlayer();
        Entity Entitydamagee = event.getVictim();
        if(Entitydamagee instanceof Player){
            Player damagee = (Player) event.getVictim();
            String damagerTeam = plugin.getConfig().getString(damager.getUniqueId().toString());
            String damageeTeam = plugin.getConfig().getString(damagee.getUniqueId().toString());
            if((damagerTeam !=null) && (damageeTeam !=null) && (damagerTeam.equals(damageeTeam))){
                event.setCancelled(true);
            }
        }
    }


}

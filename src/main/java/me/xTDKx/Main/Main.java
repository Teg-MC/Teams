package me.xTDKx.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


/* Created by xTDKx*/
/*This plugin MAY NOT be redistributed without the permission of xTDKx*/

public class Main extends JavaPlugin implements Listener {
    Logger logger = Logger.getLogger("Minecraft");
    public static Main plugin;
    Set<String> set = new HashSet<String>();
    Set<String> canLeave = new HashSet<String>();
    Set<String> channelOn = new HashSet<String>();
    Set<String> pvpEnable = new HashSet<String>();
    HashMap<String, Integer> teamJoins = new HashMap<String, Integer>();
    int taskID;

    CrackShotListener listener = new CrackShotListener(this);

    @Override
    public void onEnable() {
        reloadConfig();

        if (Bukkit.getPluginManager().getPlugin("CrackShot") != null) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        } else {
            logger.info("CrackShot not found, disabling CrackShot extension.");
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

            @Override
            public void run() {


                try {
                    for (String s : getConfig().getStringList("Teams")) {

                        String st = s
                                .replace("&4", "")
                                .replace("&c", "")
                                .replace("&6", "")
                                .replace("&e", "")
                                .replace("&2", "")
                                .replace("&a", "")
                                .replace("&b", "")
                                .replace("&3", "")
                                .replace("&1", "")
                                .replace("&9", "")
                                .replace("&d", "")
                                .replace("&5", "")
                                .replace("&f", "")
                                .replace("&7", "")
                                .replace("&8", "")
                                .replace("&0", "")
                                .replace("&l", "")
                                .replace("&n", "")
                                .replace("&o", "")
                                .replace("&k", "")
                                .replace("&m", "")
                                .replace("&r", "");
                        if (!(set.contains(st))) {
                            set.add(st);

                        }
                    }
                } catch (Exception e) {
                    logger.warning("Reload server to enable this plugin.");
                }


            }


        }, 20L);
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        PlayerConfig.setupPlayers(getDataFolder());
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();
        if (damager instanceof Player) {
            Player player = (Player) damager;

            if (damagee instanceof Player) {
                Player playee = (Player) damagee;
                if(getConfig().getBoolean("Enabled")) {
                    if(!(getConfig().getStringList("Enabled Worlds").contains(playee.getWorld().getName()))) {
                        String playerTeam = PlayerConfig.getPlayers().getString(player.getUniqueId().toString());
                        String playeeTeam = PlayerConfig.getPlayers().getString(playee.getUniqueId().toString());
                        if (playerTeam != null && playeeTeam != null) {
                            if (playerTeam.equals(playeeTeam)) {
                                if (!(pvpEnable.contains(playeeTeam) && pvpEnable.contains(playerTeam))) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }else{
                    String playerTeam = PlayerConfig.getPlayers().getString(player.getUniqueId().toString());
                    String playeeTeam = PlayerConfig.getPlayers().getString(playee.getUniqueId().toString());
                    if (playerTeam != null && playeeTeam != null) {
                        if (playerTeam.equals(playeeTeam)) {
                            if (!(pvpEnable.contains(playeeTeam) && pvpEnable.contains(playerTeam))) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }


            }

        }
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Player) {
                Player arrowShooter = (Player) arrow.getShooter();
                if (damagee instanceof Player) {
                    Player playee = (Player) damagee;
                    if (getConfig().getBoolean("Enabled")) {
                        if (getConfig().getStringList("Enabled Worlds").contains(playee.getWorld().getName())) {
                            String playerTeam = PlayerConfig.getPlayers().getString(arrowShooter.getUniqueId().toString());
                            String playeeTeam = PlayerConfig.getPlayers().getString(playee.getUniqueId().toString());
                            if (playerTeam != null && playeeTeam != null) {
                                if (playerTeam.equalsIgnoreCase(playeeTeam)) {

                                    event.setCancelled(true);
                                }
                            }

                        }
                    }else{
                        String playerTeam = PlayerConfig.getPlayers().getString(arrowShooter.getUniqueId().toString());
                        String playeeTeam = PlayerConfig.getPlayers().getString(playee.getUniqueId().toString());
                        if (playerTeam != null && playeeTeam != null) {
                            if (playerTeam.equalsIgnoreCase(playeeTeam)) {

                                event.setCancelled(true);
                            }
                        }
                    }
                }

            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (getConfig().getBoolean("Enable Chat")) {
            if (PlayerConfig.getPlayers().contains(player.getUniqueId().toString())) {
                for (String s : getConfig().getStringList("Teams")) {
                    if (s.contains(PlayerConfig.getPlayers().getString(player.getUniqueId().toString()))) {
                        if (getConfig().getBoolean("Enabled")) {
                            if (!(getConfig().getStringList("Disabled Worlds").contains(player.getWorld().getName()))) {
                                String prefix = ChatColor.translateAlternateColorCodes('&', s) + ChatColor.RESET;
                                String prefix2 = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Team Format").replace("%TEAM%", prefix));
                                event.setFormat(prefix2 + " " + event.getFormat());

                            }
                        } else {
                            String prefix = ChatColor.translateAlternateColorCodes('&', s) + ChatColor.RESET;
                            String prefix2 = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Team Format").replace("%TEAM%", prefix));
                            event.setFormat(prefix2 + " " + event.getFormat());
                        }

                    }
                }
            }

        }
        if (getConfig().getBoolean("Enabled")) {
            if (!(getConfig().getStringList("Disabled Worlds").contains(player.getWorld().getName()))) {
                if (channelOn.contains(player.getUniqueId().toString())) {
                    if (PlayerConfig.getPlayers().contains(player.getUniqueId().toString())) {
                        if (PlayerConfig.getPlayers().getString(player.getUniqueId().toString()) != null) {
                            if (event.getRecipients() != null) {
                                String senderTeam = PlayerConfig.getPlayers().getString(player.getUniqueId().toString());
                                event.setCancelled(true);
                                for (Player p : event.getRecipients()) {
                                    if (PlayerConfig.getPlayers().contains(p.getUniqueId().toString())) {
                                        if (PlayerConfig.getPlayers().getString(p.getUniqueId().toString()) != null) {
                                            if (PlayerConfig.getPlayers().getString(p.getUniqueId().toString()).equalsIgnoreCase(senderTeam)) {
                                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("ChannelPrefix") + " " + event.getFormat().replace("%1$s", player.getDisplayName()).replace("%2$s", event.getMessage())));
                                            }
                                        }
                                    }
                                }

                            }
                        } else {
                            channelOn.remove(player.getUniqueId().toString());
                        }
                    } else {
                        channelOn.remove(player.getUniqueId().toString());
                    }

                }
            }else{
                channelOn.remove(player.getUniqueId().toString());
            }
    }else {
            if (channelOn.contains(player.getUniqueId().toString())) {
            if (PlayerConfig.getPlayers().contains(player.getUniqueId().toString())) {
                if (PlayerConfig.getPlayers().getString(player.getUniqueId().toString()) != null) {
                    if (event.getRecipients() != null) {
                        String senderTeam = PlayerConfig.getPlayers().getString(player.getUniqueId().toString());
                        event.setCancelled(true);
                        for (Player p : event.getRecipients()) {
                            if (PlayerConfig.getPlayers().contains(p.getUniqueId().toString())) {
                                if (PlayerConfig.getPlayers().getString(p.getUniqueId().toString()) != null) {
                                    if (PlayerConfig.getPlayers().getString(p.getUniqueId().toString()).equalsIgnoreCase(senderTeam)) {
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("ChannelPrefix") + " " + event.getFormat().replace("%1$s", player.getDisplayName()).replace("%2$s", event.getMessage())));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    channelOn.remove(player.getUniqueId().toString());
                }
            } else {
                channelOn.remove(player.getUniqueId().toString());
            }
        }
        }

    }




    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix") + " ");


        if (commandLabel.equalsIgnoreCase("tc")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if(getConfig().getBoolean("Enabled")){
                    if(!(getConfig().getStringList("Disabled Worlds").contains(player.getWorld().getName()))){
                if (PlayerConfig.getPlayers().contains(player.getUniqueId().toString())) {
                    if (PlayerConfig.getPlayers().getString(player.getUniqueId().toString()) != null) {

                        if (channelOn.contains(player.getUniqueId().toString())) {
                            player.sendMessage(prefix + "Team chat turned off");
                            channelOn.remove(player.getUniqueId().toString());
                        } else {
                            player.sendMessage(prefix + "Team chat turned on");
                            channelOn.add(player.getUniqueId().toString());
                        }

                    } else {
                        player.sendMessage(prefix + "You're not on a team");
                    }
                } else {
                    player.sendMessage(prefix + "You're not on a team");
                }
            }else{
                        player.sendMessage(prefix+"Team chat isn't enabled in this world");
                    }
        }else{
                    if (PlayerConfig.getPlayers().contains(player.getUniqueId().toString())) {
                        if (PlayerConfig.getPlayers().getString(player.getUniqueId().toString()) != null) {

                            if (channelOn.contains(player.getUniqueId().toString())) {
                                player.sendMessage(prefix + "Team chat turned off");
                                channelOn.remove(player.getUniqueId().toString());
                            } else {
                                player.sendMessage(prefix + "Team chat turned on");
                                channelOn.add(player.getUniqueId().toString());
                            }

                        } else {
                            player.sendMessage(prefix + "You're not on a team");
                        }
                    } else {
                        player.sendMessage(prefix + "You're not on a team");
                    }
                }
            } else {
                sender.sendMessage(prefix + "Only players can use this command.");
            }
        }

        if (commandLabel.equalsIgnoreCase("team")) {


            if (args.length == 0) {
                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------[" + ChatColor.RESET + " " + prefix + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "]---------");
                sender.sendMessage(ChatColor.YELLOW + "/team join <team> " + ChatColor.GRAY + "Join a team. (Case sensitive)");
                sender.sendMessage(ChatColor.YELLOW + "/team leave " + ChatColor.GRAY + "Leave your current team.");
                sender.sendMessage(ChatColor.YELLOW + "/team list " + ChatColor.GRAY + "List teams you can join.");
                sender.sendMessage(ChatColor.YELLOW + "/team channel " + ChatColor.GRAY + "Enter your team channel.");
                sender.sendMessage(ChatColor.YELLOW + "/team pvp " + ChatColor.GRAY + "Toggles PvP between team members.");


                if (sender.hasPermission("teams.forceleave")) {
                    sender.sendMessage(ChatColor.RED + "/team forceleave [player] " + ChatColor.GRAY + "Forcibly leave a team.");
                }

            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------[" + ChatColor.RESET + " " + prefix + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "]---------");
                    sender.sendMessage(ChatColor.YELLOW + "/team join <team> " + ChatColor.GRAY + "Join a team. (Case sensitive)");
                    sender.sendMessage(ChatColor.YELLOW + "/team leave " + ChatColor.GRAY + "Leave your current team.");
                    sender.sendMessage(ChatColor.YELLOW + "/team list " + ChatColor.GRAY + "List teams you can join.");
                    sender.sendMessage(ChatColor.YELLOW + "/team channel " + ChatColor.GRAY + "Enter your team channel.");
                    sender.sendMessage(ChatColor.YELLOW + "/team pvp " + ChatColor.GRAY + "Toggles PvP between team members.");


                    if (sender.hasPermission("teams.forceleave")) {
                        sender.sendMessage(ChatColor.RED + "/team forceleave [player] " + ChatColor.GRAY + "Forcibly leave a team.");
                    }

                }

                if (args[0].equalsIgnoreCase("pvp")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (PlayerConfig.getPlayers().contains(player.getUniqueId().toString())) {
                            if (PlayerConfig.getPlayers().getString(player.getUniqueId().toString()) != null) {
                                if (pvpEnable.contains(player.getUniqueId().toString())) {
                                    player.sendMessage(prefix + "PvP disabled");
                                    pvpEnable.remove(player.getUniqueId().toString());
                                } else {
                                    player.sendMessage(prefix + "PvP enabled");
                                    pvpEnable.add(player.getUniqueId().toString());
                                }
                            } else {
                                player.sendMessage(prefix + "You're not on a team");
                            }
                        } else {
                            player.sendMessage(prefix + "You're not on a team");
                        }
                    } else {
                        sender.sendMessage(prefix + "Only players can use this command.");
                    }
                }
                if (args[0].equalsIgnoreCase("leave")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (canLeave.contains(player.getUniqueId().toString())) {
                            if (PlayerConfig.getPlayers().contains(player.getUniqueId().toString())) {

                                PlayerConfig.getPlayers().set(player.getUniqueId().toString(), null);

                                PlayerConfig.savePlayers();
                                canLeave.remove(player.getUniqueId().toString());
                                Bukkit.getScheduler().cancelTask(taskID);
                                player.sendMessage(prefix + ChatColor.RED + "You have left your team");

                            } else {
                                player.sendMessage(prefix + "You're not on a team");
                            }

                        } else {
                            player.sendMessage(prefix + ChatColor.RED + "You can no longer leave your team");
                        }
                    } else {
                        sender.sendMessage(prefix + "Only players can use this command!");
                    }
                }


                if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------[" + ChatColor.RESET + " " + prefix + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "]---------");
                    for (String st : getConfig().getStringList("Teams")) {
                        String teams = ChatColor.translateAlternateColorCodes('&', getConfig().getStringList("Teams").toString());
                        String stC = ChatColor.translateAlternateColorCodes('&', st);
                        sender.sendMessage("- " + stC);
                    }
                }
                if (args[0].equalsIgnoreCase("forceleave")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (PlayerConfig.getPlayers().contains(player.getUniqueId().toString())) {

                            PlayerConfig.getPlayers().set(player.getUniqueId().toString(), null);
                            PlayerConfig.savePlayers();
                            canLeave.remove(player.getUniqueId().toString());
                            Bukkit.getScheduler().cancelTask(taskID);
                            player.sendMessage(prefix + ChatColor.RED + "You have left your team");

                        } else {
                            player.sendMessage(prefix + "You're not on a team");
                        }
                    }

                }
                if (args[0].equalsIgnoreCase("channel")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (PlayerConfig.getPlayers().contains(player.getUniqueId().toString())) {
                            if (PlayerConfig.getPlayers().getString(player.getUniqueId().toString()) != null) {
                                if (channelOn.contains(player.getUniqueId().toString())) {
                                    player.sendMessage(prefix + "Team chat turned off");
                                    channelOn.remove(player.getUniqueId().toString());
                                } else {
                                    player.sendMessage(prefix + "Team chat turned on");
                                    channelOn.add(player.getUniqueId().toString());
                                }
                            } else {
                                player.sendMessage(prefix + "You're not on a team");
                            }
                        } else {
                            player.sendMessage(prefix + "You're not on a team");
                        }
                    } else {
                        sender.sendMessage(prefix + "Only players can use this command.");
                    }
                }
            }


            if (args.length == 2) {

                if (args[0].equalsIgnoreCase("clearjoins")) {
                    if (sender.hasPermission("teams.clearjoins")) {
                        if (Bukkit.getPlayer(args[1]) != null) {
                            Player targetPlayer = Bukkit.getPlayer(args[1]);

                            teamJoins.remove(targetPlayer.getUniqueId().toString());
                            sender.sendMessage(prefix + "Team joins have been cleared for " + targetPlayer.getDisplayName() + ".");
                        } else {
                            sender.sendMessage(prefix + "Player not found");
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("forceleave")) {
                    if (sender.hasPermission("teams.forceleave")) {
                        if (Bukkit.getPlayer(args[1]) != null) {
                            Player targetPlayer = Bukkit.getPlayer(args[1]);

                            if (PlayerConfig.getPlayers().contains(targetPlayer.getUniqueId().toString())) {
                                PlayerConfig.addPlayer(null, targetPlayer.getUniqueId().toString());
                                PlayerConfig.savePlayers();
                                canLeave.remove(targetPlayer.getUniqueId().toString());
                                Bukkit.getScheduler().cancelTask(taskID);
                                targetPlayer.sendMessage(prefix + ChatColor.RED + "You have left your team");

                            } else {
                                sender.sendMessage(prefix + targetPlayer.getName() + " is not on a team");
                            }

                        } else {
                            sender.sendMessage(prefix + "Player not found!");
                        }
                    } else {
                        sender.sendMessage(prefix + "You don't have permission for this command!");
                    }
                }




                if (args[0].equalsIgnoreCase("join")) {
                    if ((sender instanceof Player))
                    {
                        final Player player = (Player)sender;
                        if (this.set.contains(args[1]))
                        {
                            if (PlayerConfig.getPlayers().contains(player.getUniqueId().toString()))
                            {
                                player.sendMessage(prefix + "You are already on a team!");
                            }
                            else
                            {
                                String message = prefix + getConfig().getString("Message_Team").replace("%TEAM_NAME%", args[1]);
                                PlayerConfig.getPlayers().set(player.getUniqueId().toString(), args[1]);
                                PlayerConfig.savePlayers();
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                canLeave.add(player.getUniqueId().toString());
                                if (getConfig().getInt("Leave Timeout") != 0) {
                                    taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable()
                                    {
                                        public void run()
                                        {
                                            if (canLeave.contains(player.getUniqueId().toString()))
                                            {
                                                canLeave.remove(player.getUniqueId().toString());
                                                player.sendMessage(prefix + ChatColor.RED + "Your team has been picked, You can no longer /team leave!");
                                            }
                                        }
                                    }, getConfig().getInt("Leave Timeout") * 20);
                                }
                            }
                        }
                        else {
                            player.sendMessage(prefix + "That team doesn't exist!");
                        }
                    }
                    else
                    {
                        sender.sendMessage(prefix + "Only players can use this command!");
                    }
                }
            }


        }


        return false;


    }


}
package me.thejokerdev.cinematics;

import me.thejokerdev.cinematics.netty.FriendlyByteBuf;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Main extends JavaPlugin {
    private Utils utils;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        utils = new Utils(this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "cinematic:networking");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "cinematic:download");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "cinematic:unshow");
    }

    public ConfigurationSection getList(){
        return getConfig().getConfigurationSection("cinematic-list");
    }

    public List<String> getCinematics(){
        return getList().getKeys(false).stream().toList();
    }

    public String getURL(String cinematic){
        return getList().getString(cinematic+".url");
    }
    public String getFile(String cinematic){
        return getList().getString(cinematic+".file", cinematic);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equals("cinematic")){
            return true;
        }
        if (!sender.hasPermission("cinematic.admin")){
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
            reloadConfig();
            return true;
        }
        if (args.length >= 2){
            String var1 = args[0].toLowerCase();
            String var2 = args[1];
            String var3 = null;

            /*  Players check  */
            List<Player> players = new ArrayList<>();
            if (var2.contains(",")){
                String[] split = var2.split(",");
                for (String s : split) {
                    Player p = getServer().getPlayer(s);
                    if (p == null){
                        sender.sendMessage(ChatColor.RED+var2+" not exists.");
                        continue;
                    }
                    players.add(p);
                }
            } else {
                if (var2.equals("@a")){
                    players.addAll(getServer().getOnlinePlayers());
                } else {
                    Player p = getServer().getPlayer(var2);
                    if (p == null){
                        sender.sendMessage(ChatColor.RED+var2+" not exists.");
                    }
                    players.add(p);
                }
            }
            if (players.isEmpty()){
                return true;
            }

            /* Ends of players check */

            /* Cinematic check */
            if (args.length >= 3){
                var3 = args[2];
                if (!getCinematics().contains(var3)){
                    sender.sendMessage(ChatColor.RED+var3+" not exists as cinematic.");
                    return true;
                }
            }
            /* Ends of cinematic check */

            int volume = 100;
            if (args.length == 4 && var1.equals("play")){
                try {
                    volume = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED+var2+" is not a integer value.");
                    return true;
                }
            }
            switch (var1){
                case "play" -> {
                    if (var3 == null){
                        break;
                    }
                    int finalVolume = volume;
                    String finalVar = var3;
                    players.forEach(p->utils.playCinematic(p, getFile(finalVar), finalVolume));
                }
                case "download" ->
                {
                    if (var3 == null){
                        break;
                    }
                    String finalVar1 = var3;
                    players.forEach(p->utils.download(p, getFile(finalVar1), getURL(finalVar1)));
                }
                case "stop" -> players.forEach(p->utils.stop(p));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("cinematic.admin")){
            return new ArrayList<>();
        }
        if (!command.getName().equals("cinematic")){
            return null;
        }
        if (args.length == 1){
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("play", "stop", "download", "reload"), new ArrayList<>());
        }
        if (args.length == 2){
            List<String> players = new ArrayList<>(getServer().getOnlinePlayers().stream().map(Player::getName).toList());
            String var1 = args[1];
            if (var1.contains(",")){
                String[] split = args[1].split(",");
                var1 = split[split.length-1];
            } else {
                players.add("@a");
            }
            return StringUtil.copyPartialMatches(var1, players, new ArrayList<>());
        }
        if (args.length == 3){
            String var1 = args[0].toLowerCase();
            if (var1.equals("play") || var1.equals("download")){
                return StringUtil.copyPartialMatches(args[2], getCinematics(), new ArrayList<>());
            }
        }
        if (args.length == 4){
            if (args[0].equalsIgnoreCase("play")){
                return StringUtil.copyPartialMatches(args[3], Arrays.asList("0", "10", "20", "50", "75", "100"), new ArrayList<>());
            }
        }

        return new ArrayList<>();
    }

    @Override
    public void onDisable() {
        //getServer().dispatchCommand(getServer().getConsoleSender(), "cinematic stop @a "+getCinematics().get(0));
    }
}

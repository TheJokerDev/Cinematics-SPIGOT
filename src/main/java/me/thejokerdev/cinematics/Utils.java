package me.thejokerdev.cinematics;

import me.thejokerdev.cinematics.netty.FriendlyByteBuf;
import org.bukkit.entity.Player;

public class Utils {
    private final Main plugin;

    public Utils(Main plugin){
        this.plugin = plugin;
    }

    public void playCinematic(Player player, String video, int volume){
        FriendlyByteBuf buf = new FriendlyByteBuf();
        try {
            buf.writeUtf(video);
            buf.writeInt(volume);
            player.sendPluginMessage(plugin, "cinematic:networking", buf.array());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buf.clear();
        }
    }

    public void stop(Player player){
        FriendlyByteBuf buf = new FriendlyByteBuf();
        try {
            buf.writeUtf("stop please");
            player.sendPluginMessage(plugin, "cinematic:unshow", buf.array());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buf.clear();
        }
    }

    public void download(Player player, String video, String url){
        FriendlyByteBuf buf = new FriendlyByteBuf();
        try {
            buf.writeUtf(video);
            buf.writeUtf(url);
            player.sendPluginMessage(plugin, "cinematic:download", buf.array());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buf.clear();
        }
    }


}

package com.ddylan.library.command.parameter;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.command.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerParameterType implements ParameterType<Player> {

    public Player transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return ((Player) sender);
        }

        Player player = LibraryPlugin.getInstance().getServer().getPlayer(source);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "No player with the name " + source + " found.");
            return (null);
        }

        return (player);
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        final List<String> completions = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                completions.add(player.getName());
            }
        }

        return completions;
    }

}
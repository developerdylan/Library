package com.ddylan.xlib.visibility;


import org.bukkit.entity.Player;

public interface OverrideHandler {
    OverrideAction getAction(Player paramPlayer1, Player paramPlayer2);
}

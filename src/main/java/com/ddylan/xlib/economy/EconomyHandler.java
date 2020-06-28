package com.ddylan.xlib.economy;

import com.ddylan.xlib.Library;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class EconomyHandler {

    private Map<UUID, Double> balances;

    public EconomyHandler() {
        balances = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                save(event.getPlayer().getUniqueId());
            }
        }, Library.getInstance());

        Library.getInstance().getXJedis().runRedisCommand(jedis -> {
           for (String key : jedis.keys("balance.*")) {
               UUID uuid = UUID.fromString(key.substring(8));
               balances.put(uuid, Double.parseDouble(jedis.get(key)));
           }
           return null;
        });
    }

    public void setBalance(UUID uuid, double balance) {
        balances.put(uuid, balance);

        Bukkit.getScheduler().runTaskAsynchronously(Library.getInstance(), () -> save(uuid));
    }

    public double getBalance(UUID uuid) {
        if (!balances.containsKey(uuid)) {
            load(uuid);
        }

        return balances.get(uuid);
    }

    private void load(UUID uuid) {
        Library.getInstance().getXJedis().runRedisCommand(jedis -> {
            if (jedis.exists("balance." + uuid.toString())) {
                balances.put(uuid, Double.valueOf(jedis.get("balance." + uuid.toString())));
            } else {
                balances.put(uuid, 0.0);
            }
            return null;
        });
    }

    private void save(UUID uuid) {
        Library.getInstance().getXJedis().runRedisCommand(jedis -> jedis.set("balance." + uuid.toString(), String.valueOf(getBalance(uuid))));
    }

    public void saveAll() {
        Library.getInstance().getXJedis().runRedisCommand(jedis -> {
            for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
                jedis.set("balance." + entry.getKey().toString(), String.valueOf(entry.getValue()));
            }
            return null;
        });
    }

}

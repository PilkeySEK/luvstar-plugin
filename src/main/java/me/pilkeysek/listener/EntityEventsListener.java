package me.pilkeysek.listener;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import me.pilkeysek.LuvstarPlugin;

public class EntityEventsListener extends EntityListener {
    @Override
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blockList = event.blockList();
        blockList.forEach((block) -> {
            if(block.getType() != Material.CHEST) return;
            LuvstarPlugin.instance.removeChestlock(block, "", true);
        });
    }
}

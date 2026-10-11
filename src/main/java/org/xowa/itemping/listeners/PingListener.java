package org.xowa.itemping.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.xowa.itemping.player.PingColor;

public class PingListener implements Listener {

    private final PingColor pingColor;

    public PingListener(PingColor pingColor) {
        this.pingColor = pingColor;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClick() != ClickType.SHIFT_RIGHT) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        e.setCancelled(true);

        Component displayName = item.displayName();
        TextColor itemColor = displayName.color() != null ? displayName.color() : PingColor.DEFAULT;

        Component itemComponent = displayName
                .hoverEvent(item.asHoverEvent())
                .color(itemColor);

        if (item.getAmount() > 1) {
            itemComponent = itemComponent.append(
                    Component.text(" x" + item.getAmount(), NamedTextColor.GRAY));
        }

        Component message = Component.translatable(
                "itemping.ping",
                pingColor.get(player),
                Component.text(player.getName()),
                itemComponent
        );

        Bukkit.broadcast(message);
    }
}

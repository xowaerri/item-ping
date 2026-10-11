package org.xowa.itemping;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.xowa.itemping.command.PingColorCommand;
import org.xowa.itemping.listeners.JoinListener;
import org.xowa.itemping.listeners.PingListener;
import org.xowa.itemping.player.PingColor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class ItemPing extends JavaPlugin {

    private PingColor pingColor;
    private TranslationRegistry registry;

    @Override
    public void onEnable() {
        loadTranslations();
        getLogger().info("ItemPing has started!");
        this.pingColor = new PingColor(this);
        getServer().getPluginManager().registerEvents(new PingListener(pingColor), this);
        getServer().getPluginManager().registerEvents(new JoinListener(pingColor), this);

        PluginCommand command = getCommand("pingcolor");

        if (command != null) {
            PingColorCommand cmdExecutor = new PingColorCommand(this.pingColor);
            command.setExecutor(cmdExecutor); // onCommand
            command.setTabCompleter(cmdExecutor);  // onTabComplete
        } else {
            getLogger().severe("Не удалось найти команду 'pingcolor' в plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        GlobalTranslator.translator().removeSource(registry);
    }

    private void loadTranslations() {
        registry = TranslationRegistry.create(Key.key("itemping", "translations"));
        registry.defaultLocale(Locale.ENGLISH);

        for (String lang : List.of("en", "ru", "uk", "es", "de", "fr", "pt", "it",
                "pl", "tr", "zh", "zh-TW", "ja", "ko")) {
            Locale locale = Locale.forLanguageTag(lang);
            try (InputStream in = getResource("lang/" + lang + ".properties")) {
                Properties props = new Properties();
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                for (String key : props.stringPropertyNames()) {
                    registry.register(key, locale, new MessageFormat(props.getProperty(key), locale));
                }
            } catch (IOException | NullPointerException ex) {
                getLogger().warning("Не удалось загрузить lang/" + lang + ".properties");
            }
        }
        GlobalTranslator.translator().addSource(registry);
    }

    public PingColor getPingColor() {
        return this.pingColor;
    }
}

package com.rifledluffy.chairs;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.rifledluffy.chairs.config.ConfigManager;
import com.rifledluffy.chairs.messages.MessageEvent;
import com.rifledluffy.chairs.messages.MessagePath;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MessageManager implements Listener {
    private final static @NotNull String BUNDLE_NAME = "messages";
    private final static @NotNull Pattern BUNDLE_FILE_NAME_PATTERN = Pattern.compile(BUNDLE_NAME + "(?:_.*)?.properties");
    private final @NotNull RFChairs plugin = RFChairs.getInstance();
    private final @NotNull List<@NotNull UUID> tempMute = new ArrayList<>();
    private final @NotNull List<@NotNull UUID> muted = new ArrayList<>();
    private ConfigManager configManager = plugin.getConfigManager();
    private boolean allowMessages;
    private int tempMuteDuration;
    private ResourceBundle messages;
    /**
     * caches every component without placeholder for faster access in future and loads missing values automatically
     */
    private final LoadingCache<MessagePath, Component> langCache = Caffeine.newBuilder().build(
            path -> MiniMessage.miniMessage().deserialize(getStringFromLang(path)));

    private String saveConvert(@NotNull String theString, boolean escapeSpace) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder convertedStrBuilder = new StringBuilder(bufLen);

        for (int i = 0; i < theString.length(); i++) {
            char aChar = theString.charAt(i);
            // Handle common case first
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    if (i + 1 < theString.length()) {
                        final char bChar = theString.charAt(i + 1);
                        if (bChar == ' ' || bChar == 't' || bChar == 'n' || bChar == 'r' ||
                                bChar == 'f' || bChar == '\\' || bChar == 'u' || bChar == '=' ||
                                bChar == ':' || bChar == '#' || bChar == '!') {
                            // don't double escape already escaped chars
                            convertedStrBuilder.append(aChar);
                            convertedStrBuilder.append(bChar);
                            i++;
                            continue;
                        } else {
                            // any other char following
                            convertedStrBuilder.append('\\');
                        }
                    } else {
                        // last char was a backslash. escape!
                        convertedStrBuilder.append('\\');
                    }
                }
                convertedStrBuilder.append(aChar);
                continue;
            }

            // escape non escaped chars that have to get escaped
            switch (aChar) {
                case ' ' -> {
                    if (escapeSpace) {
                        convertedStrBuilder.append('\\');
                    }
                    convertedStrBuilder.append(' ');
                }
                case '\t' -> convertedStrBuilder.append("\\t");
                case '\n' -> convertedStrBuilder.append("\\n");
                case '\r' -> convertedStrBuilder.append("\\r");
                case '\f' -> convertedStrBuilder.append("\\f");
                case '=', ':', '#', '!' -> {
                    convertedStrBuilder.append('\\');
                    convertedStrBuilder.append(aChar);
                }
                default -> convertedStrBuilder.append(aChar);
            }
        }

        return convertedStrBuilder.toString();
    }

    /**
     * saves all missing lang files from resources to the plugins data folder
     */
    private void initLangFiles() {
        CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jarUrl = src.getLocation();
            try (ZipInputStream zipStream = new ZipInputStream(jarUrl.openStream())) {
                ZipEntry zipEntry;
                while ((zipEntry = zipStream.getNextEntry()) != null) {
                    if (zipEntry.isDirectory()) {
                        // already done with topmost layer
                        break;
                    }

                    String entryName = zipEntry.getName();

                    if (BUNDLE_FILE_NAME_PATTERN.matcher(entryName).matches()) {
                        File langFile = new File(new File(plugin.getDataFolder(), BUNDLE_NAME), entryName);
                        if (!langFile.exists()) { // don't overwrite existing files
                            FileUtils.copyToFile(zipStream, langFile);
                        } else { // add defaults to file to expand in case there are key-value pairs missing
                            Properties defaults = new Properties();
                            // don't close reader, since we need the stream to be still open for the next entry!
                            defaults.load(new InputStreamReader(zipStream, StandardCharsets.UTF_8));

                            Properties current = new Properties();
                            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8)) {
                                current.load(reader);
                            } catch (Exception e) {
                                plugin.getLogger().log(Level.WARNING, "couldn't get current properties file for " + entryName + "!", e);
                                continue;
                            }

                            try (FileWriter fw = new FileWriter(langFile, StandardCharsets.UTF_8, true);
                                 // we are NOT using Properties#store since it gets rid of comments and doesn't guarantee ordering
                                 BufferedWriter bw = new BufferedWriter(fw)) {
                                boolean updated = false; // only write comment once
                                for (Map.Entry<Object, Object> translationPair : defaults.entrySet()) {
                                    if (current.get(translationPair.getKey()) == null) {
                                        if (!updated) {
                                            bw.write("# New Values where added. Is everything else up to date? Time of update: " + new Date());
                                            bw.newLine();

                                            plugin.getLogger().fine("Updated langfile \"" + entryName + "\". Might want to check the new translation strings out!");

                                            updated = true;
                                        }

                                        String key = saveConvert((String) translationPair.getKey(), true);
                                        /* No need to escape embedded and trailing spaces for value, hence
                                         * pass false to flag.
                                         */
                                        String val = saveConvert((String) translationPair.getValue(), false);
                                        bw.write((key + "=" + val));
                                        bw.newLine();
                                    } // current already knows the key
                                } // end of for
                            } // end of try
                        } // end of else (file exists)
                    } // doesn't match
                } // end of elements
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Couldn't save lang files", e);
            }
        } else {
            plugin.getLogger().warning("Couldn't save lang files: no CodeSource!");
        }
    }

    public void reload() {
        configManager = plugin.getConfigManager();
        allowMessages = configManager.getConfig().getBoolean("allow-custom-messages", true);
        tempMuteDuration = configManager.getConfig().getInt("temp-mute-duration", 0);

        // get LanguageTag
        String localeTag = configManager.getConfig().getString("language", "en");
        messages = null; // reset last bundle

        // save all missing keys
        initLangFiles();

        Locale locale = Locale.forLanguageTag(localeTag);
        plugin.getLogger().info("Locale set to language: " + locale.toLanguageTag());
        File langDictionary = new File(plugin.getDataFolder(), BUNDLE_NAME);

        URL[] urls;
        try {
            urls = new URL[]{langDictionary.toURI().toURL()};
            messages = ResourceBundle.getBundle(BUNDLE_NAME, locale, new URLClassLoader(urls), UTF8ResourceBundleControl.get());

        } catch (SecurityException | MalformedURLException e) {
            plugin.getLogger().log(Level.WARNING, "Exception while reading lang bundle. Using internal", e);
        } catch (MissingResourceException ignored) { // how? missing write access?
            plugin.getLogger().log(Level.WARNING, "No translation file for " + UTF8ResourceBundleControl.get().toBundleName(BUNDLE_NAME, locale) + " found on disc. Using internal");
        }

        if (messages == null) { // fallback, since we are always trying to save defaults this never should happen
            try {
                messages = PropertyResourceBundle.getBundle(BUNDLE_NAME, locale, plugin.getClass().getClassLoader(), new UTF8ResourceBundleControl());
            } catch (MissingResourceException e) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't get Ressource bundle \"lang\" for locale \"" + locale.toLanguageTag() + "\". Messages WILL be broken!", e);
            }
        }
    }

    private @NotNull String getStringFromLang(@NotNull MessagePath path) {
        try {
            return messages.getString(path.getPath());
        } catch (MissingResourceException | ClassCastException e) {
            plugin.getLogger().log(Level.WARNING, "couldn't find path: \"" + path.getPath() + "\" in lang files using fallback.", e);
            return path.getPath();
        }
    }

    private void tempMute(@NotNull Player player) {
        int duration = tempMuteDuration;
        tempMute.add(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (tempMuted(player)) {
                tempMute.remove(player.getUniqueId());
            }
        }, duration);
    }

    private boolean tempMuted(@NotNull Player player) {
        return tempMute.contains(player.getUniqueId());
    }

    /**
     * prepend the message with the plugins prefix before sending it to the audience.
     */
    public void sendMessageWithoutPrefix(@NotNull Audience audience, @NotNull ComponentLike message) {
        audience.sendMessage(message);
    }

    /**
     * send a component from the lang file to the audience, prefixed with this plugins prefix.
     */
    public void sendLang(@NotNull Audience audience, @NotNull MessagePath path) {
        audience.sendMessage(Component.text().append(langCache.get(MessagePath.PLUGIN_PREFIX)).appendSpace().
                append(langCache.get(path)));
    }

    /**
     * send a component from the lang file to the audience, prefixed with this plugins prefix and applying the given tag resolver.
     * Note: might be slightly slower than {@link #sendLang(Audience, MessagePath)} since this can not use cache.
     */
    public void sendLang(@NotNull Audience audience, @NotNull MessagePath path, @NotNull TagResolver... resolvers) {
        audience.sendMessage(Component.text().append(langCache.get(MessagePath.PLUGIN_PREFIX)).appendSpace().append(
                MiniMessage.miniMessage().deserialize(getStringFromLang(path), resolvers)));
    }

    /**
     * get a component from lang file and apply the given tag resolver.
     * Note: might be slightly slower than {@link #getLang(MessagePath)} since this can not use cache.
     */
    public @NotNull Component getLang(@NotNull MessagePath path, @NotNull TagResolver resolver) {
        return MiniMessage.miniMessage().deserialize(getStringFromLang(path), resolver);
    }

    /**
     * get a component from lang file
     */
    public @NotNull Component getLang(@NotNull MessagePath path) {
        return langCache.get(path);
    }

    @EventHandler(ignoreCancelled = true)
    private void onMessage(@NotNull MessageEvent event) {
        if (event.getAudience() instanceof Player player) {
            if (tempMute.contains(player.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            if (tempMuteDuration > 0) {
                tempMute(player);
            }
            if (allowMessages && !muted.contains(player.getUniqueId())) {
                sendLang(event.getAudience(), event.getType(), event.getResolvers());
            }
        } else { // only players can be muted
            sendLang(event.getAudience(), event.getType(), event.getResolvers());
        }
    }

    void saveMuted() {
        List<String> ids = new ArrayList<>();
        if (muted.isEmpty()) {
            configManager.getData().set("Muted", new ArrayList<String>());
        }

        for (UUID id : muted) {
            ids.add(id.toString());
        }
        plugin.getServer().getLogger().info("Saving " + ids.size() + " Players that had events muted.");
        configManager.getData().set("Muted", ids);
    }

    void loadMuted() {
        List<String> muted = configManager.getData().getStringList("Muted");
        if (muted.isEmpty()) {
            return;
        }
        plugin.getServer().getLogger().info(muted.size() + " Players had events muted off. Adding Them...");
        for (String toggler : muted) {
            UUID id = UUID.fromString(toggler);
            this.muted.add(id);
        }
        configManager.getData().set("Muted", new ArrayList<String>());
    }

    public @NotNull List<@NotNull UUID> getMuted() {
        return muted;
    }
}

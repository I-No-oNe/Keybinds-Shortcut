package net.i_no_am.keybinds.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.i_no_am.keybinds.util.OsUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static net.i_no_am.keybinds.KeybindsShortcut.error;

public class Config {
    private final Path path;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static List<Keybind> keybinds = new ArrayList<>();

    public Config(Path path) {
        this.path = path;
        load();
        firstInit();
    }

    public void load() {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "{}");
            }

            String content = Files.readString(path);
            Keybind.ConfigData data = gson.fromJson(content, Keybind.ConfigData.class);
            keybinds = (data != null && data.getKeybinds() != null) ? data.getKeybinds() : new ArrayList<>();
        } catch (IOException e) {
            error("Failed to load keybinds: " + e.getMessage());
            keybinds = new ArrayList<>();
        }
    }

    public boolean save() {
        try {
            Keybind.ConfigData data = new Keybind.ConfigData();
            data.setKeybinds(keybinds);
            Files.writeString(path, gson.toJson(data));
            return true;
        } catch (IOException e) {
            error("Failed to save keybinds: " + e.getMessage());
            return false;
        }
    }

    public boolean add(Keybind keybind) {
        keybinds.add(keybind);
        return save();
    }

    public boolean update(List<String> keys, String application) {
        return keybinds.stream()
                .filter(k -> k.getKeys().equals(keys))
                .findFirst()
                .map(k -> {
                    k.setApp(application);
                    return save();
                })
                .orElse(false);
    }

    public boolean remove(List<String> keys) {
        return keybinds.removeIf(k -> k.getKeys().equals(keys)) && save();
    }

    public void firstInit() {
        if (keybinds.isEmpty()) {
            Keybind defaultKeybind = new Keybind(List.of("CTRL", "ALT", "I"), OsUtils.getDefaultApp());
            keybinds.add(defaultKeybind);
            save();
        }
    }

    public static List<Keybind> getKeybinds() {
        return Collections.unmodifiableList(keybinds);
    }

    public void reload() {
        load();
    }

    public void onChange(Runnable callback) {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            path.getParent().register(watcher, ENTRY_MODIFY);

            Thread.ofVirtual().start(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        WatchKey key = watcher.take();
                        key.pollEvents().stream()
                                .filter(event -> event.kind() == ENTRY_MODIFY)
                                .map(event -> (Path) event.context())
                                .filter(changed -> changed.equals(path.getFileName()))
                                .findAny()
                                .ifPresent(p -> callback.run());
                        if (!key.reset()) break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        } catch (IOException e) {
            error("Failed to setup file watcher: " + e.getMessage());
        }
    }
}

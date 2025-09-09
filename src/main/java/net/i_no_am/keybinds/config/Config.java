package net.i_no_am.keybinds.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.i_no_am.keybinds.KeybindsShortcut.error;

public class Config {

    private final Path path;
    private List<Keybind> keybinds;
    private final Gson gson;

    public Config(Path path) {
        this.path = path;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.keybinds = new ArrayList<>();
        load();
    }

    public void load() {
        var file = path.toFile();
        if (!file.exists()) createFrom(file);
        try (FileReader reader = new FileReader(file)) {
            Keybind.ConfigData data = gson.fromJson(reader, Keybind.ConfigData.class);
            if (data != null && data.getKeybinds() != null) {
                keybinds = data.getKeybinds();
            } else {
                keybinds = new ArrayList<>();
            }
        } catch (IOException e) {
            error("Failed to load keybinds: " + e.getMessage());
        }
    }

    private void createFrom(File file) {
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("{}");
        } catch (IOException e) {
            error("Failed to create keybinds file: " + e.getMessage());
        }
    }

    public boolean save() {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            Keybind.ConfigData data = new Keybind.ConfigData();
            data.setKeybinds(keybinds);
            gson.toJson(data, writer);
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
        for (Keybind k : keybinds) {
            if (k.getKeys().equals(keys)) {
                k.setApp(application);
                return save();
            }
        }
        return false;
    }

    public boolean remove(List<String> keys) {
        boolean removed = keybinds.removeIf(k -> k.getKeys().equals(keys));
        if (removed) return save();
        return false;
    }

    public List<Keybind> getKeybinds() {
        return Collections.unmodifiableList(keybinds);
    }

    public void reload() {
        load();
    }
}

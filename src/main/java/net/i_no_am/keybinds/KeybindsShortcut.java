package net.i_no_am.keybinds;

import net.i_no_am.keybinds.util.KeyListener;

import java.nio.file.Path;

public class KeybindsShortcut {

    private static KeybindsShortcut instance;

    public static void main(String[] args) {
        KeybindsShortcut app = getInstance();
        try {
            app.load(args);
            print("Successfully started KeybindsShortcut%n");
        } catch (Exception e) {
            error("Error: %s%n", e.getMessage());
        }
    }

    private void load(String[] args) {
        Path configPath = (args.length > 0) ? Path.of(args[0]) : Path.of("keybinds.json");
        KeyListener.register(configPath);
    }

    public static KeybindsShortcut getInstance() {
        if (instance == null) {
            instance = new KeybindsShortcut();
        }
        return instance;
    }

    public static void print(String message, Object... args) {
        System.out.printf(message, args);
    }

    public static void error(String message, Object... args) {
        System.err.printf(message, args);
        System.exit(1);
    }
}
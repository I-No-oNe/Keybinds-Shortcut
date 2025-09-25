package net.i_no_am.keybinds;

import net.i_no_am.keybinds.config.Config;
import net.i_no_am.keybinds.listener.KeyListener;

import java.nio.file.Paths;
public class KeybindsShortcut {

    public static void main(String[] args) {
        try {
            var path = Paths.get(System.getProperty("user.home"), "Documents", "shortcuts", "keys.json");
            KeyListener.register(path);
            print("""
                    Successfully started KeybindsShortcut%n
                     find config on %s
                     current values: %s""", path, Config.getKeybinds());
        } catch (Exception e) {
            error("Error: %s%n", e.getMessage());
        }
    }

    public static void print(String message, Object... args) {
        System.out.printf(message, args);
    }

    public static void error(String message, Object... args) {
        System.err.printf("[ERROR] " + message, args);
        System.exit(1);
    }
}
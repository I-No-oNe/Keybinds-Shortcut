package net.i_no_am.keybinds.util;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import net.i_no_am.keybinds.config.Config;
import net.i_no_am.keybinds.config.Keybind;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static net.i_no_am.keybinds.KeybindsShortcut.error;

public class KeyListener implements NativeKeyListener {

    private final Config config;
    private final Set<Integer> pressedKeys = new HashSet<>();

    private KeyListener(Path configPath) {
        this.config = new Config(configPath);
    }

    public static void register(Path configPath) {
        KeyListener listener = new KeyListener(configPath);
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(listener);
        } catch (Exception e) {
            error("Failed to register native hook: " + e.getMessage());
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        execute();
        config.reload();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    private void execute() {
        for (Keybind configKeys : config.getKeybinds()) {
            Set<Integer> requiredKeys = Arrays.stream(KeyBindUtils.getKeycodes(configKeys.getKeys().toString())).boxed().collect(Collectors.toSet());
            if (pressedKeys.containsAll(requiredKeys) && requiredKeys.containsAll(pressedKeys)) {
                if (!OsUtils.open(configKeys.getApp())) error("Failed to open %s%n", configKeys.getApp());
            }
        }
    }
}

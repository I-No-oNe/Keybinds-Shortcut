package net.i_no_am.keybinds.util;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.lang.reflect.Field;
import java.util.Arrays;

import static net.i_no_am.keybinds.KeybindsShortcut.error;

public class KeyBindUtils {

    public static int[] getKeycodes(String keys) {
        String[] parts = keys.replaceAll("[\\[\\]]", "").split(",");
        return Arrays.stream(parts)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .mapToInt(KeyBindUtils::mapSingleKey)
                .toArray();
    }

    private static int mapSingleKey(String key) {
        String normalized = key.toUpperCase().replaceAll("\"", "");

        normalized = switch (normalized) {
            case "CTRL" -> "CONTROL";
            case "ESC" -> "ESCAPE";
            case "WIN" -> "WINDOWS";
            default -> normalized;
        };

        try {
            // Try to find the field in NativeKeyEvent
            Field field = NativeKeyEvent.class.getField("VC_" + normalized);
            return field.getInt(null);
        } catch (NoSuchFieldException e) {
            error("Unknown key: " + key);
            throw new IllegalArgumentException("Unknown key: " + key, e);
        } catch (IllegalAccessException e) {
            error("Error accessing key field: " + e.getMessage());
            throw new RuntimeException("Error accessing key field", e);
        }
    }
}
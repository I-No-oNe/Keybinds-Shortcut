package net.i_no_am.keybinds.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static net.i_no_am.keybinds.KeybindsShortcut.error;

public class OsUtils {

    public enum OperatingSystem {
        WINDOWS, MACOS, LINUX, UNKNOWN
    }

    private static final OperatingSystem CURRENT_OS = detectOs();

    private static OperatingSystem detectOs() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return OperatingSystem.WINDOWS;
        } else if (os.contains("mac") || os.contains("darwin")) {
            return OperatingSystem.MACOS;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return OperatingSystem.LINUX;
        } else {
            return OperatingSystem.UNKNOWN;
        }
    }

    private static ProcessBuilder createProcessBuilder(String appName) {
        return switch (CURRENT_OS) {
            case WINDOWS -> new ProcessBuilder("cmd", "/c", "start", "", appName);
            case MACOS -> new ProcessBuilder("open", appName);
            case LINUX -> new ProcessBuilder("xdg-open", appName);
            default -> new ProcessBuilder(appName);
        };
    }

    private static String findApp(String appName) {
        return switch (CURRENT_OS) {
            case WINDOWS -> findWindow(appName);
            case MACOS -> findMac(appName);
            case LINUX -> findLinux(appName);
            default -> appName;
        };
    }


    private static String findWindow(String appName) {
        try {
            Process process = new ProcessBuilder(
                    "powershell", "-command",
                    "Get-StartApps | Where-Object {$_.Name -like '*" + appName + "*'} " +
                            "| Select-Object -First 1 -ExpandProperty AppID"
            ).start();

            List<String> output = readOutput(process);
            return output.isEmpty() ? appName : "shell:AppsFolder\\" + output.getFirst();
        } catch (IOException e) {
            return appName;
        }
    }

    private static String findMac(String appName) {
        try {
            // Use mdfind to locate .app bundles by name
            Process process = new ProcessBuilder(
                    "mdfind", "kMDItemKind==Application && kMDItemDisplayName=='" + appName + "'"
            ).start();

            List<String> output = readOutput(process);
            return output.isEmpty() ? appName : output.getFirst();
        } catch (IOException e) {
            return appName;
        }
    }

    private static String findLinux(String appName) {
        try {
            Process process = new ProcessBuilder("which", appName).start();
            List<String> output = readOutput(process);
            return output.isEmpty() ? appName : output.getFirst();
        } catch (IOException e) {
            return appName;
        }
    }

    private static List<String> readOutput(Process process) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null)
                lines.add(line.trim());
        }
        return lines;
    }

    public static boolean open(String appName) {
        try {
            createProcessBuilder(findApp(appName)).start();
            return true;
        } catch (IOException e) {
            error("Failed to open app: %s%n", appName);
            return false;
        }
    }
}

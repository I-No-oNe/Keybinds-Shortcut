package net.i_no_am.keybinds.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static net.i_no_am.keybinds.KeybindsShortcut.error;

public class OsUtils {

    public enum OperatingSystem {
        WINDOWS("win"), MACOS("mac", "darwin"), LINUX("nix", "nux", "aix"), UNKNOWN;

        private final String[] aliases;

        OperatingSystem(String... aliases) {
            this.aliases = aliases;
        }

        public static OperatingSystem detectOs() {
            String os = System.getProperty("os.name").toLowerCase();
            return Arrays.stream(values()).filter(osType -> Arrays.stream(osType.aliases).anyMatch(os::contains)).findFirst().orElse(UNKNOWN);
        }
    }

    private static final OperatingSystem CURRENT_OS = OperatingSystem.detectOs();

    private static String executeCommand(String... command) {
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();

            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return null;
            }

            return new String(process.getInputStream().readAllBytes()).trim();
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    private static String findApp(String appName) {
        String result = switch (CURRENT_OS) {
            case WINDOWS -> {
                String output = executeCommand("powershell", "-command", "Get-StartApps | Where-Object {$_.Name -like '*" + appName + "*'} " + "| Select-Object -First 1 -ExpandProperty AppID");
                yield output != null && !output.isEmpty() ? "shell:AppsFolder\\" + output : null;
            }
            case MACOS -> executeCommand("mdfind", "kMDItemKind==Application && kMDItemDisplayName=='" + appName + "'");
            case LINUX -> executeCommand("which", appName);
            default -> null;
        };

        return result != null && !result.isEmpty() ? result.lines().findFirst().orElse(appName) : appName;
    }

    private static ProcessBuilder createProcessBuilder(String appPath) {
        return switch (CURRENT_OS) {
            case WINDOWS -> new ProcessBuilder("cmd", "/c", "start", "", appPath);
            case MACOS -> new ProcessBuilder("open", appPath);
            case LINUX -> new ProcessBuilder("xdg-open", appPath);
            default -> new ProcessBuilder(appPath);
        };
    }

    public static String getDefaultApp() {
        return switch (CURRENT_OS) {
            case WINDOWS -> "explorer";
            case MACOS -> "Finder";
            case LINUX -> "xdg-open";
            default -> "notepad";
        };
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
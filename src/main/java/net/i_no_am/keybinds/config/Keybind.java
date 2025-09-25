package net.i_no_am.keybinds.config;

import java.util.List;

public class Keybind {

    private List<String> keys;
    private String app;

    public Keybind(List<String> keys, String app) {
        this.keys = keys;
        this.app = app;
    }

    public List<String> getKeys() {
        return keys;
    }

    public String getApp() {
        return app;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public static class ConfigData {

        private List<Keybind> keybinds;

        public List<Keybind> getKeybinds() {
            return keybinds;
        }

        public void setKeybinds(List<Keybind> keybinds) {
            this.keybinds = keybinds;
        }
    }

    @Override
    public String toString() {
        return "keys =" + keys +
                ", app='" + app + '\'' +
                '}';
    }
}
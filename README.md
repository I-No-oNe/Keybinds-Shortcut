# KeybindsShortcut ⌨

**KeybindsShortcut** is a lightweight, cross-platform Java app that lets you define **global keyboard shortcuts** to launch programs on **Windows, macOS, and Linux**.

---

## Quick Start 🚀

1. Download the latest `.jar` file  
2. Create a `keybinds.json` config file  
3. Run:

```sh
java -jar KeybindsShortcut.jar KeybindsShortcut
````

Optionally, specify a custom config path:

```sh
java -jar KeybindsShortcut.jar KeybindsShortcut PUT_YOUR_PATH_HERE.json
```

---

## Configuration ⚙️

Example `keybinds.json`:

```json
{
  "keybinds": [
    {
      "keys": ["CTRL", "ALT", "F"],
      "app": "firefox"
    }
  ]
}
```

* Supports **letters A-Z**, **numbers 0-9**, **F1-F12**, and modifiers: `CTRL`, `ALT`, `SHIFT`, `WIN`, `ESC`.
* Apps can be **common names**, **full paths**, or **shell commands**.
* On Windows, it attempts to find the closest installed app match automatically.

---

## Troubleshooting 🛠️

* **Failed to register native hook:** Run as admin/root; verify Java; macOS needs Accessibility permission.
* **Failed to open app:** Check app name/path, installation, and permissions.
* **Failed to load keybinds:** Check JSON file exists, syntax, and permissions.

---

# Thanks for using and don't forget to star the repo 🙏
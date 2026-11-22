# Steam Controller Trackpads for Controlify — Minecraft 26.3

**Early development / not a functional controller mod yet.**

This project is separate from the old crafting-lce-mod repository. It targets the **newer (2026) Steam Controller**, Minecraft Fabric 26.3 and coexistence with Controlify.

## Required control behavior

| Context | Left trackpad | Right trackpad |
|---|---|---|
| Gameplay | Nine-slot hotbar radial menu | Customizable action radial menu |
| Inventory, crafting, chat, pause, settings, containers | Scroll wheel | Mousepad/cursor |

Radial menus: touch to open, slide to highlight, release to activate. Configurable deadzone, touch/press semantics and bindings.

## Hardware approach

- Reuse *protocol knowledge* from [ddeverill/SteamlessController](https://github.com/ddeverill/SteamlessController) rather than copying its complete virtual-Xbox driver.
- Valve vendor ID `0x28DE`; wired controller PID `0x1302`; BLE PID `0x1303`; wireless puck PID `0x1304`. HID vendor usage page `0xFF00`. Input report IDs `0x42` (USB) and `0x45` (BLE), subject to hardware verification.
- Raw HID ownership may conflict with Steam Input. The integration must explicitly choose Steam Input handoff or direct HID ownership; **never silently disable lizard mode or send firmware-changing commands**.
- A native Windows HID reader/bridge may be needed. Minecraft Fabric cannot read vendor HID reports on its own. The protocol and trackpad fields should be validated against actual controller reports before implementing the bridge.
- Controlify should remain responsible for ordinary controller buttons and sticks. Our mod should own only the specialized trackpad UI, avoiding duplicate gamepad events.

## Milestones

1. Fabric mod scaffold and GitHub Actions build.
2. Verify compatibility with the installed Controlify version and Minecraft 26.3.
3. Read-only HID diagnostic probe (device discovery and decoded touch positions) based on the SteamlessController report layout.
4. Local bridge between HID input and Minecraft client, with connection status.
5. Radial menus, inventory scroll, cursor mapping and context switch.
6. Settings, controller testing, release JAR.

## Current status

The Java initializer is only a **context detection scaffold**. There is no native HID reader, no controller gestures, no radial UI and no usable gameplay integration yet. Do not mistake a successful build for finished functionality.

## Sources and licenses

- [SteamlessController](https://github.com/ddeverill/SteamlessController) — MIT licensed, useful for HID parsing.
- [sc2-research](https://github.com/CouchTurtle/sc2-research) — controller research and HID report analysis.
- [Controlify](https://github.com/isXander/Controlify) — separate controller mod; no bundled source.

No upstream source has been copied into this repository. MIT license applies to this project's original code; preserve attribution if upstream source is later reused.

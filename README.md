# Steam Controller Trackpads for Controlify

A client-side Fabric add-on for Minecraft Java 26.3 and Controlify 3.5.3+mc26.3. It uses the two touchpads exposed by Controlify's SDL3 driver for the 2026 Steam Controller.

| Context | Left trackpad | Right trackpad |
| --- | --- | --- |
| Gameplay | Nine-slot hotbar radial menu | Configurable Controlify action radial menu |
| Inventory, crafting, and other cursor-enabled screens | Swipe vertically to scroll | Slide to move the Controlify cursor |

Touch a pad to open its radial menu, slide toward a slot, and lift your finger to select it. Move back to the center before lifting to cancel. Controlify's normal virtual-mouse bindings handle clicks and dragging. Configure the action wheel, mouse sensitivity, and button activation threshold in Controlify.

## Setup

1. Use Minecraft Java 26.3 with Fabric Loader 0.19.3 or newer and Java 25.
2. Install Fabric API for 26.3, Controlify 3.5.3+mc26.3, and Controlify's YetAnotherConfigLib dependency.
3. Put the add-on JAR in the client mods folder. In Controlify, install SDL natives when prompted and select the physical Steam Controller. The controller must expose both pads through SDL. A virtual Xbox controller will typically hide them.

To build from source with Java 25 and Gradle 9.6.1, run `gradle build`. The build writes the JAR to `build/libs/`.

## Hardware validation

This implementation needs an in-game test with the physical controller. Start with a wired connection and check that each pad opens its own wheel, returning to center cancels, and changing screens or unplugging during a gesture does not select an action. In an inventory or scrollable screen, check cursor direction, vertical scrolling, and selection using Controlify's click bind. Wireless and Bluetooth behavior also need hardware verification.

The add-on reads Controlify's existing touch state. Device access runs through Controlify's bundled [SDL Steam Controller driver](https://github.com/libsdl-org/SDL/blob/release-3.4.12/src/joystick/hidapi/SDL_hidapi_steam_triton.c).

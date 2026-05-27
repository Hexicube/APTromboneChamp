# Trombone Champ setup guide

## Requirements

- [Archipelago 0.6.7+](https://github.com/ArchipelagoMW/Archipelago/releases), must be at least 0.6.7 as the APWorld relies on RuleBuilder.
- [The latest APWorld](https://github.com/Hexicube/APTromboneChamp/releases), unless hosting through Ionium which has v0.3.4.
- If manually submitting, the [manual client](https://github.com/Hexicube/APTromboneChamp/releases).
- If playing through the mod, it can be installed via R2modman or downloaded either through [github](https://github.com/Hexicube/APTromboneChampMod/releases) or [Thunderstore](https://thunderstore.io/c/trombone-champ/p/Hexi/Archipelago/).

## Setup (APWorld/settings)

1. If you're hosting locally (or playing solo), download the .apworld and install it via the option in the AP launcher or add it to the /custom_worlds folder.
2. Create a settings yaml via the option creator in the AP launcher, or by generating option templates and editing that.
- Make sure you pay attention to [settings limitations](https://github.com/Hexicube/APTromboneChamp), it's easy to make settings that the world will refuse to generate with.
3. Place your settings yaml in /players and generate your world, or send your settings yaml to the host and wait for them to do so.

## Modded client

### Setup (R2modman)

1. Install [R2modman](https://github.com/ebkr/r2modmanPlus/releases).
2. Open R2modman and select Trombone Champ, then create a new profile (or use the Default profile).
3. Select Online on the left, search for Archipelago, select it, press Download, and Download with dependencies.
4. Select Start modded on the left.
- If using R2modman and you want to see chat messages, close the game and enable the console: Config editor -> BepInEx.cfg -> Logging.Console = true
- You have to start the game modded once for this config to appear.
5. Once in-game, verify the AP mod loaded correctly; there should be a grey AP logo bottom-left.
6. Press F1 to open the connection window and enter the connection details.

### Usage

Pressing F1 once connected will display general goal information, showing what is required to goal and how many tracks are unlocked.

Pressing F2 once connected will open a hint window, showing existing unfound hints and allowing hinting your own items.

Once on the track select screen, three new collections can be found:
- Archipelago: Lists all tracks included based on your settings yaml.
- Archipelago Checks: Lists all tracks that are unlocked and have checks available.
- Archipelago Locked: Lists all tracks that are missing items. The track description will state what items are missing.

These collections will instead contain only Warm-Up if there are no matching tracks, such as when BKed.

Tip: You can leave the track selection screen on the AP Checks collection when BKed, when a new track is available the list will update and the new track will play its preview. This requires enabling track previews in the game's settings.

Only unlocked tracks will let you play them, the play button is removed for all other tracks.

Note: There's currently a bug that makes the button invisible or only showing the highlight when unlocking a track whilst having none unlocked, you can still play tracks by clicking where the button should be.

Checks and goaling will send automatically once reaching the score screen after playing a track.

## Manual client

Using the modded client is strongly recommended, the manual client lacks trap support and has lingering issues such as not notifying you if the connection drops, and is also no longer being updated.

### Setup

1. Install [java 17](https://adoptium.net/en-GB/temurin/releases?version=17) or higher.
2. Download the manual client jar from the [matching APWorld release](https://github.com/Hexicube/APTromboneChamp/releases).
3. Open the manual client, enter the connection details, and connect.

### Usage

Enter the connection details and press connect, messages will appear in the chat display once connected.

Once connected, a list of the following will appear:

- Rank Reduction items, if rating is below initial rating in your yaml.
- Progressive Difficulty items or Difficulty X items, if difficulty gating is enabled in your yaml.
- Hot Dog items, if a goal track is set and hot dogs are required in your yaml. The counter will show how many are required and the list will show how many there are in total.
- A list of tracks, sorted based on the sort order.

The sort order at the top is adjustable, drag the four options to change their priority (left is highest priority).

Double-clicking on an entry in the list will do one of the following:

- If it is not a track and there are unhinted items, a hint window will appear.
- If it is a locked track, a hint window will appear.
- If it is an unlocked track, a rating entry window will appear.
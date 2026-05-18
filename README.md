# Archipelago Trombone Champ APWorld and Manual Client

An APWorld and Manual Client for playing Trombone Champ on the Archipelago randomiser.

## Mod Client

A modded client can be found here: https://github.com/Hexicube/APTromboneChampMod/

## APWorld

Current features:

- Locations for both playing and beating tracks
- Items for unlocking tracks and difficulties
- Items for reducing the required rank to beat a track
- Options for filtering which tracks are used
- MacGuffin item to delay playing/goaling final track
- Traps to make it harder to beat tracks

YAML settings:
- goal:
  - 0: Goal is to beat the track set in goal_track, after collecting the required hot dogs
  - Above 0: Goal is to beat this number of tracks (auto-limits to number of tracks available)
- goal_track: Specifies which track is the goal track (ignored if goal is above 0)
  - Must be exactly the same as a short track name
  - If using the launcher option generator, use removed_tracks to help get the track name
- rating: Required rating to beat a track (C to S)
- rating_start: Initial rating required to beat a track (C to S)
  - Rank Reduction items are created to cover the difference between this and rating
- easy_track: Controls logic for beating tracks based on difficulty and rating settings
  - Tracks at max_diff always require all difficulty reductions
  - If set to 0, all tracks require this
  - If set above 0, every N difficulties below max_diff requires one fewer reductions
- fun_facts: How many Nothing filler items to replace with Fun Fact items  (auto-limits to number of fillers)
  - Fun Fact items say a random fun fact from the in-game loading screen
- hot_dogs: How many hot dogs are required to unlock the goal track (ignored if goal is above 0)
- extra_hot_dogs: How many extra hot dogs to add to the item pool (ignored if goal is above 0)
- track_gating: off/on/loose
  - Off: Tracks do not require an item to unlock
  - On: Tracks require an item, you start with the shortest min_diff track
  - Loose: Tracks require an item, you start with all min_diff tracks
  - Tracks may still require unlocking their difficulty and/or collecting hot dogs for the goal track
- difficulty_gating: off/on/progressive
  - Off: Difficulties do not require an item to unlock
  - On: Difficulties above min_diff require a specific item to unlock
  - Progressive: Difficulties above min_diff require a progressive item, granting difficulties from easiest to hardest
- min_diff: Minimum track difficulty to include (1-9)
  - You always start with tracks from this difficulty
    - If track gating is On, you start with the shortest min_diff track
    - If track gating is Loose, you start with all min_diff tracks
    - If difficulty gating is enabled, you start with min_diff difficulty
- max_diff: Maximum track difficulty to include (2-10)
  - Must be above min_diff (you always have at least two difficulties)
- unsafe: If enabled, includes tracks marked as stream-unsafe
  - Vanilla unsafe tracks: Hungarian Dance, Stars & Stripes, W. Post March
  - All DLC tracks are considered unsafe
- celeste: If enabled, includes the Celeste DLC tracks (requires unsafe)
- pizza_tower: If enabled, includes the Pizza Tower DLC tracks (requires unsafe)
- undertale_deltarune: If enabled, includes the Undertale/Deltarune DLC tracks (requires unsafe)
- removed_tracks: A list of tracks to remove (both items and locations)
  - All tracks must be exactly the same as short track names
  - Can include tracks that would be removed for other reasons (such as not enabling a DLC)
- trap_flip: How many Flip Controls traps to add, which temporarily invert your controls
- trap_deaf: How many Silence Track traps to add, which temporarily mute the backing track
- trap_mute: How many Silence Trombone traps to add, which temporarily mute your trombone
- trap_hide: How many Hide Notes traps to add, which temporarily hide both the note track and your pitch indicator
- trap_breath: How many No Breath traps to add, which instantly make you out of breath

Settings limitations:
- max_diff must be strictly greater than min_diff
- rating must not be lower than rating_start
- If goal is 0:
  - There must be a goal_track set, matching one of the available tracks based on other settings
  - If hot_dogs is 0, extra_hot_dogs must also be 0
- There must be a track at min_diff difficulty
  - *If both difficulty and track gating are enabled, there must be three tracks instead
- *There must be a track at max_diff difficulty
- *If difficulty gating is enabled, all difficulties (min_diff to max_diff) must have tracks
- *If difficulty gating is enabled, track gating cannot be set to On
- *If track gating is enabled, min_diff tracks must not require Rank Reduction items
- If track gating is On and a goal track is set, the goal track cannot be the shortest min_diff track (this is the starting track)
- There must be enough tracks to place all items
  - This includes trap items, generation will fail rather than reducing how many traps there are
  - This does NOT include Fun Fact items, which replace Nothing items
  - *There must also be space for two filler items or traps, as one track will be the final track played

Entries with an asterisk can be bypassed via enabling the hidden option bypass_options.
Enabling this may cause slow generation or failures, especially solo; use at your own risk.

## Manual Client

Current features:
- Connection entry with password and support for both secure and insecure websockets
- Track list that filters to tracks required based on settings, and hides beaten tracks
  - Draggable sorting order to help find tracks
  - Goal track (if used) always shows above all other tracks
  - Non-track items (Rank Reduction, Hot Dog, Difficulty unlocks) show above all tracks
  - Easy location entry, double-click and select the rating
  - Hint information per track, double-click a locked track to hint its location
    - Hints for/from other worlds require mouse-over to view item/location
  - Highlights based on status (grey for locked, red for in-logic checks, yellow for out-of-logic beating the track)
- Current rating requirement to beat a track, based on collected Rank Reduction items
- Current goal information (either what track is required to be beaten, or how many tracks have been beaten, depending on goal)
- Chat view
  - Item collections unrelated to your slot are hidden
  - Received items are yellow, unless it is a filler item
  - Hints for your locations/items are cyan

## TODO

- Add option to remove checks for playing tracks, provided track gating is off (if track gating is on there won't be enough locations)
- Add Deathlink support (failing to beat a track sends a death, receiving a death negates your next track entry)
- Add chat entry box
- Display internal errors in the chat view (such as losing connection)

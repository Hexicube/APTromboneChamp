# Archipelago Trombone Champ APWorld and Manual Client

An APWorld and Manual Client for playing Trombone Champ on the Archipelago randomiser.

## APWorld

Current features:

- Locations for both playing and beating tracks
- Items for unlocking tracks and difficulties
- Items for reducing the required rank to beat a track
- Options for filtering which tracks are used
- MacGuffin item to delay playing/goaling final track

YAML settings:
- goal:
  - 0: Goal is to beat the track set in goal_track, after collecting the required hot dogs
  - Above 0: Goal is to beat this number of tracks (auto-limits to number of tracks available)
- goal_track: Specifies which track is the goal track (ignored if goal is above 0)
  - Must be exactly the same as a short track name, or "none"
  - If using the launcher option generator, use removed_tracks to help get the track name
- rating: required rating to beat a track (C to S)
- rating_start: initial rating required to beat a track (C to S)
  - Rank Reduction items are created to cover the difference between this and rating
- easy_track: Controls logic for beating tracks based on difficulty and rating settings
  - Tracks at max_diff always require all difficulty reductions
  - If set to 0, all tracks require this
  - If set above 0, every N difficulties below max_diff requires one fewer reductions
- fun_facts: How many Nothing filler items to replace with Fun Fact items  (auto-limits to number of fillers)
  - Fun Fact items say a random fun fact from the in-game loading screen
- hot_dogs: How many hot dogs are required to unlock the goal track (ignored if goal is above 0)
- extra_hot_dogs: How many extra hot dogs to add to the item pool (ignored if goal is above 0 or hot_dogs is 0)
- track_gating: off/on/loose
  - Off: Tracks do not require an item to unlock (could still require a difficulty unlock or hot dogs)
  - On: Tracks require an item, you start with the shortest min_diff track (cannot be used with difficulty_gating)
  - Loose: Tracks require an item, you start with all min_diff tracks
- difficulty_gating: off/on/progressive
  - Off: Difficulties do not require an item to unlock
  - On: Difficulties above min_diff require a specific item to unlock
  - Progressive: Difficulties above min_diff require a progressive item, granting difficulties from easiest to hardest
- min_diff: Minimum track difficulty to include (1-9)
  - You always start with tracks from this difficulty if track/difficulty gating is enabled
- max_diff: Maximum track difficulty to include (2-10)
  - Must be above min_diff (you always have at least two difficulties)
- unsafe: If enabled, includes tracks marked as stream-unsafe
  - Vanilla unsafe tracks: Hungarian Dance, Stars & Stripes, W. Post March
  - All DLC tracks are considered unsafe
- celeste: If enabled, includes the Celeste DLC tracks (requires unsafe)
- pizza: If enabled, includes the Pizza Tower DLC tracks (requires unsafe)
- toby: If enabled, includes the Undertale/Deltarune DLC tracks (requires unsafe)
- removed_tracks: A list of tracks to remove (both items and locations)
  - All tracks must be exactly the same as short track names

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
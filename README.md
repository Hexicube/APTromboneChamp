# Archipelago Trombone Champ APWorld and Manual Client

An APWorld and Manual Client for playing Trombone Champ on the Archipelago randomiser.

## APWorld

Current features:

- Locations for both playing and beating tracks
- Items for unlocking tracks
- Items for reducing the required rank to beat a track

World options:
- goal: 0 for beating goal_track, or above 0 for beating that many tracks (auto-limits to number of tracks available)
- goal_track: When goal is 0, specifies which track is the goal track
- rating: required rating to beat a track (C to S)
- rating_start: initial rating required to beat a track (C to S), Rank Reduction items are created to cover the difference between this and rating
- easy_track: how many difficulty steps easier than max_diff per increase in required rank (rank A increases to S if this many difficulties below max_diff, B increases to S at double this, etc.), 0 to disable
- fun_facts: how many Nothing filler items to replace with Fun Fact items, which say one of the game's fun facts in chat (auto-limits to number of fillers)
- min_diff: minimum track difficulty to include (1-9), the starting track will be the shortest track of this difficulty
- max_diff: maximum track difficulty to include (2-10), must be higher than min_diff
- unsafe: if enabled, includes tracks marked as stream-unsafe (Hungarian Dance, Stars & Stripes, W. Post March)
- celeste: if enabled, includes the Celeste DLC tracks (requires unsafe)
- pizza: if enabled, includes the Pizza Tower DLC tracks (requires unsafe)
- toby: if enabled, includes the Undertale/Deltarune DLC tracks (requires unsafe)
- removed_tracks: a list of tracks to remove (both items and locations)

## Manual Client

Current features:
- Connection entry with password and support for both secure and insecure websockets
- Track list that filters to tracks required based on settings, and hides beaten tracks, sorted by DLC and alphabetically
  - Easy location entry, double-click and select the rating
  - Hint information per track, double-click a locked track to hint its location
    - Hints for other worlds require mouse-over to view location
  - Highlights based on status (grey for locked, red for in-logic checks, yellow for out-of-logic beating the track)
- Current rating requirement (shows as X(Y) where X is current requirement and Y is minimum requirement with Rank Reduction items)
- Current goal information (either what track is required to be beaten, or how many tracks have been beaten, depending on goal)
- Chat view
  - Item collections unrelated to your slot are hidden
  - Receiving an item highlights yellow
  - Hints for your locations are cyan

## TODO

- Add difficulty gating options (items for either increasing allowed difficulty, or for allowing specific difficulties)
- Add option to disable track gating, provided difficulty gating is enabled
- Add option to remove checks for playing tracks, provided track gating is off (if track gating is on there won't be enough locations)
- Add MacGuffin option (require hot dog items to unlock goal track)
- Add Deathlink support (failing to beat a track sends a death, receiving a death negates your next track entry)
- Add chat entry box
- Display internal errors in the chat view (such as losing connection)
- Track list sorting options (alphabetical, difficulty, availability, hinted)

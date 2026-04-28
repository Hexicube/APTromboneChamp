# Archipelago Trombone Champ APWorld and Manual Client

An APWorld and Manual Client for playing Trombone Champ on the Archipelago randomiser.

## APWorld

Current features:

- Locations for both playing and beating tracks
- Items for unlocking tracks
- Items for reducing the required rank to beat a track

World options:
- goal: 0 for longest track with difficulty matching max_diff, or above 0 for beating that many tracks (auto-limits to number of tracks available)
- rating: required rating to beat a track (C to S)
- rating_start: initial rating required to beat a track (C to S), Rank Reduction items are created to cover the difference between this and rating
- easy_track: how many difficulty steps easier than max_diff per increase in required rank
- fun_facts: how many Nothing filler items to replace with Fun Fact items, which say one of the game's fun facts in chat
- min_diff: minimum track difficulty to include (1-9), the starting track will be the shortest track of this difficulty
- max_diff: maximum track difficulty to include (2-10), auto-increases to be above min_diff
- unsafe: if enabled, includes tracks marked as stream-unsafe
- celeste: if enabled, includes the Celeste DLC tracks (requires unsafe)
- pizza: if enabled, includes the Pizza Tower DLC tracks (requires unsafe)
- toby: if enabled, includes the Undertale/Deltarune DLC tracks (requires unsafe)

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

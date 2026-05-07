from __future__ import annotations
from typing import TYPE_CHECKING
from BaseClasses import ItemClassification, LocationProgressType, Location
from . import items, tracks

if TYPE_CHECKING:
    from .world import APTromboneWorld

LOCATION_NAME_TO_ID = {}
for track in tracks.TRACK_LIST:
    LOCATION_NAME_TO_ID["Play: " + track["name"]] = track["id"]
    LOCATION_NAME_TO_ID["Beat: " + track["name"]] = track["id"] + 1000

class APTromboneLocation(Location):
    game = "Trombone Champ"

def create_all_locations(world: APTromboneWorld) -> None:
    goal_track = tracks.get_goal_track(world)
    for track in tracks.get_track_list(world):
        region = world.get_region("Track: " + track["name"])
        region.add_locations({
            f"Play: {track["name"]}": track["id"],
            f"Beat: {track["name"]}": track["id"] + 1000
        }, APTromboneLocation)
        if track == goal_track:
            world.get_location(f"Play: {track["name"]}").progress_type = LocationProgressType.EXCLUDED
            world.get_location(f"Beat: {track["name"]}").progress_type = LocationProgressType.EXCLUDED
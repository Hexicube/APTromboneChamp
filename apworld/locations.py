from __future__ import annotations
from typing import TYPE_CHECKING
from BaseClasses import ItemClassification, Location
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
    for track in tracks.get_track_list(world):
        region = world.get_region("Track: " + track["name"])
        region.add_locations({
            "Play: " + track["name"]: track["id"],
            "Beat: " + track["name"]: track["id"] + 1000
        }, APTromboneLocation)
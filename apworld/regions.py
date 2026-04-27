from __future__ import annotations
from typing import TYPE_CHECKING
from BaseClasses import Entrance, Region
from rule_builder.rules import Has
from . import tracks

if TYPE_CHECKING:
    from .world import APTromboneWorld

def create_and_connect_regions(world: APTromboneWorld) -> None:
    start_region = Region("Game", world.player, world.multiworld)
    world.multiworld.regions += [start_region]
    track_list = tracks.get_track_list(world)
    for track in track_list:
        end_region = Region("Track: " + track["name"], world.player, world.multiworld)
        world.multiworld.regions += [end_region]
        world.create_entrance(start_region, end_region, Has(track["name"]))
from __future__ import annotations
from typing import TYPE_CHECKING
from BaseClasses import Entrance, Region
from rule_builder.rules import Has, And
from . import tracks

if TYPE_CHECKING:
    from .world import APTromboneWorld

def create_and_connect_regions(world: APTromboneWorld) -> None:
    start_region = Region("Game", world.player, world.multiworld)
    diff_regions = {}
    world.multiworld.regions += [start_region]
    track_list = tracks.get_track_list(world)
    for diff in range(1, 11):
        diff_region = Region(f"Difficulty {diff}", world.player, world.multiworld)
        diff_regions[diff] = diff_region
        world.create_entrance(start_region, diff_region) # TODO: gating based on settings
    world.multiworld.regions += list(diff_regions.values())
    goal_track = tracks.get_goal_track(world)
    hot_dogs = world.options.hot_dogs.value
    for track in track_list:
        end_region = Region("Track: " + track["name"], world.player, world.multiworld)
        world.multiworld.regions += [end_region]
        if hot_dogs > 0 and track == goal_track:
            world.create_entrance(diff_regions[track["stars"]], end_region, And(Has(track["name"]), Has("Hot Dog", hot_dogs)))
        else:
            world.create_entrance(diff_regions[track["stars"]], end_region, Has(track["name"]))
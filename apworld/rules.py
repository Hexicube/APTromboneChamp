from __future__ import annotations
from typing import TYPE_CHECKING
from BaseClasses import CollectionState
from worlds.generic.Rules import add_rule, set_rule
from rule_builder.rules import Has, HasGroup, And
from . import tracks

if TYPE_CHECKING:
    from .world import APTromboneWorld

def set_all_rules(world: APTromboneWorld) -> None:
    hard_tracks = tracks.get_hardest_tracks(world)
    hardest_diff = hard_tracks[0]["stars"]
    
    rating_start = world.options.rating_start.value
    rating_end = world.options.rating.value
    rating_diff = rating_start - rating_end
    
    rating_gap = world.options.easy_track
    
    num_tracks_win = world.options.goal.value
    track_list = tracks.get_track_list(world)
    if num_tracks_win == 0:
        if rating_diff > 0:
            for track in track_list:
                track_diff = (hardest_diff - track["stars"])
                if rating_gap > 0: track_diff = track_diff // rating_gap
                required = rating_diff - track_diff
                if required > 0:
                    # needs reductions to be considered beatable
                    world.set_rule(world.get_location("Beat: " + track["name"]), Has("Rank Reduction", required))
        # set goal rule to specific track
        goal_track = tracks.get_goal_track(world)
        world.set_completion_rule(Has(goal_track["name"]) & Has("Rank Reduction", rating_diff))
    else:
        # make sure the goal is possible
        if num_tracks_win > len(track_list):
            print("NOTE: Goal tracks " + str(num_tracks_win) + " exceeds track count, reducing to " + str(len(track_list)))
            num_tracks_win = len(track_list)
            world.options.goal.value = num_tracks_win
        # assumes that all rank reductions are required
        world.set_completion_rule(HasGroup("Track", num_tracks_win) & Has("Rank Reduction", rating_diff))
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

    track_gating = world.options.track_gating.value
    diff_gating = world.options.difficulty_gating.value

    min_diff = world.options.min_diff.value
    max_diff = world.options.max_diff.value
    num_diff_items = max_diff - min_diff - 1

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
        hot_dogs = world.options.hot_dogs.value

        rule = Has("Hot Dog", hot_dogs) & Has("Rank Reduction", rating_diff)
        if track_gating: rule = rule & Has(goal_track["name"])
        if diff_gating == 1: rule = rule & Has(f"Difficulty {goal_track["stars"]}")
        if diff_gating == 2: rule = rule & Has("Progressive Difficulty", goal_track["stars"] - min_diff - 1)

        world.set_completion_rule(rule)
    else:
        # make sure the goal is possible
        if num_tracks_win > len(track_list):
            print("NOTE: Goal tracks " + str(num_tracks_win) + " exceeds track count, reducing to " + str(len(track_list)))
            num_tracks_win = len(track_list)
            world.options.goal.value = num_tracks_win
        # assumes that all rank reductions are required
        # NOTE: difficulty gating assumes we need all of them
        rule = Has("Rank Reduction", rating_diff)
        if track_gating: rule = rule & HasGroup("Track", num_tracks_win)
        if diff_gating == 1: rule = rule & HasGroup("Difficulty", num_diff_items)
        if diff_gating == 2: rule = rule & Has("Progressive Difficulty", num_diff_items)
        world.set_completion_rule(rule)
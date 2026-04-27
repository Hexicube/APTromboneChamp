from dataclasses import dataclass

from Options import Choice, OptionGroup, PerGameCommonOptions, Range, Toggle

# https://github.com/ArchipelagoMW/Archipelago/blob/main/docs/options%20api.md

# general gameplay options

class GoalTracks(Range):
    """
    How many tracks need to be beaten to goal.
    If set to 0, the goal is instead to beat the longest, hardest track.
    Will cap at the total number of tracks.
    """
    display_name = "Goal Tracks"
    range_start = 0
    range_end = 200
    default = 10

class GoalRating(Choice):
    """
    Required rating for a track to count as beaten.
    """
    display_name = "Goal Rating"
    option_C = 0
    option_B = 1
    option_A = 2
    option_S = 3
    default = 1

class InitialRating(Choice):
    """
    Initial required rating to beat tracks, Rating Reduction items are created to allow this to match Goal Rating.
    """
    display_name = "Initial Rating Required"
    option_C = 0
    option_B = 1
    option_A = 2
    option_S = 3
    default = 3

class EasyTrackStarGap(Range):
    """
    Easier tracks will require a higher rating to count as completed, based on the star difference.
    Set to 0 to disable. Values above 0 indicate how many stars below the hardest track needed per rating nudge.
    Example: Hardest is 10 stars, goal is B rank, setting is 2. A 6 star track requires S rank (4 below, nudges twice).
    """
    display_name = "Easy Track Difficulty Gap"
    range_start = 0
    range_end = 5
    default = 3

class FunFacts(Range):
    """
    How many filler items to replace with Fun Fact items, which send a random Trombone Champ fact out when collected.
    """
    display_name = "Fun Facts"
    range_start = 0
    range_end = 200
    default = 0

# track selection options

class MinDiff(Range):
    """
    Minimum star rating for a track to be included.
    The starting track will be the easiest track with this difficulty.
    """
    display_name = "Min Stars"
    range_start = 1
    range_end = 9
    default = 3

class MaxDiff(Range):
    """
    Maximum star rating for a track to be included.
    If Goal Tracks is 0, the goal track is the longest track with this difficulty.
    Must be higher than Min Stars, and will auto-increase as required.
    """
    display_name = "Max Stars"
    range_start = 2
    range_end = 10
    default = 7

class IncludeUnsafe(Toggle):
    """
    Include tracks that are marked as streamer unsafe.
    This is required for DLC tracks, which are all marked unsafe.
    """
    display_name = "Unsafe Tracks"

class IncludeCeleste(Toggle):
    """
    Include tracks from the Celeste DLC.
    Does nothing if Unsafe Tracks is not selected.
    """
    display_name = "Celeste DLC"

class IncludePizzaTower(Toggle):
    """
    Include tracks from the Pizza Tower DLC.
    Does nothing if Unsafe Tracks is not selected.
    """
    display_name = "Pizza Tower DLC"

class IncludeTobyFox(Toggle):
    """
    Include tracks from the Undertale+Deltarune DLC.
    Does nothing if Unsafe Tracks is not selected.
    """
    display_name = "Toby Fox DLC"

# items

@dataclass
class APTromboneOptions(PerGameCommonOptions):
    goal: GoalTracks
    rating: GoalRating
    rating_start: InitialRating
    easy_track: EasyTrackStarGap
    fun_facts: FunFacts
    
    min_diff: MinDiff
    max_diff: MaxDiff
    unsafe: IncludeUnsafe
    celeste: IncludeCeleste
    pizza: IncludePizzaTower
    toby: IncludeTobyFox

option_groups = [
    OptionGroup(
        "Gameplay Options",
        [GoalTracks, GoalRating, InitialRating, EasyTrackStarGap, FunFacts],
    ),
    OptionGroup(
        "Track Options",
        [MinDiff, MaxDiff, IncludeUnsafe, IncludeCeleste, IncludePizzaTower, IncludeTobyFox]
    )
]

option_presets = {
    "default": {
        "goal": 10,
        "rating": "A",
        "rating_start": "S",
        "easy_track": 3,
        
        "min_diff": 3,
        "max_diff": 7,
        "unsafe": False,
        "celeste": False,
        "pizza": False,
        "toby": False
    }
}

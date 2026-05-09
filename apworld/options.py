from dataclasses import dataclass

from Options import Choice, OptionGroup, PerGameCommonOptions, Range, Toggle, DefaultOnToggle, TextChoice, OptionList, Visibility
from . import tracks

# https://github.com/ArchipelagoMW/Archipelago/blob/main/docs/options%20api.md

# general gameplay options

class GoalTracks(Range):
    """
    How many tracks need to be beaten to goal.
    If set to 0, the goal is instead to beat the goal track specified.
    Will cap at the total number of tracks.
    """
    display_name = "Goal Tracks Count"
    range_start = 0
    range_end = 200
    default = 10

class GoalTrack(TextChoice):
    """
    Determines which track is the goal track.
    Must be set if goal tracks count is 0.
    Does nothing if goal tracks count is above 0.
    Must be set to a short track name that exists in the track list based on stars settings.
    """
    display_name = "Goal Track"
    option_none = -1
    #options = tracks.make_dict()
    default = -1

# TODO: GoalHotDogs(Range) - how many Hot Dog items are required before the goal track is playable
# requires goal track to be set to matter

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
    Must be larger than or equal to goal rating.
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

class HotDogs(Range):
    """
    How many Hot Dog items are required to unlock the goal track.
    Does nothing if goal tracks count is above 0.
    """
    display_name = "Hot Dogs"
    range_start = 0
    range_end = 20
    default = 10

class ExtraHotDogs(Range):
    """
    How many extra Hot Dog items to add.
    Does nothing if goal tracks count is above 0.
    """
    display_name = "Extra Hot Dogs"
    range_start = 0
    range_end = 20
    default = 0

class TrackGating(Choice):
    """
    Determines how track gating works:
    - Off: Tracks have no unlock requirement
    - On: Tracks require an item to unlock, you start with the shortest min_diff track
    - Loose: Tracks require an item to unlock, you start with all min_diff tracks
    Track gating cannot be set to On if difficulty gating is enabled.
    """
    display_name = "Track Gating"
    option_off = 0
    option_on = 1
    option_loose = 2
    default = 1

class DifficultyGating(Choice):
    """
    Determines how difficulty levels unlock:
    - Off: Difficulties have no unlock requirement
    - On: Difficulties require an item to unlock
    - Progressive: A Progressive Difficulty item unlocks difficulties in order from easiest to hardest
    """
    display_name = "Difficulty Gating"
    option_off = 0
    option_on = 1
    option_progressive = 2
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

class RemovedTracks(OptionList):
    """
    A list of tracks to remove after prior filtering.
    Must not include the goal track if specified.
    Must not include all tracks of a given star rating.
    Can include tracks that would be filtered out.
    """
    display_name = "Removed Tracks"
    valid_keys = list(map(lambda t: t["name"], tracks.TRACK_LIST))
    default = []

# TODO: DisablePlayLocations(Toggle) - when enabled, removes the "Play: X" locations (halves location count)

class OptionLimitBypass(Toggle):
    """
    If enabled, bypasses option restrictions that throw an OptionError but are not strictly mistakes.
    WARNING: This may result in generation failures, especially solo.
    """
    visibility = Visibility.none
    display_name = "Bypass Option Restrictions"

@dataclass
class APTromboneOptions(PerGameCommonOptions):
    goal: GoalTracks
    goal_track: GoalTrack
    rating: GoalRating
    rating_start: InitialRating
    easy_track: EasyTrackStarGap
    fun_facts: FunFacts
    hot_dogs: HotDogs
    extra_hot_dogs: ExtraHotDogs
    track_gating: TrackGating
    difficulty_gating: DifficultyGating
    
    min_diff: MinDiff
    max_diff: MaxDiff
    unsafe: IncludeUnsafe
    celeste: IncludeCeleste
    pizza_tower: IncludePizzaTower
    undertale_deltarune: IncludeTobyFox
    removed_tracks: RemovedTracks

    bypass_options: OptionLimitBypass

option_groups = [
    OptionGroup(
        "Goal Options",
        [GoalTracks, GoalTrack, GoalRating, InitialRating, HotDogs, ExtraHotDogs]
    ),
    OptionGroup(
        "Track List Options",
        [MinDiff, MaxDiff, EasyTrackStarGap, IncludeUnsafe, IncludeCeleste, IncludePizzaTower, IncludeTobyFox, RemovedTracks]
    ),
    OptionGroup(
        "Item Options",
        [FunFacts, TrackGating, DifficultyGating],
    )
]

option_presets = {
    "default": {
        "goal": 10,
        "goal_track": "none",
        "rating": "A",
        "rating_start": "S",
        "easy_track": 3,
        "hot_dogs": 10,
        "extra_hot_dogs": 0,

        "track_gating": "on",
        "difficulty_gating": "progressive",

        "min_diff": 3,
        "max_diff": 7,
        "unsafe": False,
        "celeste": False,
        "pizza_tower": False,
        "undertale_deltarune": False,
        "removed_tracks": []
    }
}

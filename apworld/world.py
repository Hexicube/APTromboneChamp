from collections.abc import Mapping
from typing import Any, TextIO
from worlds.AutoWorld import World
from Options import OptionError
from . import items, locations, options, regions, rules, web_world, tracks

class APTromboneWorld(World):
    """
    Trombone Champ is a very serious musical performance game about saving the world from the evil of the Bass and Treble clef with the assistance of monkeys.
    """
    game = "Trombone Champ"
    web = web_world.APTromboneWeb()
    options_dataclass = options.APTromboneOptions
    options: options.APTromboneOptions
    location_name_to_id = locations.LOCATION_NAME_TO_ID
    item_name_to_id = items.ITEM_NAME_TO_ID
    origin_region_name = "Game"
    
    item_name_groups = {
        "Track": tracks.make_group()
    }

    def generate_early(self) -> None:
        print(f"Chosen goal track: {self.options.goal_track.value}")
        # must have two difficulty levels at minimum
        min_diff = self.options.min_diff
        max_diff = self.options.max_diff
        if max_diff <= min_diff:
            raise OptionError(f"max_diff({max_diff}) must be greater than min_diff({min_diff})")
        # rating range has to make sense
        rating_start = self.options.rating_start.value
        rating_end = self.options.rating.value
        if rating_start < rating_end:
            raise OptionError(f"rating_start({rating_start}) is below rating({rating_end})")
        # convert goal track id to name
        goal_track_value = self.options.goal_track.value
        if isinstance(goal_track_value, int) or goal_track_value.isdigit():
            ID = int(goal_track_value)
            if ID == -1:
                goal_track_value = "none"
            else:
                match = False
                for track in tracks.TRACK_LIST:
                    if track["id"] == ID:
                        match = True
                        goal_track_value = track["name"]
                        break
                if not match:
                    raise OptionError(f"Unknown track ID {ID}")
        self.options.goal_track.value = goal_track_value
        # verify track list has no difficulties missing
        track_list = tracks.get_track_list(self)
        missing_diffs = list(range(min_diff, max_diff+1))
        for track in track_list:
            if track["stars"] in missing_diffs:
                missing_diffs.remove(track["stars"])
        if missing_diffs:
            raise OptionError(f"Excluded tracks made some difficulties empty: {missing_diffs}")
        # verify goal tracks count
        goal_track_count = self.options.goal.value
        if goal_track_count > len(track_list):
            # auto-reduce this
            goal_track_count = len(track_list)
            self.options.goal.value = goal_track_count
        # set hot dogs and extra hot dogs to 0 if goal tracks count is above 0
        if goal_track_count > 0:
            self.options.hot_dogs.value = 0
            self.options.extra_hot_dogs.value = 0
        else:
            if self.options.hot_dogs.value == 0 and self.options.extra_hot_dogs.value > 0:
                raise OptionError(f"Hot Dogs is 0 and Extra Hot Dogs ({self.options.extra_hot_dogs.value}) is above 0")
        # verify goal track
        goal_track = tracks.get_goal_track(self)
        if goal_track_count == 0:
            if not goal_track:
                raise OptionError(f"Goal tracks is 0 and no goal track is set")
            if not goal_track in track_list:
                raise OptionError(f"Goal track {goal_track["name"]} not in track list")
        # TODO: verify gating options when added (track+difficulty)
        # track gating: when enabled, tracks require an item (as it is right now)
        # difficulty gating:
        # - off: no gating, difficulties are ignored
        # - on: each difficulty requires its item before tracks with that difficulty are accessible
        # - progressive: difficulties require N progressive difficulty items instead, based on how many steps above min_diff they are

        # verify enough locations exist for expected items
        num_locs = len(track_list) * 2 # TODO: dont double when option to disable "Play: X" locs is enabled
        num_items = len(track_list) - 1 # TODO: dont add track gating items when option exists
        num_items += rating_start - rating_end
        num_items += self.options.hot_dogs.value
        num_items += self.options.extra_hot_dogs.value
        # TODO: add difficulty gating items when added
        if num_locs < num_items:
            raise OptionError(f"Settings are too restrictive, location count {num_locs} is below item count {num_items}")
        #raise OptionError(list(map(lambda t: t["name"], track_list)))

    def create_regions(self) -> None:
        regions.create_and_connect_regions(self)
        locations.create_all_locations(self)

    def set_rules(self) -> None:
        rules.set_all_rules(self)

    def create_items(self) -> None:
        items.create_all_items(self)

    def create_item(self, name: str) -> items.APTromboneItem:
        return items.create_item_with_correct_classification(self, name)

    def get_filler_item_name(self) -> str:
        return items.get_random_filler_item_name(self)

    def fill_slot_data(self) -> Mapping[str, Any]:
        return self.options.as_dict(
            "goal", "goal_track", "rating", "rating_start", "easy_track", "fun_facts", "hot_dogs", "extra_hot_dogs", "track_gating", "difficulty_gating",
            "min_diff", "max_diff", "unsafe", "celeste", "pizza", "toby", "removed_tracks"
        )
    
    def write_spoiler(self, spoiler_handle: TextIO) -> None:
        track_list = tracks.get_track_list(self)
        spoiler_handle.write(f"\nTrack List ({len(track_list)}):")
        len_sum = 0
        for track in track_list:
            dur = track["duration"]
            mins = dur // 60
            secs = dur % 60
            len_sum += dur
            spoiler_handle.write(f"\n{str(track["stars"]).rjust(2)}/10 | {mins}:{secs:02} | {track["fullname"]}")
        mins = len_sum // 60
        hrs = mins // 60
        mins = mins - (hrs * 60)
        secs = len_sum % 60
        spoiler_handle.write(f"\nTotal duration: {hrs}:{mins:02}:{secs:02}")
        easy_tracks = tracks.get_easiest_tracks(self)
        shortest = easy_tracks[0]
        for track in easy_tracks:
            if track["duration"] < shortest["duration"]: shortest = track
        spoiler_handle.write(f"\nStarting Track: {shortest["fullname"]}")
        
        num_tracks_win = self.options.goal.value
        if num_tracks_win == 0:
            goal_track = tracks.get_goal_track(self)
            spoiler_handle.write(f"\nGoal Track: {goal_track["fullname"]}")
        else:
            if num_tracks_win > len(track_list): num_tracks_win = len(track_list)
            spoiler_handle.write(f"\nGoal Tracks Count: ${num_tracks_win}")
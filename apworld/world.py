from collections.abc import Mapping
from typing import Any, TextIO
from worlds.AutoWorld import World
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
            "goal", "rating", "rating_start", "easy_track", "fun_facts",
            "min_diff", "max_diff", "unsafe", "celeste", "pizza", "toby"
        )
    
    def write_spoiler(self, spoiler_handle: TextIO) -> None:
        track_list = tracks.get_track_list(self)
        spoiler_handle.write("\nTrack List (" + str(len(track_list)) + "):")
        len_sum = 0
        for track in track_list:
            name = track["name"]
            diff = track["stars"]
            mins = track["duration"] // 60
            secs = track["duration"] % 60
            len_sum = len_sum + track["duration"]
            spoiler_handle.write("\n" + str(diff).rjust(2) + "/10 | " + str(mins) + ":" + f"{secs:02}" + " | " + track["fullname"])
        mins = len_sum // 60
        hrs = mins // 60
        mins = mins - (hrs * 60)
        secs = len_sum % 60
        spoiler_handle.write("\nTotal duration: " + str(hrs) + ":" + f"{mins:02}" + ":" + f"{secs:02}")
        easy_tracks = tracks.get_easiest_tracks(self, 1)
        shortest = easy_tracks[0]
        for track in easy_tracks:
            if track["duration"] < shortest["duration"]: shortest = track
        spoiler_handle.write("\nStarting Track: " + shortest["fullname"])
        
        num_tracks_win = self.options.goal.value
        if num_tracks_win == 0:
            hard_tracks = tracks.get_hardest_tracks(self)
            longest = hard_tracks[0]
            for track in hard_tracks:
                if track["duration"] > longest["duration"]: longest = track
            spoiler_handle.write("\nGoal Track: " + longest["fullname"])
        else:
            if num_tracks_win > len(track_list): num_tracks_win = len(track_list)
            spoiler_handle.write("\nGoal Track Count: " + str(num_tracks_win))
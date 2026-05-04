from __future__ import annotations
from typing import TYPE_CHECKING
from BaseClasses import Item, ItemClassification
from . import tracks

if TYPE_CHECKING:
    from .world import APTromboneWorld

ITEM_NAME_TO_ID = {
    "Rank Reduction": 1001,
    "Nothing": 1002,
    "Fun Fact": 1003,
    "Hot Dog": 1004,
    "Progressive Difficulty": 1011,
    "Difficulty 2": 1012,
    "Difficulty 3": 1013,
    "Difficulty 4": 1014,
    "Difficulty 5": 1015,
    "Difficulty 6": 1016,
    "Difficulty 7": 1017,
    "Difficulty 8": 1018,
    "Difficulty 9": 1019,
    "Difficulty 10": 1020,
}

ITEM_CLASS = {
    "Rank Reduction": ItemClassification.progression | ItemClassification.useful,
    "Nothing": ItemClassification.filler,
    "Fun Fact": ItemClassification.filler,
    "Hot Dog": ItemClassification.progression | ItemClassification.useful,
    "Progressive Difficulty": ItemClassification.progression | ItemClassification.useful,
    "Difficulty 2": ItemClassification.progression | ItemClassification.useful,
    "Difficulty 3": ItemClassification.progression | ItemClassification.useful,
    "Difficulty 4": ItemClassification.progression | ItemClassification.useful,
    "Difficulty 5": ItemClassification.progression | ItemClassification.useful,
    "Difficulty 6": ItemClassification.progression | ItemClassification.useful,
    "Difficulty 7": ItemClassification.progression | ItemClassification.useful,
    "Difficulty 8": ItemClassification.progression | ItemClassification.useful,
    "Difficulty 9": ItemClassification.progression | ItemClassification.useful,
    "Difficulty 10": ItemClassification.progression | ItemClassification.useful,
}

for track in tracks.TRACK_LIST:
    ITEM_NAME_TO_ID[track["name"]] = track["id"]

class APTromboneItem(Item):
    game = "Trombone Champ"

def get_random_filler_item_name(world: APTromboneWorld) -> str:
    return "Nothing"

def create_item_with_correct_classification(world: APTromboneWorld, name: str) -> APTromboneItem:
    classification = ItemClassification.progression
    if name in ITEM_CLASS:
        classification = ITEM_CLASS[name]
    item = APTromboneItem(name, classification, ITEM_NAME_TO_ID[name], world.player)
    return item

def create_all_items(world: APTromboneWorld) -> None:
    # find starting track
    
    easy_tracks = tracks.get_easiest_tracks(world)
    shortest = easy_tracks[0]
    for track in easy_tracks:
        if track["duration"] < shortest["duration"]: shortest = track
    
    number_of_items = 0
    for track in tracks.get_track_list(world):
        if track != shortest:
            number_of_items += 1
            world.multiworld.itempool.append(world.create_item(track["name"]))
    world.multiworld.push_precollected(world.create_item(shortest["name"]))
    
    rating_start = world.options.rating_start.value
    rating_end = world.options.rating.value
    if rating_start < rating_end:
        print("NOTE: Rating start " + str(rating_start) + " is below end, increasing to " + str(rating_end))
        rating_start = rating_end
        world.options.rating_start = world.options.rating
    rating_diff = rating_start - rating_end
    for i in range(rating_diff):
        number_of_items += 1
        world.multiworld.itempool.append(world.create_item("Rank Reduction"))

    for i in range(world.options.hot_dogs.value + world.options.extra_hot_dogs.value):
        number_of_items += 1
        world.multiworld.itempool.append(world.create_item("Hot Dog"))
    
    number_of_unfilled_locations = len(world.multiworld.get_unfilled_locations(world.player))
    num_facts = world.options.fun_facts.value
    if num_facts > number_of_unfilled_locations: num_facts = number_of_unfilled_locations
    for i in range(num_facts):
        number_of_unfilled_locations -= 1
        world.multiworld.itempool.append(world.create_item("Fun Fact"))
    
    needed_number_of_filler_items = number_of_unfilled_locations - number_of_items
    world.multiworld.itempool += [world.create_filler() for _ in range(needed_number_of_filler_items)]
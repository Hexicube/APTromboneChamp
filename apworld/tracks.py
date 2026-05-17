from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from .world import APTromboneWorld

def track(ID, shortName, longName, difficulty, length, streamUnsafe = False, DLC = "Base"):
    return {
        "id": ID,
        "name": shortName,
        "fullname": longName,
        "stars": difficulty,
        "duration": length,
        "unsafe": streamUnsafe,
        "DLC": DLC
    }

TRACK_LIST = [
    # base game
    track(  1, "Are U Ready", "Are U Ready 4 Thiz?", 7, 1 * 60 + 52),
    track(  2, "Arirang", "Arirang", 3, 2 * 60 + 8),
    track(  3, "Auld Lang Syne", "Auld Lang Syne (Champ Mix)", 7, 2 * 60 + 4),
    track(  4, "Baboons!", "Baboons!", 6, 1 * 60 + 36),
    track(  5, "Bald Mountain", "Night on Bald Mountain", 7, 2 * 60 + 46),
    track(  6, "Ball Game", "Take Me Out to the Ball Game", 3, 1 * 60 + 26),
    track(  7, "Barber of Seville", "The Barber of Seville Overture", 7, 2 * 60 + 21),
    track(  8, "Beethoven's Fifth", "Beethoven's Fifth Symphony", 7, 1 * 60 + 41),
    track(  9, "Blue Danube", "The Blue Danube Waltz", 5, 2 * 60 + 18),
    track( 10, "Bumblebee", "Flight of the Bumblebee", 9, 2 * 60 + 0),
    track( 11, "Carol of the Bells", "Carol of the Bells", 7, 2 * 60 + 0),
    track( 12, "Chop Waltz", "The Celebrated Chop Waltz", 6, 1 * 60 + 20),
    track( 13, "Commander Tokyo", "Commander Tokyo, The Dancing Robot", 9, 1 * 60 + 36),
    track( 14, "Danny Boy", "Danny Boy", 3, 1 * 60 + 58),
    track( 15, "Danse Macabre", "Danse Macabre", 8, 2 * 60 + 52),
    track( 16, "Eine (CHAMP MIX)", "Eine Kleine (Champ Mix)", 10, 1 * 60 + 41),
    track( 17, "Eine Kleine", "Eine Kleine Nachtmusik (Trap Mix)", 5, 1 * 60 + 55),
    track( 18, "Entertainer", "The Entertainer", 7, 1 * 60 + 17),
    track( 19, "Four Seasons (Summer)", "The Four Seasons (Summer)", 9, 3 * 60 + 5),
    track( 20, "Funiculi Funicula", "Funiculi, Funicula", 6, 2 * 60 + 1),
    track( 21, "Gladiators", "Entry of the Gladiators", 8, 1 * 60 + 23),
    track( 22, "God Save The King", "God Save The King", 2, 1 * 60 + 7),
    track( 23, "Gymnopédie", "Gymnopédie No. 1", 5, 2 * 60 + 35),
    track( 24, "Habanera", "Habanera (From Carmen)", 5, 3 * 60 + 16),
    track( 25, "Happy Birthday", "Happy Birthday to You (Ska Mix)", 5, 1 * 60 + 24),
    track( 26, "Hava Nagila", "Hava Nagila", 5, 2 * 60 + 3),
    track( 27, "Hello! Ma Baby", "Hello! Ma Baby", 6, 2 * 60 + 51),
    track( 28, "Hino Do Brasil", "Hino Nacional Brasileiro", 7, 1 * 60 + 52),
    track( 29, "Hungarian Dance", "Hungarian Dance No. 5", 7, 2 * 60 + 48, True),
    track( 30, "Hungarian Rhapsody", "Hungarian Rhapsody No. 2", 9, 4 * 60 + 14),
    track( 31, "Jarabe Tapatío", "Jarabe Tapatío (Mexican Hat Dance)", 9, 1 * 60 + 21),
    track( 32, "Jasmine Flower", "Jasmine Flower (Mo Li Hua)", 3, 1 * 60 + 50),
    track( 33, "Jingle Bells", "Jingle Bells (Jazz Mix)", 5, 1 * 60 + 50),
    track( 34, "Korobeiniki", "Korobeiniki", 7, 3 * 60 + 2),
    track( 35, "Long-Tail Limbo", "Long-Tail Limbo", 5, 1 * 60 + 29),
    track( 36, "Mars", "Mars, The Bringer of War", 4, 2 * 60 + 20),
    track( 37, "Marseillaise", "La Marseillaise", 7, 1 * 60 + 20),
    track( 38, "Martian Killbots", "Martial Killbots!!!", 3, 2 * 60 + 0),
    track( 39, "Merry Gentlemen", "God Rest ye Merry, Gentlemen", 7, 1 * 60 + 55),
    track( 40, "Mountain King", "In The Hall of The Mountain King", 8, 2 * 60 + 30),
    track( 41, "O Canada", "O Canada", 3, 1 * 60 + 8),
    track( 42, "O Christmas Tree", "O Christmas Tree", 4, 2 * 60 + 25),
    track( 43, "Ode to Joy", "Ode to Joy", 2, 1 * 60 + 43),
    track( 44, "Oh Chanukah!", "Chanukah Oh Chanukah!", 8, 2 * 60 + 0),
    track( 45, "Old Gray Mare", "Old Gray Mare", 5, 1 * 60 + 17),
    track( 46, "Old MacDonald", "Old MacDonald", 7, 1 * 60 + 45),
    track( 47, "Rhapsody in Blue", "Rhapsody in Blue", 8, 4 * 60 + 18),
    track( 48, "Rising Sun Blues", "The House of The Rising Sun", 4, 2 * 60 + 15),
    track( 49, "Rosamunde", "Rosamunde (Beer Barrel Polka)", 6, 1 * 60 + 41),
    track( 50, "Round the Mountain", "She'll Be Coming 'Round the Mountain", 7, 1 * 60 + 54),
    track( 51, "Sailor's Hornpipe", "The Sailor's Hornpipe", 10, 2 * 60 + 0),
    track( 52, "Sakura", "Sakura Sakura", 2, 1 * 60 + 21),
    track( 53, "Silent Night", "Max Tundra's Silent Night", 4, 2 * 60 + 16),
    track( 54, "SkaBIRD", "SkaBIRD", 6, 2 * 60 + 47),
    track( 55, "Skeleton Rag", "The Skeleton Rag (Remix)", 6, 3 * 60 + 12),
    track( 56, "Skip To My Lou", "Skip To My Lou", 5, 1 * 60 + 13),
    track( 57, "St James Trombonery", "St. James Trombonery Blues", 4, 2 * 60 + 13),
    track( 58, "Stars & Stripes", "Stars and Stripes Forever", 8, 2 * 60 + 34, True),
    track( 59, "Star-Spangled", "The Star-Spangled Banner", 5, 1 * 60 + 12),
    track( 60, "Sugar Plum Fairy", "Dance of The Sugar Plum Fairy", 5, 1 * 60 + 38),
    track( 61, "T. Champ Medley", "Trombone Champ Medley", 7, 4 * 60 + 21),
    track( 62, "Taps", "Taps", 3, 39),
    track( 63, "The Can-Can", "The Can-Can", 8, 2 * 60 + 19),
    track( 64, "The Ritz", "Puttin On The Ritz", 7, 2 * 60 + 0),
    track( 65, "The Riverside", "Down By The Riverside", 5, 2 * 60 + 13),
    track( 66, "The Saints", "When The Saints Go Marching In", 5, 1 * 60 + 55),
    track( 67, "Toccata & Fugue", "Toccata & Fugue in D Minor", 8, 3 * 60 + 5),
    track( 68, "Trombone Fuerte", "Trombone Fuerte", 9, 1 * 60 + 25),
    track( 69, "Trombone Skyze", "Trombone Skyze", 4, 2 * 60 + 24),
    track( 70, "Trombone Skyze (Nasty)", "Trombone Skyze (Nasty Mix)", 8, 2 * 60 + 14),
    track( 71, "W. Post March", "The Washington Post March", 7, 2 * 60 + 34, True),
    track( 72, "Warm-Up", "Warm-Up", 1, 1 * 60 + 12),
    track( 73, "William Tell", "William Tell Overture", 8, 1 * 60 + 52),
    track( 74, "Zarathustra", "Also Sprach Zarathustra", 1, 1 * 60 + 26),
    # celeste
    track(201, "Confronting Myself", "Confronting Myself", 7, 4 * 60 + 11, True, "Celeste"),
    track(202, "First Steps", "First Steps", 7, 3 * 60 + 34, True, "Celeste"),
    track(203, "Heart of the Mountain", "Heart of the Mountain", 7, 3 * 60 + 36, True, "Celeste"),
    track(204, "Madeline and Theo", "Madeline and Theo", 6, 3 * 60 + 13, True, "Celeste"),
    track(205, "Reach for the Summit", "Reach for the Summit", 6, 4 * 60, True, "Celeste"),
    track(206, "Reflection", "Reflection", 6, 3 * 60 + 11, True, "Celeste"),
    track(207, "Resurrections", "Resurrections", 6, 4 * 60 + 18, True, "Celeste"),
    track(208, "Scattered and Lost", "Scattered and Lost", 8, 4 * 60 + 34, True, "Celeste"),
    track(209, "Spirit of Hospitality", "Spirit of Hospitality", 6, 1 * 60 + 48, True, "Celeste"),
    track(210, "Starjump", "Starjump", 8, 2 * 60 + 10, True, "Celeste"),
    # pizza tower
    track(251, "Bye Bye There!", "Bye Bye There!", 7, 4 * 60, True, "Pizza Tower"),
    track(252, "Cold Spaghetti", "Cold Spaghetti", 8, 2 * 60 + 52, True, "Pizza Tower"),
    track(253, "Death I Deservioli", "The Death That I Deservioli", 8, 2 * 60 + 56, True, "Pizza Tower"),
    track(254, "ET Wahwahs", "Extraterrestrial Wahwahs", 8, 3 * 60 + 32, True, "Pizza Tower"),
    track(255, "Funiculi Holiday", "Funiculi Holiday", 7, 1 * 60 + 19, True, "Pizza Tower"),
    track(256, "Good Eatin'", "Good Eatin'", 8, 3 * 60 + 26, True, "Pizza Tower"),
    track(257, "It's Pizza Time!", "It's Pizza Time!", 8, 3 * 60 + 40, True, "Pizza Tower"),
    track(258, "Kid's Menu", "What On the Kid's Menu?", 9, 3 * 60 + 58, True, "Pizza Tower"),
    track(259, "Oregano Mirage", "Oregano Mirage", 9, 2 * 60 + 29, True, "Pizza Tower"),
    track(260, "Pizza Deluxe", "Pizza Deluxe", 7, 2 * 60 + 16, True, "Pizza Tower"),
    track(261, "Pumpin' Hot Stuff", "Pumpin' Hot Stuff", 7, 2 * 60 + 19, True, "Pizza Tower"),
    track(262, "Put On A Show!", "Put On A Show!", 7, 2 * 60 + 43, True, "Pizza Tower"),
    track(263, "Unexpectancy", "Unexpectancy (Part 2 of 3)", 9, 2 * 60 + 59, True, "Pizza Tower"),
    track(264, "Yeehaw", "Yeehaw Deliveryboy", 8, 2 * 60 + 7, True, "Pizza Tower"),
    # undertale / deltarune
    track(301, "ASGORE", "ASGORE", 8, 2 * 60 + 36, True, "UTDR"),
    track(302, "BIG SHOT", "BIG SHOT", 7, 2 * 60 + 24, True, "UTDR"),
    track(303, "Black Knife", "Black Knife", 7, 1 * 60 + 59, True, "UTDR"),
    track(304, "Bonetrousle", "Bonetrousle", 8, 60, True, "UTDR"),
    track(305, "CYBER'S WORLD?", "A CYBER'S WORLD?", 7, 2 * 60 + 48, True, "UTDR"),
    track(306, "Dark Sanctuary", "Dark Sanctuary + Neverending Night", 7, 3 * 60 + 6, True, "UTDR"),
    track(307, "Dummy!", "Dummy!", 9, 2 * 60 + 29, True, "UTDR"),
    track(308, "GUARDIAN", "GUARDIAN", 8, 3 * 60 + 30, True, "UTDR"),
    track(309, "Hopes and Dreams", "Hopes and Dreams", 8, 3 * 60 + 3, True, "UTDR"),
    track(310, "It's TV Time!", "It's TV Time!", 8, 2 * 60 + 48, True, "UTDR"),
    track(311, "Killer Queen", "Attack of the Killer Queen", 8, 2 * 60 + 3, True, "UTDR"),
    track(312, "Megalovania", "Megalovania", 9, 2 * 60 + 39, True, "UTDR"),
    track(313, "Metal Crusher", "Metal Crusher", 9, 1 * 60 + 5, True, "UTDR"),
    track(314, "Pandora Palace", "Pandora Palace", 7, 1 * 60 + 40, True, "UTDR"),
    track(315, "Rude Buster", "Rude Buster", 9, 1 * 60 + 19, True, "UTDR"),
    track(316, "Scarlet Forest", "Scarlet Forest", 6, 2 * 60 + 12, True, "UTDR"),
    track(317, "SWORD", "SWORD", 7, 1 * 60 + 50, True, "UTDR"),
    track(318, "Third Sanctuary", "The Third Sanctuary", 9, 4 * 60 + 8, True, "UTDR"),
    track(319, "True Hero", "Battle Against a True Hero", 8, 2 * 60 + 36, True, "UTDR"),
    track(320, "TV World", "TV World", 7, 2 * 60 + 12, True, "UTDR"),
    track(321, "World Revolving", "The World Revolving", 8, 1 * 60 + 44, True, "UTDR"),
]

def get_track_list(world):
    min_diff = world.options.min_diff.value
    max_diff = world.options.max_diff.value
    excludes = world.options.removed_tracks.value
    
    unsafe = world.options.unsafe.value
    celeste = world.options.celeste.value
    pizza_tower = world.options.pizza_tower.value
    utdr = world.options.undertale_deltarune.value
    tracks = []
    for track in TRACK_LIST:
        if track["stars"] < min_diff: continue
        if track["stars"] > max_diff: continue
        if track["unsafe"] and not unsafe: continue
        if track["DLC"] == "Celeste" and not celeste: continue
        if track["DLC"] == "Pizza Tower" and not pizza_tower: continue
        if track["DLC"] == "UTDR" and not utdr: continue
        if track["name"] in excludes: continue
        tracks.append(track)
    return tracks

def get_hardest_tracks(world):
    max_diff = world.options.max_diff
    track_list = get_track_list(world)
    new_list = []
    for track in track_list:
        if track["stars"] == max_diff:
            new_list.append(track)
    return new_list

def get_easiest_tracks(world):
    min_diff = world.options.min_diff
    track_list = get_track_list(world)
    new_list = []
    for track in track_list:
        if track["stars"] == min_diff:
            new_list.append(track)
    return new_list

def get_goal_track(world):
    goal_track = world.options.goal_track.value
    if goal_track == "none":
        return None
    for track in TRACK_LIST:
        if track["name"] == goal_track:
            return track
    raise OptionError(f"Unknown goal track {goal_track}")

def make_group():
    tracks = []
    for track in TRACK_LIST:
        tracks.append(track["name"])
    return tracks
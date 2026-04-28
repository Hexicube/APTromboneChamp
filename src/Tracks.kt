data class Track(
    val ID: Long,
    val name: String, val fullName: String,
    val diff: Int, val len: Int,
    val unsafe: Boolean = false, val DLC: String = "Base"
) {
    companion object {
        val TRACK_LIST = arrayOf(
            // base game
            Track(  1, "Are U Ready", "Are U Ready 4 Thiz?", 7, 1 * 60 + 52),
            Track(  2, "Arirang", "Arirang", 3, 2 * 60 + 8),
            Track(  3, "Auld Lang Syne", "Auld Lang Syne (Champ Mix)", 7, 2 * 60 + 4),
            Track(  4, "Baboons!", "Baboons!", 6, 1 * 60 + 36),
            Track(  5, "Bald Mountain", "Night on Bald Mountain", 7, 2 * 60 + 46),
            Track(  6, "Ball Game", "Take Me Out to the Ball Game", 3, 1 * 60 + 26),
            Track(  7, "Barber of Seville", "The Barber of Seville Overture", 7, 2 * 60 + 21),
            Track(  8, "Beethoven's Fifth", "Beethoven's Fifth Symphony", 7, 1 * 60 + 41),
            Track(  9, "Blue Danube", "The Blue Danube Waltz", 5, 2 * 60 + 18),
            Track( 10, "Bumblebee", "Flight of the Bumblebee", 9, 2 * 60 + 0),
            Track( 11, "Carol of the Bells", "Carol of the Bells", 7, 2 * 60 + 0),
            Track( 12, "Chop Waltz", "The Celebrated Chop Waltz", 6, 1 * 60 + 20),
            Track( 13, "Commander Tokyo", "Commander Tokyo, The Dancing Robot", 9, 1 * 60 + 36),
            Track( 14, "Danny Boy", "Danny Boy", 3, 1 * 60 + 58),
            Track( 15, "Danse Macabre", "Danse Macabre", 8, 2 * 60 + 52),
            Track( 16, "Eine (Champ Mix)", "Eine Kleine (Champ Mix)", 10, 1 * 60 + 41),
            Track( 17, "Eine Kleine", "Eine Kleine Nachtmusik (Trap Mix)", 5, 1 * 60 + 55),
            Track( 18, "Entertainer", "The Entertainer", 7, 1 * 60 + 17),
            Track( 19, "Four Seasons (Summer)", "The Four Seasons (Summer)", 9, 3 * 60 + 5),
            Track( 20, "Funiculi Funicula", "Funiculi, Funicula", 6, 2 * 60 + 1),
            Track( 21, "Gladiators", "Entry of the Gladiators", 8, 1 * 60 + 23),
            Track( 22, "God Save The King", "God Save The King", 2, 1 * 60 + 7),
            Track( 23, "Gymnopédie No. 1", "Gymnopédie No. 1", 5, 2 * 60 + 35),
            Track( 24, "Habanera", "Habanera (From Carmen)", 5, 3 * 60 + 16),
            Track( 25, "Happy Birthday", "Happy Birthday to You (Ska Mix)", 5, 1 * 60 + 24),
            Track( 26, "Hava Nagila", "Hava Nagila", 5, 2 * 60 + 3),
            Track( 27, "Hello! Ma Baby", "Hello! Ma Baby", 6, 2 * 60 + 51),
            Track( 28, "Hino Do Brasil", "Hino Nacional Brasileiro", 7, 1 * 60 + 52),
            Track( 29, "Hungarian Dance", "Hungarian Dance No. 5", 7, 2 * 60 + 48, true),
            Track( 30, "Hungarian Rhapsody", "Hungarian Rhapsody No. 2", 9, 4 * 60 + 14),
            Track( 31, "Jarabe Tapatío", "Jarabe Tapatío (Mexican Hat Dance)", 9, 1 * 60 + 21),
            Track( 32, "Jasmine Flower", "Jasmine Flower (Mo Li Hua)", 3, 1 * 60 + 50),
            Track( 33, "Jingle Bells", "Jingle Bells (Jazz Mix)", 5, 1 * 60 + 50),
            Track( 34, "Korobeiniki", "Korobeiniki", 7, 3 * 60 + 2),
            Track( 35, "Long-Tail Limbo", "Long-Tail Limbo", 5, 1 * 60 + 29),
            Track( 36, "Mars", "Mars, The Bringer of War", 4, 2 * 60 + 20),
            Track( 37, "Marseillaise", "La Marseillaise", 7, 1 * 60 + 20),
            Track( 38, "Martian Killbots", "Martial Killbots!!!", 3, 2 * 60 + 0),
            Track( 39, "Merry Gentlemen", "God Rest ye Merry, Gentlemen", 7, 1 * 60 + 55),
            Track( 40, "Mountain King", "In The Hell of The Mountain King", 8, 2 * 60 + 30),
            Track( 41, "O Canada", "O Canada", 3, 1 * 60 + 8),
            Track( 42, "O Christmas Tree", "O Christmas Tree", 4, 2 * 60 + 25),
            Track( 43, "Ode to Joy", "Ode to Joy", 2, 1 * 60 + 43),
            Track( 44, "Oh Chanukah!", "Chanukah Oh Chanukah!", 8, 2 * 60 + 0),
            Track( 45, "Old Gray Mare", "Old Gray Mare", 5, 1 * 60 + 17),
            Track( 46, "Old MacDonald", "Old MacDonald", 7, 1 * 60 + 45),
            Track( 47, "Rhapsody in Blue", "Rhapsody in Blue", 8, 4 * 60 + 18),
            Track( 48, "Rising Sun Blues", "The House of The Rising Sun", 4, 2 * 60 + 15),
            Track( 49, "Rosamunde", "Rosamunde (Beer Barrel Polka)", 6, 1 * 60 + 41),
            Track( 50, "Round the Mountain", "She'll Be Coming 'Round the Mountain", 7, 1 * 60 + 54),
            Track( 51, "Sailor's Hornpipe", "The Sailor's Hornpipe", 10, 2 * 60 + 0),
            Track( 52, "Sakura", "Sakura Sakura", 2, 1 * 60 + 21),
            Track( 53, "Silent Night", "Max Tundra's Silent Night", 4, 2 * 60 + 16),
            Track( 54, "Skabird", "Skabird", 6, 2 * 60 + 47),
            Track( 55, "Skeleton Rag", "The Skeleton Rag (Remix)", 6, 3 * 60 + 12),
            Track( 56, "Skip to My Lou", "Skip to My Lou", 5, 1 * 60 + 13),
            Track( 57, "St James Trombonery", "St. James Trombonery Blues", 4, 2 * 60 + 13),
            Track( 58, "Stars & Stripes", "Stars and Stripes Forever", 8, 2 * 60 + 34, true),
            Track( 59, "Star-Spangled", "The Star-Spangled Banner", 5, 1 * 60 + 12),
            Track( 60, "Sugar Plum Fairy", "Dance of The Sugar Plum Fairy", 5, 1 * 60 + 38),
            Track( 61, "T. Champ Medley", "Trombone Champ Medley", 7, 4 * 60 + 21),
            Track( 62, "Taps", "Taps", 3, 39),
            Track( 63, "The Can-Can", "The Can-Can", 8, 2 * 60 + 19),
            Track( 64, "The Ritz", "Puttin On The Ritz", 7, 2 * 60 + 0),
            Track( 65, "The Riverside", "Down By The Riverside", 5, 2 * 60 + 13),
            Track( 66, "The Saints", "When The Saints Go Marching In", 5, 1 * 60 + 55),
            Track( 67, "Toccata & Fugue", "Toccata & Fugue in D Minor", 8, 3 * 60 + 5),
            Track( 68, "Trombone Fuerte", "Trombone Fuerte", 9, 1 * 60 + 25),
            Track( 69, "Trombone Skyze", "Trombone Skyze", 4, 2 * 60 + 24),
            Track( 70, "Trombone Skyze (Nasty)", "Trombone Skyze (Nasty Mix)", 8, 2 * 60 + 14),
            Track( 71, "W. Post March", "The Washington Post March", 7, 2 * 60 + 34, true),
            Track( 72, "Warm-Up", "Warm-Up", 1, 1 * 60 + 12),
            Track( 73, "William Tell", "William Tell Overture", 8, 1 * 60 + 52),
            Track( 74, "Zarathustra", "Also Sprach Zarathustra", 1, 1 * 60 + 26),
            // celeste
            Track(201, "Confronting Myself", "Confronting Myself", 7, 4 * 60 + 11, true, "Celeste"),
            Track(202, "First Steps", "First Steps", 7, 3 * 60 + 34, true, "Celeste"),
            Track(203, "Heart of The Mountain", "Heart of The Mountain", 7, 3 * 60 + 36, true, "Celeste"),
            Track(204, "Madeline and Theo", "Madeline and Theo", 6, 3 * 60 + 13, true, "Celeste"),
            Track(205, "Reach For The Summit", "Reach For The Summit", 6, 4 * 60, true, "Celeste"),
            Track(206, "Reflection", "Reflection", 6, 3 * 60 + 11, true, "Celeste"),
            Track(207, "Resurrections", "Resurrections", 6, 4 * 60 + 18, true, "Celeste"),
            Track(208, "Scattered and Lost", "Scattered and Lost", 8, 4 * 60 + 34, true, "Celeste"),
            Track(209, "Spirit of Hospitality", "Spirit of Hospitality", 6, 1 * 60 + 48, true, "Celeste"),
            Track(210, "Starjump", "Starjump", 8, 2 * 60 + 10, true, "Celeste"),
            // pizza tower
            Track(251, "Bye Bye There!", "Bye Bye There!", 7, 4 * 60, true, "Pizza Tower"),
            Track(252, "Cold Spaghetti", "Cold Spaghetti", 8, 2 * 60 + 52, true, "Pizza Tower"),
            Track(253, "Death I Deservioli", "The Death That I Deservioli", 8, 2 * 60 + 56, true, "Pizza Tower"),
            Track(254, "ET Wahwahs", "Extraterrestrial Wahwahs", 8, 3 * 60 + 32, true, "Pizza Tower"),
            Track(255, "Funiculi Holiday", "Funiculi Holiday", 7, 1 * 60 + 19, true, "Pizza Tower"),
            Track(256, "Good Eatin'", "Good Eatin'", 8, 3 * 60 + 26, true, "Pizza Tower"),
            Track(257, "It's Pizza Time!", "It's Pizza Time!", 8, 3 * 60 + 40, true, "Pizza Tower"),
            Track(258, "Kid's Menu", "What On the Kid's Menu?", 9, 3 * 60 + 58, true, "Pizza Tower"),
            Track(259, "Oregano Mirage", "Oregano Mirage", 9, 2 * 60 + 29, true, "Pizza Tower"),
            Track(260, "Pizza Deluxe", "Pizza Deluxe", 7, 2 * 60 + 16, true, "Pizza Tower"),
            Track(261, "Pumpin' Hot Stuff", "Pumpin' Hot Stuff", 7, 2 * 60 + 19, true, "Pizza Tower"),
            Track(262, "Put On a Show!", "Put On a Show!", 7, 2 * 60 + 43, true, "Pizza Tower"),
            Track(263, "Unexpectancy", "Unexpectancy (Part 2 of 3)", 9, 2 * 60 + 59, true, "Pizza Tower"),
            Track(264, "Yeehaw", "Yeehaw Deliveryboy", 8, 2 * 60 + 7, true, "Pizza Tower"),
            // undertale / deltarune
            Track(301, "Asgore", "Asgore", 8, 2 * 60 + 36, true, "Toby Fox"),
            Track(302, "Big Shot", "Big Shot", 7, 2 * 60 + 24, true, "Toby Fox"),
            Track(303, "Black Knife", "Black Knife", 7, 1 * 60 + 59, true, "Toby Fox"),
            Track(304, "Bonetrousle", "Bonetrousle", 8, 60, true, "Toby Fox"),
            Track(305, "Cyber's World?", "A Cyber's World?", 7, 2 * 60 + 48, true, "Toby Fox"),
            Track(306, "Dark Sanctuary", "Dark Sanctuary + Neverending Night", 7, 3 * 60 + 6, true, "Toby Fox"),
            Track(307, "Dummy!", "Dummy!", 9, 2 * 60 + 29, true, "Toby Fox"),
            Track(308, "Guardian", "Guardian", 8, 3 * 60 + 30, true, "Toby Fox"),
            Track(309, "Hopes and Dreams", "Hopes and Dreams", 8, 3 * 60 + 3, true, "Toby Fox"),
            Track(310, "It's TV Time!", "It's TV Time!", 8, 2 * 60 + 48, true, "Toby Fox"),
            Track(311, "Killer Queen", "Attack of the Killer Queen", 8, 2 * 60 + 3, true, "Toby Fox"),
            Track(312, "Megalovania", "Megalovania", 9, 2 * 60 + 39, true, "Toby Fox"),
            Track(313, "Metal Crusher", "Metal Crusher", 9, 1 * 60 + 5, true, "Toby Fox"),
            Track(314, "Pandora Palace", "Pandora Palace", 7, 1 * 60 + 40, true, "Toby Fox"),
            Track(315, "Rude Buster", "Rude Buster", 9, 1 * 60 + 19, true, "Toby Fox"),
            Track(316, "Scarlet Forest", "Scarlet Forest", 6, 2 * 60 + 12, true, "Toby Fox"),
            Track(317, "Sword", "Sword", 7, 1 * 60 + 50, true, "Toby Fox"),
            Track(318, "Third Sanctuary", "The Third Sanctuary", 9, 4 * 60 + 8, true, "Toby Fox"),
            Track(319, "True Hero", "True Hero", 8, 2 * 60 + 36, true, "Toby Fox"),
            Track(320, "TV World", "TV World", 7, 2 * 60 + 12, true, "Toby Fox"),
            Track(321, "World Revolving", "The World Revolving", 8, 1 * 60 + 44, true, "Toby Fox"),
        )

        public fun getTrackList(settings: Settings): List<Track> {
            return TRACK_LIST.filter { track ->
                track.diff >= settings.minDiff &&
                track.diff <= settings.maxDiff &&
                (settings.unsafe || !track.unsafe) &&
                (
                    track.DLC == "Base" ||
                    (track.DLC == "Celeste" && settings.celeste) ||
                    (track.DLC == "Pizza Tower" && settings.pizza) ||
                    (track.DLC == "Toby Fox" && settings.toby)
                )
            }
        }

        public fun getHardest(settings: Settings): List<Track> {
            val tracks = getTrackList(settings)
            val hardest = tracks.maxOf { it.diff }
            return tracks.filter { track -> track.diff == hardest }
        }

        public fun getGoalTrack(settings: Settings): Track? {
            if (settings.goalTracks > 0) return null
            return getHardest(settings).maxBy { it.len }
        }
    }
}
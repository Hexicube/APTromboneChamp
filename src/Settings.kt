enum class DifficultyGatingMode(val id: Int) {
    OFF(0),
    ON(1),
    PROG(2);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }
}

data class Settings(
    var goalTracks: Int,
    var goalTrack: Track?,
    var goalRating: Int,
    var startRating: Int,
    var easyTrackGap: Int,
    var hotDogs: Int,
    var extraHotDogs: Int,
    var trackGating: Boolean,
    var diffGating: DifficultyGatingMode,

    var minDiff: Int,
    var maxDiff: Int,
    var unsafe: Boolean,
    var celeste: Boolean,
    var pizza: Boolean,
    var toby: Boolean,
    var removedTracks: List<Track>
)
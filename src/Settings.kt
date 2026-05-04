enum class DifficultyGatingMode {
    OFF, ON, PROG
}

data class Settings(
    var goalTracks: Int,
    var goalTrack: Track?,
    var goalRating: Int,
    var startRating: Int,
    var easyTrackGap: Int,
    var hotDogs: Int,
    var extraHotDogs: Int,
    var diffGating: DifficultyGatingMode,

    var minDiff: Int,
    var maxDiff: Int,
    var unsafe: Boolean,
    var celeste: Boolean,
    var pizza: Boolean,
    var toby: Boolean,
    var removedTracks: List<Track>
)
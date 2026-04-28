import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

enum class APPermission(val id: Int) {
    DISABLED(0b000),
    ENABLED(0b001),
    GOAL(0b010),
    AUTO(0b110),
    AUTO_ENABLED(0b111);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }
}

data class APNetworkPlayer(val team: Int, val slot: Int, val alias: String, val name: String) {
    companion object {
        fun fromJson(json: JsonObject): APNetworkPlayer {
            return APNetworkPlayer(
                json.get("team").asInt,
                json.get("slot").asInt,
                json.get("alias").asString,
                json.get("name").asString
            )
        }
    }
}

enum class SlotType(val id: Int) {
    Spectator(0b00),
    Player(0b01),
    Group(0b10);
    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }
}

data class APNetworkSlot(val name: String, val game: String, val type: SlotType)

enum class APItemFlags(val id: Int) {
    ADVANCEMENT(0b001),
    VERY_USEFUL(0b010),
    TRAP(0b100);

    companion object {
        fun fromID(id: Int): List<APItemFlags> {
            val flags = ArrayList<APItemFlags>()
            if (id and 0b001 != 0) flags.add(ADVANCEMENT)
            if (id and 0b010 != 0) flags.add(VERY_USEFUL)
            if (id and 0b100 != 0) flags.add(TRAP)
            return flags.toList()
        }
    }
}

data class APNetworkItem(val item: Long, val location: Long, val player: Int, val flags: Int) {
    companion object {
        fun fromJson(json: JsonObject): APNetworkItem {
            return APNetworkItem(
                json.get("item").asLong,
                json.get("location").asLong,
                json.get("player").asInt,
                json.get("flags").asInt
            )
        }
    }

    fun toJson(): JsonObject {
        TODO()
    }
}

data class APNetworkVersion(val major: Int, val minor: Int, val build: Int) {
    companion object {
        fun fromJson(json: JsonObject): APNetworkVersion {
            return APNetworkVersion(json.get("major").asInt, json.get("minor").asInt, json.get("build").asInt)
        }
    }
    fun toJson(): JsonElement {
        val obj = JsonObject()
        obj.add("major", JsonPrimitive(major))
        obj.add("minor", JsonPrimitive(minor))
        obj.add("build", JsonPrimitive(build))
        obj.add("class", JsonPrimitive("Version"))
        return obj
    }
}

enum class HintStatus(val id: Int) {
    UNSPECIFIED(0),
    FILLER(10),
    TRAP(20),
    PRIORITY(30),
    FOUND(40);
    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }
}

// TODO: what are flags for?
data class Hint(val receivingPlayer: Int, val findingPlayer: Int, val location: Long, val item: Long, val found: Boolean, val flags: Int, val status: HintStatus) {
    fun getFinderName(): String {
        try {
            return MainFrame.CONN.playerList.first { it.name == MainFrame.CONN.slotList[findingPlayer]!!.name }.alias
        }
        catch (e: Exception) {
            e.printStackTrace()
            return "PLAYER:$findingPlayer"
        }
    }
    fun getReceiverName(): String {
        try {
            return MainFrame.CONN.playerList.first { it.name == MainFrame.CONN.slotList[receivingPlayer]!!.name }.alias
        }
        catch (e: Exception) {
            e.printStackTrace()
            return "PLAYER:$receivingPlayer"
        }
    }
    fun getItemName(): String{
        try {
            return MainFrame.CONN.slotItemMap[MainFrame.CONN.slotList[receivingPlayer]!!.game]!![item]!!
        }
        catch (e: Exception) {
            e.printStackTrace()
            return "ITEM:$item"
        }
    }
    fun getItemLoc(): String {
        try {
            if (location == -1L) return "Cheated Item"
            if (location == -2L) return "Starting Item"
            return MainFrame.CONN.slotLocMap[MainFrame.CONN.slotList[findingPlayer]!!.game]!![location]!!
        }
        catch (e: Exception) {
            e.printStackTrace()
            return "LOC:$location"
        }
    }
}
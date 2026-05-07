import MainFrame.Companion.CONN
import com.google.gson.*
import java.awt.Color
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.UUID
import java.util.concurrent.CompletionStage
import javax.swing.JOptionPane

class APConnectionManager : WebSocket.Listener {
    companion object {
        private const val GAME_NAME = "Trombone Champ"
        private val AP_VERSION = APNetworkVersion(0, 6, 7)
    }

    private var socket: WebSocket? = null
    private var lastIndex = 0
    private var thisTeam = -1
    var thisSlot = -1

    var lastFunFact: Long = System.currentTimeMillis()

    /*
    Connection handshake:
    - Client connects
    - Server accepts and sends RoomInfo
    - Client sends GetDataPackage (optional)
        - Server responds with DataPackage
    - Client sends Connect
        - Server validates and responds with Connected or ConnectionRefused (ConnectionRefused allows retries)
        - Server may send ReceivedItems
    - Server sends PrintJSON globally
    */

    /*
    0: Awaiting RoomInfo
    1: Awaiting DataPackage (optional)
    2: Awaiting Connected/ConnectionRefused
    3: Connected
    */
    private var connectionState = 0

    val slotLocMap = HashMap<String, HashMap<Long, String>>()
    val slotItemMap = HashMap<String, HashMap<Long, String>>()

    val playerList = ArrayList<APNetworkPlayer>()
    val slotList = HashMap<Int, APNetworkSlot>()

    val hints = ArrayList<Hint>()
    var hintCost = 0
    var hintPoints = 0

    private fun disconnect() {
        try {
            socket?.sendClose(1000, "")?.get() // .get() waits for the packet to send
            socket?.abort()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            socket = null
            connectionState = 0
            thisTeam = -1
            thisSlot = -1

            MainFrame.LOCS.clear()
            MainFrame.ITEMS.clear()
            hints.clear()
            slotLocMap.clear()
            slotItemMap.clear()
            playerList.clear()
            slotList.clear()
            MainFrame.update()
        }
    }

    fun getThisPlayer() = playerList.firstOrNull { it.slot == thisSlot && it.team == thisTeam }

    fun findOwnHintItem(item: Long) = hints.firstOrNull { it.receivingPlayer == thisSlot && it.item == item }
    fun findOwnHintItemList(item: Long) = hints.filter { it.receivingPlayer == thisSlot && it.item == item }
    fun findOwnHintLoc(loc: Long) = hints.firstOrNull { it.findingPlayer == thisSlot && it.location == loc }

    fun connect(uri: String, secure: Boolean = true) {
        disconnect()
        try {
            HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(URI("ws${if (secure) "s" else ""}://$uri"), this).get()
        }
        catch (e: Exception) {
            if (secure) connect(uri, false)
            else e.printStackTrace()
        }
    }

    override fun onOpen(webSocket: WebSocket?) {
        println("Open: ${webSocket.toString()}")
        if (socket != null) println("[WARN] Socket was overridden")
        socket = webSocket
        webSocket?.request(1)
    }

    override fun onError(webSocket: WebSocket?, error: Throwable?) {
        if (socket != webSocket) println("[WARN] Socket mismatch on error")
        error?.printStackTrace()
        webSocket?.request(1)
    }

    override fun onClose(webSocket: WebSocket?, statusCode: Int, reason: String?): CompletionStage<*>? {
        if (webSocket == socket) socket = null
        println("Socket closed: [$statusCode] $reason")
        return null
    }

    private val parts: ArrayList<CharSequence> = ArrayList()
    override fun onText(webSocket: WebSocket?, data: CharSequence?, last: Boolean): CompletionStage<*>? {
        webSocket?.request(1)
        if (data == null) return null
        if (socket != webSocket) println("[WARN] Socket mismatch on text in")
        parts.add(data)
        if (!last) return null

        val text = parts.joinToString("")
        parts.clear()
        val dataList = JsonParser.parseString(text).asJsonArray
        for (entry in dataList) {
            try {
                val data = entry.asJsonObject
                when (val id = data.get("cmd").asString) {
                    "RoomInfo" -> {
                        if (connectionState != 0) {
                            println("[WARN] RoomInfo packet received at wrong time, ignoring")
                            continue
                        }
                        val APVersion = APNetworkVersion.fromJson(data.get("version").asJsonObject)
                        if (APVersion != AP_VERSION) println("[WARN] Server AP version does not match supported version")
                        val genVersion = APNetworkVersion.fromJson(data.get("generator_version").asJsonObject)
                        if (genVersion != AP_VERSION) println("[WARN] Server gen version does not match supported version")
                        val tags = data.get("tags").asJsonArray.map { it.asString }
                        val needsPassword = data.get("password")
                        val perms = data.get("permissions").asJsonObject.asMap() // TODO: int map?
                        hintCost = data.get("hint_cost").asInt
                        val locationCheckPoints = data.get("location_check_points").asInt
                        // println("LocChkPts: $locationCheckPoints") // what is this for?
                        val gameList = data.get("games").asJsonArray.map { it.asString }
                        if (!gameList.contains(GAME_NAME)) {
                            println("[ERR ] Room does not contains this game")
                            JOptionPane.showMessageDialog(MainFrame.INST, "Room does not contain this game.", "Game not in room", JOptionPane.ERROR_MESSAGE)
                            disconnect()
                            continue
                        }
                        // TODO: datapackage_checksums dict[str,str]
                        val seed = data.get("seed_name").asString
                        val time = data.get("time").asFloat

                        connectionState = 1
                        sendGetDataPackage()
                    }
                    "ConnectionRefused" -> {
                        if (connectionState != 2) {
                            println("[WARN] ConnectionRefused packet received at wrong time, ignoring")
                            continue
                        }
                        val errors = data.get("errors").asJsonArray.map { it.asString }
                        println("ConnectionRefused: ${errors.joinToString()}")
                        if (errors.contains("InvalidSlot")) {
                            JOptionPane.showMessageDialog(MainFrame.INST, "Room does not contain slot \"${MainFrame.connectSlot.text}\".", "Slot not in room", JOptionPane.ERROR_MESSAGE)
                        }
                        else if (errors.contains("InvalidGame")) {
                            JOptionPane.showMessageDialog(MainFrame.INST, "Slot \"${MainFrame.connectSlot.text}\" is not for this game.", "Slot wrong", JOptionPane.ERROR_MESSAGE)
                        }
                        else if (errors.contains("IncompatibleVersion")) {
                            JOptionPane.showMessageDialog(MainFrame.INST, "Version mismatch.", "Bad version", JOptionPane.ERROR_MESSAGE)
                        }
                        else if (errors.contains("InvalidPassword")) {
                            JOptionPane.showMessageDialog(MainFrame.INST, "Wrong password.", "Bad password", JOptionPane.ERROR_MESSAGE)
                        }
                        else if (errors.contains("InvalidItemsHandling")) {
                            JOptionPane.showMessageDialog(MainFrame.INST, "Client incompatible with room item handling.", "Bad item handling", JOptionPane.ERROR_MESSAGE)
                        }
                        else {
                            JOptionPane.showMessageDialog(MainFrame.INST, "Unknown error: \"${errors.joinToString()}\".", "Unknown error", JOptionPane.ERROR_MESSAGE)
                        }
                        disconnect()
                        break
                    }
                    "Connected" -> {
                        if (connectionState != 2) {
                            println("[WARN] Connected packet received at wrong time, ignoring")
                            continue
                        }
                        thisTeam = data.get("team").asInt
                        thisSlot = data.get("slot").asInt
                        playerList.clear()
                        playerList.addAll(data.get("players").asJsonArray.map { APNetworkPlayer.fromJson(it.asJsonObject) })
                        val missingLocations = data.get("missing_locations").asJsonArray.map { it.asLong }
                        val checkedLocations = data.get("checked_locations").asJsonArray.map { it.asLong }
                        MainFrame.LOCS.clear()
                        MainFrame.LOCS.addAll(checkedLocations)
                        hintCost = (missingLocations.size + checkedLocations.size) * hintCost / 100
                        val slotData = data.get("slot_data").asJsonObject
                        MainFrame.SETTINGS.goalTracks = slotData.get("goal").asInt
                        val goalTrackName = slotData.get("goal_track").asString
                        if (goalTrackName == "none") MainFrame.SETTINGS.goalTrack = null
                        else MainFrame.SETTINGS.goalTrack = Track.TRACK_LIST.first { it.name == goalTrackName }
                        MainFrame.SETTINGS.goalRating = slotData.get("rating").asInt
                        MainFrame.SETTINGS.startRating = slotData.get("rating_start").asInt
                        MainFrame.SETTINGS.easyTrackGap = slotData.get("easy_track").asInt
                        MainFrame.SETTINGS.trackGating = slotData.get("track_gating").asInt > 0
                        MainFrame.SETTINGS.diffGating = DifficultyGatingMode.fromID(slotData.get("difficulty_gating").asInt)
                        MainFrame.SETTINGS.hotDogs = slotData.get("hot_dogs").asInt
                        MainFrame.SETTINGS.extraHotDogs = slotData.get("extra_hot_dogs").asInt
                        MainFrame.SETTINGS.minDiff = slotData.get("min_diff").asInt
                        MainFrame.SETTINGS.maxDiff = slotData.get("max_diff").asInt
                        MainFrame.SETTINGS.unsafe = slotData.get("unsafe").asInt == 1
                        MainFrame.SETTINGS.celeste = slotData.get("celeste").asInt == 1
                        MainFrame.SETTINGS.pizza = slotData.get("pizza").asInt == 1
                        MainFrame.SETTINGS.toby = slotData.get("toby").asInt == 1
                        MainFrame.SETTINGS.removedTracks = slotData.get("removed_tracks").asJsonArray.map { name ->
                            Track.TRACK_LIST.first { it.name == name.asString }
                        }

                        MainFrame.updateAllEntries(Track.getTrackList(MainFrame.SETTINGS))

                        //playerMap.clear()
                        slotList.clear()
                        val allSlots = data.get("slot_info").asJsonObject.asMap()
                        for (slot in allSlots) {
                            val slotObj = slot.value.asJsonObject
                            slotList[slot.key.toInt()] = APNetworkSlot(
                                slotObj.get("name").asString,
                                slotObj.get("game").asString,
                                SlotType.fromID(slotObj.get("type").asInt)
                            )
                        }

                        hintPoints = data.get("hint_points").asInt

                        connectionState = 3

                        updateStatus(if (checkedLocations.isEmpty()) ClientStatus.CONNECTED else ClientStatus.PLAYING)
                        MainFrame.checkWin()

                        // fetch hints
                        sendGet("_read_hints_${thisTeam}_${thisSlot}")
                    }
                    "ReceivedItems" -> {
                        if (connectionState != 3) {
                            println("[WARN] ReceivedItems packet received at wrong time, ignoring")
                            continue
                        }
                        val index = data.get("index").asInt // TODO: track index and make sure it lines up
                        val items = data.get("items").asJsonArray.map { APNetworkItem.fromJson(it.asJsonObject) }
                        var hintRefresh = false
                        var updateList = false
                        for (item in items) {
                            MainFrame.ITEMS.add(item.item)
                            val track = MainFrame.trackList.firstOrNull { it.ID == item.item }
                            if (track != null) MainFrame.trackEntries[track]?.update()
                            // index=0 means the client just connected (always appears due to starter item)
                            if (item.item == 1003L && index != 0) { // fun fact item
                                val now = System.currentTimeMillis()
                                if (now - lastFunFact > 1000L) { // prevent spamming when multiple are found at once
                                    lastFunFact = now
                                    if (MainFrame.CUR_FACTS.isEmpty()) {
                                        MainFrame.CUR_FACTS.addAll(MainFrame.ALL_FACTS)
                                        MainFrame.CUR_FACTS.shuffle()
                                    }
                                    val fact = MainFrame.CUR_FACTS.removeAt(0)
                                    sendChat("FUN FACT: $fact")
                                }
                            }
                            if (item.item == 1001L || item.item == 1004L || item.item == 1011L) hintRefresh = true // hint data needs refreshing as there are multiple of these
                            if (item.item < 1000L || item.item == 1001L || item.item == 1004L || item.item > 1010L) updateList = true
                        }
                        if (hintRefresh) sendGet("_read_hints_${thisTeam}_${thisSlot}")
                        if (updateList) {
                            MainFrame.update()
                            MainFrame.sortTrackList()
                            if (!hintRefresh) MainFrame.updateHints()
                        }
                    }
                    "LocationInfo" -> {
                        // Response packet for LocationScouts
                        if (connectionState != 3) {
                            println("[WARN] LocationInfo packet received at wrong time, ignoring")
                            continue
                        }
                        val locations = data.get("locations").asJsonArray.map { APNetworkItem.fromJson(it.asJsonObject) }
                        locations.forEach { loc ->
                            // TODO: handle this?
                            println("${loc.item} ${loc.location} ${loc.player}")
                        }
                    }
                    "RoomUpdate" -> {
                        // print it if theres something new
                        val keys = ArrayList(data.keySet())
                        keys.remove("cmd")
                        keys.remove("hint_points")
                        keys.remove("players")
                        keys.remove("checked_locations")
                        if (keys.isNotEmpty()) println(data)

                        var needsHintUpdate = false
                        if (data.has("hint_points")) hintPoints = data.get("hint_points").asInt
                        if (data.has("players")) {
                            playerList.clear()
                            playerList.addAll(data.get("players").asJsonArray.map { APNetworkPlayer.fromJson(it.asJsonObject) })
                            needsHintUpdate = true
                        }
                        if (data.has("checked_locations")) {
                            val locs = data.get("checked_locations").asJsonArray.map { it.asLong }
                            for (loc in locs) {
                                if (!MainFrame.LOCS.contains(loc)) MainFrame.LOCS.add(loc)
                            }
                            MainFrame.update()
                            MainFrame.checkWin()
                        }

                        if (needsHintUpdate) MainFrame.updateHints()
                    }
                    "PrintJSON" -> {
                        val type = data.get("type")?.asString ?: ""
                        val text = StringBuilder()
                        when (type) {
                            "Tutorial" -> {} // player info for things like !help
                            "Hint" -> {
                                // add the hint if it applies to this player
                                val receiver = data.get("receiving").asInt
                                val itemData = data.get("item").asJsonObject
                                val hintTextPiece = data.get("data").asJsonArray.firstNotNullOfOrNull {
                                    val obj = it.asJsonObject
                                    if (obj.has("hint_status")) obj.get("hint_status").asInt else null
                                } ?: 0
                                val hint = Hint(
                                    receiver,
                                    itemData.get("player").asInt,
                                    itemData.get("location").asLong,
                                    itemData.get("item").asLong,
                                    data.get("found").asBoolean,
                                    itemData.get("flags").asInt,
                                    HintStatus.fromID(hintTextPiece)
                                )
                                if (hint.findingPlayer == thisSlot || hint.receivingPlayer == thisSlot) {
                                    hints.removeIf {
                                        it.findingPlayer == hint.findingPlayer &&
                                        it.receivingPlayer == hint.receivingPlayer &&
                                        it.location == hint.location &&
                                        it.item == hint.item
                                    }
                                    hints.add(hint)
                                    MainFrame.updateHints()
                                }
                                if (!hint.found) { // dont bother displaying found hints to reduce chat congestion
                                    // does its own formatting
                                    for (textObj in data.get("data").asJsonArray) {
                                        val obj = textObj.asJsonObject
                                        val str = obj.get("text").asString
                                        val subtype = obj.get("type")?.asString ?: ""
                                        when (subtype) {
                                            "" -> text.append(str)
                                            "player_id" -> text.append("<span style='color:blue'>${slotList[str.toInt()]?.name ?: "PLAYER:$str"}</span>")
                                            "item_id" -> text.append("<span style='color:red'>${slotItemMap[slotList[hint.receivingPlayer]?.game]?.get(str.toLong()) ?: "ITEM:$str"}</span>")
                                            "location_id" -> text.append("<span style='color:green'>${slotLocMap[slotList[hint.findingPlayer]?.game]?.get(str.toLong()) ?: "LOC:$str"}</span>")
                                            "hint_status" -> text.append("<span style='color:purple'>$str</span>")
                                            else -> {
                                                println("Unknown type inside PrintJSON(Hint): $subtype")
                                                println(data) // entrance_name
                                            }
                                        }
                                    }
                                    MainFrame.addChatMessage(text.toString(), Color(.8f, 1f, 1f))
                                }
                                continue
                            }
                            "ItemSend" -> { // player found an item
                                // does its own formatting
                                var isForThisSlot = false
                                var isFromThisSlot = false
                                for (textObj in data.get("data").asJsonArray) {
                                    val obj = textObj.asJsonObject
                                    val str = obj.get("text").asString
                                    val subtype = obj.get("type")?.asString ?: ""
                                    when (subtype) {
                                        "" -> text.append(str)
                                        "player_id" -> text.append("<span style='color:blue'>${slotList[str.toInt()]?.name ?: "PLAYER:$str"}</span>")
                                        "item_id" -> {
                                            val slot = obj.get("player").asInt
                                            if (slot == thisSlot) isForThisSlot = true
                                            text.append("<span style='color:red'>${slotItemMap[slotList[slot]?.game]?.get(str.toLong()) ?: "ITEM:$str"}</span>")
                                        }
                                        "location_id" -> {
                                            val slot = obj.get("player").asInt
                                            if (slot == thisSlot) isFromThisSlot = true
                                            text.append("<span style='color:green'>${slotLocMap[slotList[slot]?.game]?.get(str.toLong()) ?: "LOC:$str"}</span>")
                                        }
                                        else -> println("Unknown type inside PrintJSON(ItemSend): $subtype")
                                    }
                                }
                                // temporary: filter out anything not for us or from us (make this a toggle)
                                if (!isForThisSlot && !isFromThisSlot) continue

                                var bg = Color.WHITE
                                if (isForThisSlot) {
                                    val itemFlags = APItemFlags.fromID(data.get("item").asJsonObject.get("flags").asInt)
                                    val isUseful = itemFlags.contains(APItemFlags.ADVANCEMENT) || itemFlags.contains(APItemFlags.VERY_USEFUL)
                                    val isTrap = itemFlags.contains(APItemFlags.TRAP)
                                    if (isUseful) bg = Color(1f, 1f, .5f)
                                }

                                MainFrame.addChatMessage(text.toString(), bg)
                                continue
                            }
                            "", "Chat", "Join", "Part", "Goal", "Collect", "Release", "TagsChanged" -> {} // general chat or messages with no components
                            "Countdown" -> {
                                // avoid double-printing first number
                                if (data.get("data").asJsonArray[0].asJsonObject.get("text").asString.contains("Starting")) continue

                                // special case just to make it more visible
                                val num = data.get("countdown").asInt
                                val text = if (num == 0) "GO" else num.toString()
                                MainFrame.addChatMessage("<p style='font-size:30px;text-align:center'>$text</p>")
                                continue
                            }
                            "CommandResult" -> {
                                // make it slightly larger since it's probably important
                                for (textObj in data.get("data").asJsonArray) {
                                    val obj = textObj.asJsonObject
                                    val str = obj.get("text").asString
                                    val subtype = obj.get("type")?.asString ?: ""
                                    when (subtype) {
                                        "" -> text.append(str)
                                        "player_id" -> text.append("<span style='color:blue'>${slotList[str.toInt()]?.name ?: "PLAYER:$str"}</span>")
                                        "item_id" -> text.append("<span style='color:red'>${"ITEM:$str"}</span>")
                                        "location_id" -> text.append("<span style='color:green'>${"LOC:$str"}</span>")
                                        else -> println("Unknown type inside PrintJSON(CommandResult): $subtype")
                                    }
                                }
                                MainFrame.addChatMessage("<p style='font-size:12px'>$text</p>")
                                continue
                            }
                            else -> {
                                println("Unknown PrintJSON type: $type")
                                println(data)
                            }
                        }
                        // general-purpose formatting, should be plain text only
                        for (textObj in data.get("data").asJsonArray) {
                            val obj = textObj.asJsonObject
                            val str = obj.get("text").asString
                            val subtype = obj.get("type")?.asString ?: ""
                            when (subtype) {
                                "" -> text.append(str)
                                "player_id" -> text.append(slotList[str.toInt()]!!.name)
                                "item_id" -> text.append("ITEM:$str")
                                "location_id" -> text.append("LOC:$str")
                                "hint_status" -> text.append(str)
                                else -> println("Unknown type inside PrintJSON(Generic): $subtype")
                            }
                        }
                        MainFrame.addChatMessage(text.toString())
                    }
                    "DataPackage" -> {
                        // Response packet for GetDataPackage during handshake
                        if (connectionState != 1) {
                            println("[WARN] DataPackage packet received at wrong time, ignoring")
                            continue
                        }
                        // handle the data
                        val games = data.get("data").asJsonObject.get("games").asJsonObject.asMap()
                        for (game in games) {
                            val gameName = game.key
                            val locMap = HashMap<Long, String>()
                            for (loc in game.value.asJsonObject.get("location_name_to_id").asJsonObject.asMap()) {
                                locMap[loc.value.asLong] = loc.key
                            }
                            val itemMap = HashMap<Long, String>()
                            for (item in game.value.asJsonObject.get("item_name_to_id").asJsonObject.asMap()) {
                                itemMap[item.value.asLong] = item.key
                            }
                            slotLocMap[gameName] = locMap
                            slotItemMap[gameName] = itemMap
                        }

                        // TODO: generate and locally store UUID?
                        // NOTE: technically bad to use .text on a password field, but this password isn't sensitive; it's just for the UI hiding it
                        connectionState = 2
                        sendConnect(MainFrame.connectSlot.text, UUID.randomUUID().toString(), MainFrame.connectPass.text)
                    }
                    "Bounced" -> {
                        // Response packet for Bounce, ignored
                        // NOTE: This is needed for DeathLink
                    }
                    "InvalidPacket" -> {
                        val problem = data.get("type").asString
                        println("[ERR ] Invalid packet was sent: ${data.get("text").asString}")
                        if (problem == "arguments") {
                            println("[ERR ] - Original packet: ${data.get("original_cmd").asString}")
                        }
                        else println("[ERR ] - Original packet could not be parsed")
                    }
                    "Retrieved" -> {
                        // Response packet for Get, needed for hint data
                        val keys = data.get("keys").asJsonObject.asMap()
                        for (entry in keys) {
                            if (entry.key == "_read_hints_${thisTeam}_${thisSlot}") {
                                hints.clear()
                                handleHintData(entry.value.asJsonArray)
                            }
                            else println("Unknown Retrieved key: ${entry.key}")
                        }
                    }
                    "SetReply" -> {
                        // Response packet for Set/SetNotify, needed for !hint reply
                        val key = data.get("key").asString
                        if (key == "_read_hints_${thisTeam}_${thisSlot}") {
                            handleHintData(data.get("value").asJsonArray)
                        }
                        else println("Unknown SetReply key: $key")
                    }
                    else -> println("[WARN] Unknown packet type: $id")
                }
            }
            catch (e: Exception) {
                println("Failed to parse packet: $entry")
                e.printStackTrace()
            }
        }
        return null
    }

    private fun handleHintData(items: JsonArray) {
        // hint data this player is entitled to
        for (item in items) {
            val theItem = item.asJsonObject
            hints.add(Hint(
                theItem.get("receiving_player").asInt,
                theItem.get("finding_player").asInt,
                theItem.get("location").asLong,
                theItem.get("item").asLong,
                theItem.get("found").asBoolean,
                theItem.get("item_flags").asInt,
                HintStatus.fromID(theItem.get("status").asInt)
            ))
        }
        MainFrame.updateHints()
    }

    private fun sendPacket(packet: JsonObject) {
        socket?.sendText("[${packet}]", true)
    }

    fun sendConnect(name: String, uuid: String, password: String?) {
        if (socket == null) {
            println("[WARN] Tried to send Connect before connecting")
            return
        }
        if (connectionState != 2) {
            println("[WARN] Tried to send Connect at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("Connect"))
        obj.add("game", JsonPrimitive(GAME_NAME))
        obj.add("name", JsonPrimitive(name))
        obj.add("uuid", JsonPrimitive(uuid))
        obj.add("password", if (password == null) null else JsonPrimitive(password))
        obj.add("version", AP_VERSION.toJson())
        obj.add("items_handling", JsonPrimitive(0b111)) // needs to receive own items
        val arr = JsonArray()
        obj.add("tags", arr)
        obj.add("slot_data", JsonPrimitive(true))
        sendPacket(obj)
    }

    fun requestSync() {
        // used when a mismatch is detected
        if (socket == null) {
            println("[WARN] Tried to send Sync before connecting")
            return
        }
        if (connectionState != 3) {
            println("[WARN] Tried to send Sync at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("Sync"))
        sendPacket(obj)
    }

    fun sendLocation(location: Long) = sendLocations(listOf(location))
    fun sendLocations(locations: List<Long>) {
        val locs = locations.filter { !MainFrame.LOCS.contains(it) }
        if (locs.isEmpty()) return // seen them all
        if (socket == null) {
            println("[WARN] Tried to send LocationChecks before connecting")
            return
        }
        if (connectionState != 3) {
            println("[WARN] Tried to send LocationChecks at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("LocationChecks"))
        val arr = JsonArray()
        locs.forEach { arr.add(it) }
        obj.add("locations", arr)
        sendPacket(obj)
    }

    fun requestLocationHint(location: Long) {
        // requests the item inside a local location rather than the location of a local item
        // could make use of this with hinting but need to check if it uses hint_points as normal or some other hint currency, if any
        if (socket == null) {
            println("[WARN] Tried to send CreateHints before connecting")
            return
        }
        if (connectionState != 3) {
            println("[WARN] Tried to send CreateHints at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("CreateHints"))
        val locs = JsonArray()
        locs.add(location)
        // TODO: infer the importance of the hint (10 is low priority, 30 is high priority) with a HintStatus
        obj.add("locations", locs)
        sendPacket(obj)
    }

    fun requestItemHint(item: Long) {
        // SetReply handles the response
        if (socket == null || connectionState != 3 || thisSlot == -1) return
        val thisSlot = slotList[thisSlot] ?: return
        val thisGame = slotItemMap[thisSlot.game] ?: return
        val thisItem = thisGame[item] ?: return
        sendChat("!hint $thisItem")
    }

    enum class ClientStatus(val id: Int) {
        UNKNOWN(0),
        CONNECTED(5),
        READY(10),
        PLAYING(20),
        GOAL(30)
    }
    fun updateStatus(status: ClientStatus) {
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("StatusUpdate"))
        obj.add("status", JsonPrimitive(status.id))
        sendPacket(obj)
    }

    fun sendChat(text: String) {
        if (socket == null) {
            println("[WARN] Tried to send Say before connecting")
            return
        }
        if (connectionState != 3) {
            println("[WARN] Tried to send Say at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("Say"))
        obj.add("text", JsonPrimitive(text))
        sendPacket(obj)
    }

    fun sendGet(text: String) {
        if (socket == null) {
            println("[WARN] Tried to send Get before connecting")
            return
        }
        if (connectionState != 3) {
            println("[WARN] Tried to send Get at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("Get"))
        val arr = JsonArray()
        arr.add(text)
        obj.add("keys", arr)
        sendPacket(obj)
    }

    fun sendGetDataPackage() {
        if (socket == null) {
            println("[WARN] Tried to send GetDataPackage before connecting")
            return
        }
        if (connectionState != 1) {
            println("[WARN] Tried to send GetDataPackage at wrong time")
            return
        }
        val obj = JsonObject()
        obj.add("cmd", JsonPrimitive("GetDataPackage"))
        sendPacket(obj)
    }
}
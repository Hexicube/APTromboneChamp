import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

fun main() {
    MainFrame()
}

enum class TrackStatus(val sortOrder: Int) {
    LOCKED(3),
    AVAILABLE(0),
    PLAYED(1),
    BEATEN(2)
}

class MainFrame : JFrame("Tromboner AP Client") {
    companion object {
        var INST: MainFrame? = null

        val ALL_FACTS = listOf(
            "It takes one-thousand workers a full year to produce a single trombone.",
            "The trombone is related to the trumpet (they are cousins).",
            "The trombone is not related to the French Horn (they are just friends).",
            "Some claim that Mozart's last words before dying were \"At least I got to use a trombone.\"",
            "A student's trombone generally costs between $100 and $300, but a professional trombone can cost over two billion dollars.",
            "To this day, scientists don't really know how a trombone makes sound.",
            "A professional trombone player is known as a \"tromboner\".",
            "Thirty-four countries have outlawed the use of the trombone. In six countries, playing trombone is punishable by death.",
            "Trombones contain \"spit valves\" that allow you to blow gobs of your nasty spit all over the floor.",
            "Without trombones, there could never have been \"ska\" music. Draw your own conclusions from this factoid.",
            "The average baboon can live to be over 300 years old.",
            "There are more baboons on Earth than humans.",
            "Prehistoric trombones were forty feet long and could weigh over six hundred pounds.",
            "Trombones do not float in water, so do not accidentally drop your trombone into the river last week.",
            "Cows love the sound of a trombone (because they are smart).",
            "Playing trombone in your apartment is a great way to make friends with your neighbors.",
            "Despite its name, the trombone does not have any bones.",
            "There are between 2 and 4 spiders living inside the average trombone.",
            "The first trombone was invented in 20,000,000 B.C.",
            "If you placed all of the trombones on Earth end-to-end, they would wrap around the solar system 4 times.",
            "There are more trombones on Earth than there are humans.",
            "The highest note playable on trombones is so high-pitched that only certain species of bats can hear it.",
            "The world record for \"Most Trombones Owned\" is held by Mike Brass of Omaha, Nebraska. He owns two trombones.",
            "It takes over three thousand tons of brass to produce a single trombone.",
            "In real life, there are over nine songs that feature a trombone."
        )
        val CUR_FACTS = ArrayList<String>()

        val SETTINGS = Settings(
            1, null, 3,
            3, 2, 0, DifficultyGatingMode.OFF,
            1, 10,
            true, true, true, true,
            emptyList()
        )

        var trackList = Track.getTrackList(SETTINGS)

        //val scrollPane = JScrollPane(TABLE)
        val scrollContents = JPanel()
        val scrollPane = JScrollPane(scrollContents)

        val chatHistoryPane = JPanel()
        val chatHistoryScroll = JScrollPane(chatHistoryPane)

        val ITEMS = ArrayList<Long>()
        val LOCS = ArrayList<Long>()

        val CONN = APConnectionManager()

        val connectSlot = JTextField("HexiTrombone")
        val connectPass = JPasswordField("")

        val neededRating = JLabel("Target Rating: -")
        val goalTarget = JLabel("Goal: -")

        fun getNumReductions() = ITEMS.count { it == 1001L }

        fun getCurRatingTarget(): Int {
            var rating = SETTINGS.startRating
            if (rating == SETTINGS.goalRating) return rating
            rating -= getNumReductions()
            return rating
        }

        fun getTrackStatus(track: Long): TrackStatus {
            if (!ITEMS.contains(track)) return TrackStatus.LOCKED
            if (SETTINGS.diffGating == DifficultyGatingMode.ON) {
                val diff = trackList.first { it.ID == track }.diff
                if (diff > SETTINGS.minDiff) {
                    if (!ITEMS.contains(1010L + diff)) return TrackStatus.LOCKED
                }
            }
            if (SETTINGS.diffGating == DifficultyGatingMode.PROG) {
                val needed = trackList.first { it.ID == track }.diff - SETTINGS.minDiff
                if (needed > 0) {
                    val numProg = ITEMS.count { it == 1011L }
                    if (numProg < needed) return TrackStatus.LOCKED
                }
            }
            if (track == SETTINGS.goalTrack?.ID) {
                if (SETTINGS.hotDogs > 0) {
                    val numHotDogs = ITEMS.count { it == 1004L }
                    if (numHotDogs < SETTINGS.hotDogs) return TrackStatus.LOCKED
                }
            }
            if (!LOCS.contains(track)) return TrackStatus.AVAILABLE
            if (!LOCS.contains(track + 1000L)) return TrackStatus.PLAYED
            return TrackStatus.BEATEN
        }

        fun expectedToBeatTrack(track: Track): Boolean {
            // assumes track is available
            if (SETTINGS.goalRating == SETTINGS.startRating) return true
            var required = SETTINGS.startRating - SETTINGS.goalRating
            val acquired = getNumReductions()
            if (SETTINGS.easyTrackGap == 0) return acquired >= required

            val maxDiff = Track.getTrackList(SETTINGS).maxOf { it.diff }
            val thisDiff = track.diff
            val gap = (maxDiff - thisDiff) / SETTINGS.easyTrackGap
            required -= gap
            return acquired >= required
        }

        fun checkWin() {
            if (SETTINGS.goalTracks == 0) {
                val goal = Track.getGoalTrack(SETTINGS)!!
                if (LOCS.contains(goal.ID + 1000L)) CONN.updateStatus(APConnectionManager.ClientStatus.GOAL)
            }
            else {
                var numBeat = 0
                for (track in trackList) {
                    if (LOCS.contains(track.ID + 1000L)) numBeat++
                }
                if (numBeat >= SETTINGS.goalTracks) CONN.updateStatus(APConnectionManager.ClientStatus.GOAL)
            }
        }

        fun update() {
            neededRating.text = "Target Rating: ${listOf("C", "B", "A", "S")[getCurRatingTarget()]}"

            for (entry in pinnedEntries) entry.update()
            for (entry in trackEntries) entry.value.update()
        }

        val pinnedEntries = emptyList<HintableEntry>().toMutableList()
        var trackEntries = emptyMap<Track, TrackEntry>().toSortedMap { a, b -> a.name.compareTo(b.name) }
        fun updateAllEntries(tracks: List<Track>) {
            // happens when connecting
            val goalTrack = Track.getGoalTrack(SETTINGS)
            goalTarget.text = if (goalTrack != null) "Goal: " + goalTrack.name
            else "Goal: ${trackList.count { getTrackStatus(it.ID) == TrackStatus.BEATEN }}/${SETTINGS.goalTracks}(${trackList.size}) tracks"

            val diffNeeded = SETTINGS.startRating - SETTINGS.goalRating
            if (diffNeeded > 0) pinnedEntries.add(object : GenericHintableEntry("Rank Reduction", 1001L) {
                override fun getItemTotal() = diffNeeded
            })

            if (SETTINGS.diffGating == DifficultyGatingMode.ON) {
                for (diff in (SETTINGS.minDiff + 1) .. SETTINGS.maxDiff) {
                    pinnedEntries.add(object : GenericHintableEntry("Difficulty $diff", 101L + diff) {
                        override fun getItemTotal() = 1
                    })
                }
            }
            if (SETTINGS.diffGating == DifficultyGatingMode.PROG) {
                val needed = SETTINGS.maxDiff - SETTINGS.minDiff
                pinnedEntries.add(object : GenericHintableEntry("Progressive Difficulty", 1011L) {
                    override fun getItemTotal() = needed
                })
            }

            if (SETTINGS.hotDogs > 0) {
                pinnedEntries.add(object : GenericHintableEntry("Hot Dog", 1004L) {
                    override fun getItemTotal() = SETTINGS.hotDogs
                })
            }

            trackList = tracks
            trackEntries.clear()
            for (track in tracks) {
                val entry = TrackEntry(track)
                trackEntries[track] = entry
            }
            sortTrackList()
        }

        fun sortTrackList() {
            scrollContents.removeAll()
            for (entry in pinnedEntries) scrollContents.add(entry)
            val order = TrackSortOrderList.dataModel.elements().toList()
            val goal = SETTINGS.goalTrack
            trackEntries = trackEntries.toSortedMap { a, b ->
                if (a == goal) -1
                else if (b == goal) 1
                else {
                    var diff = 0
                    for (type in order) {
                        diff = when (type) {
                            "DLC" -> a.DLC.compareTo(b.DLC)
                            "Name" -> a.name.compareTo(b.name)
                            "Difficulty" -> a.diff.compareTo(b.diff)
                            "Status" -> getTrackStatus(a.ID).sortOrder.compareTo(getTrackStatus(b.ID).sortOrder)
                            else -> 0
                        }
                        if (diff != 0) break
                    }
                    diff
                }
            }
            for (entry in trackEntries.entries) {
                if (getTrackStatus(entry.key.ID) != TrackStatus.BEATEN) scrollContents.add(entry.value)
            }
            scrollContents.revalidate()
            scrollContents.repaint()
        }

        fun updateHints() {
            // separate update function for just hints as they separately send data
            for (entry in pinnedEntries) entry.updateHints()
            for (entry in trackEntries) entry.value.updateHints()
        }

        fun addChatMessage(message: String, bg: Color = Color.WHITE) {
            if (chatHistoryPane.componentCount >= 100) chatHistoryPane.remove(chatHistoryPane.componentCount - 1)
            val panel = JPanel()
            panel.background = bg
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(HintableEntry.makeSpacer())
            panel.add(JLabel("<html><body style='width:215px;color:black'>$message</body></html>"))
            chatHistoryPane.add(panel, 0)
        }

        fun horizAlignText(text: JLabel): JPanel {
            val panel = JPanel()
            panel.isOpaque = false
            panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
            panel.add(Box.createHorizontalGlue())
            panel.add(text)
            panel.add(Box.createHorizontalGlue())
            return panel
        }
    }

    init {
        INST = this

        val content = JPanel()
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)

        val connectPane = JPanel()
        connectPane.layout = BoxLayout(connectPane, BoxLayout.X_AXIS)
        val connectPaneLeft = JPanel()
        connectPaneLeft.layout = BoxLayout(connectPaneLeft, BoxLayout.Y_AXIS)
        connectPane.add(connectPaneLeft)
        val connectIP = JTextField("archipelago.gg")
        connectPaneLeft.add(connectIP)
        val connectPaneRight = JPanel()
        connectPaneRight.layout = BoxLayout(connectPaneRight, BoxLayout.Y_AXIS)
        connectPane.add(connectPaneRight)
        val connectPort = JTextField("38281")
        connectPaneRight.add(connectPort)
        connectPaneRight.add(connectPass)

        connectPaneLeft.add(connectSlot)
        val connectButton = JButton("CONNECT")
        connectPane.add(connectButton)
        content.add(connectPane)

        connectButton.addActionListener {
            CONN.connect("${connectIP.text}:${connectPort.text}")
        }

        content.add(Box.createVerticalStrut(5))

        content.add(horizAlignText(JLabel("Sort Order")))
        content.add(TrackSortOrderList())

        content.add(Box.createVerticalStrut(5))

        val mainPane = JPanel()
        mainPane.layout = BoxLayout(mainPane, BoxLayout.X_AXIS)

        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        mainPane.add(scrollPane)
        content.add(mainPane)
        scrollPane.preferredSize = Dimension(297, 500)
        scrollContents.layout = BoxLayout(scrollContents, BoxLayout.Y_AXIS)
        scrollPane.verticalScrollBar.unitIncrement = 15

        content.add(Box.createVerticalStrut(5))

        content.add(horizAlignText(neededRating))
        content.add(horizAlignText(goalTarget))

        content.add(Box.createVerticalStrut(5))

        chatHistoryScroll.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        chatHistoryScroll.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        chatHistoryScroll.preferredSize = Dimension(297, 300)
        chatHistoryPane.layout = BoxLayout(chatHistoryPane, BoxLayout.Y_AXIS)
        chatHistoryScroll.verticalScrollBar.unitIncrement = 15
        content.add(chatHistoryScroll)

        contentPane = content
        isResizable = false
        pack()
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
    }
}

class TrackSortOrderList : JList<String>(dataModel) {
    companion object {
        val OPTS = listOf("DLC", "Name", "Difficulty", "Status")
        val dataModel = DefaultListModel<String>()
        init {
            dataModel.addAll(OPTS)
        }
    }

    init {
        val listener = TrackSortOrderListListener(this)
        addMouseListener(listener)
        addMouseMotionListener(listener)
        layoutOrientation = HORIZONTAL_WRAP
        visibleRowCount = 1
    }
}

class TrackSortOrderListListener(private val list: TrackSortOrderList) : MouseAdapter() {
    private var pressIdx = -1
    private var releaseIdx = -1

    override fun mousePressed(e: MouseEvent?) {
        if (e == null) return
        pressIdx = list.locationToIndex(e.point)
    }

    override fun mouseReleased(e: MouseEvent?) {
        if (e == null) return
        releaseIdx = list.locationToIndex(e.point)
        if (pressIdx != releaseIdx && pressIdx != -1 && releaseIdx != -1) {
            val entry = TrackSortOrderList.dataModel.elementAt(pressIdx)
            TrackSortOrderList.dataModel.removeElementAt(pressIdx)
            TrackSortOrderList.dataModel.insertElementAt(entry, releaseIdx)
            MainFrame.sortTrackList()
        }
    }

    override fun mouseDragged(e: MouseEvent?) {
        if (e == null) return
        mouseReleased(e)
        pressIdx = releaseIdx
    }
}

class HintLabel(prefix: String) : JPanel() {
    val prefixText = JLabel(prefix)
    val hintText = JLabel("-")

    init {
        isOpaque = false
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        add(prefixText)
        add(Box.createHorizontalGlue())
        add(hintText)
    }

    fun setItemData(data: Hint?) {
        if (data == null) {
            hintText.text = "-"
            toolTipText = null
        }
        else if (data.findingPlayer == MainFrame.CONN.thisSlot) {
            hintText.text = data.getItemLoc()
            toolTipText = null
        }
        else {
            hintText.text = "${data.getFinderName()}'s World"
            toolTipText = "${data.getFinderName()}'s ${data.getItemLoc()}"
        }
    }

    fun setLocationData(data: Hint?) {
        if (data == null) {
            hintText.text = "-"
            toolTipText = null
        }
        else if (data.receivingPlayer == MainFrame.CONN.thisSlot) {
            hintText.text = data.getItemName()
            toolTipText = null
        }
        else {
            hintText.text = "${data.getReceiverName()}'s ${data.status.text}"
            toolTipText = "${data.getReceiverName()}'s ${data.getItemName()} (${data.status.text})"
        }
    }
}

abstract class HintableEntry : JPanel() {
    abstract fun update()
    abstract fun updateHints()

    companion object {
        fun titleText(text: String): JLabel {
            val label = JLabel(text)
            label.font = Font(label.font.name, label.font.style, 15)
            return label
        }

        fun makeSpacer(): JSeparator {
            val spacer = JSeparator(SwingConstants.HORIZONTAL)
            spacer.foreground = Color.BLACK
            spacer.background = null
            spacer.border = null
            return spacer
        }
    }

    init {
        isOpaque = true
        border = CompoundBorder(LineBorder(Color.BLACK, 1, false), EmptyBorder(0, 2, 2, 2))
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }
}

abstract class GenericHintableEntry(val itemName: String, val itemID: Long) : HintableEntry() {
    val itemCount = titleText("0/0")
    val hintPanel = JPanel()

    val listener = object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent?) {
            if (e == null) return
            if (e.clickCount == 2) {
                val found = MainFrame.ITEMS.count { it == itemID }
                if (found >= getItemTotal()) return

                val allHints = MainFrame.CONN.findOwnHintItemList(itemID).filter { !it.found }
                val req = getItemTotal() - found - allHints.size
                if (req <= 0) return

                val pts = MainFrame.CONN.hintPoints
                val cost = MainFrame.CONN.hintCost
                if (pts < cost) {
                    JOptionPane.showMessageDialog(
                        MainFrame.INST, "Can't afford hint.\nCosts $cost, you have $pts.",
                        "Can't afford hint", JOptionPane.PLAIN_MESSAGE
                    )
                    return
                }
                val res = JOptionPane.showConfirmDialog(
                    MainFrame.INST, "Hint location for $itemName?\n${if (cost == 0) "Hints are free!" else "Costs $cost, you have $pts."}", "Hint $itemName",
                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null
                )
                if (res == JOptionPane.YES_OPTION) MainFrame.CONN.requestItemHint(itemID)
            }
        }
    }

    init {
        background = Color(.8f, 1f, 1f)

        val titlePane = JPanel()
        titlePane.isOpaque = false
        titlePane.layout = BoxLayout(titlePane, BoxLayout.X_AXIS)
        add(titlePane)

        titlePane.add(titleText(itemName))
        titlePane.add(Box.createHorizontalGlue())
        titlePane.add(itemCount)

        hintPanel.isOpaque = false
        hintPanel.layout = BoxLayout(hintPanel, BoxLayout.Y_AXIS)
        add(hintPanel)

        addMouseListener(listener)
    }

    abstract fun getItemTotal(): Int

    private val hintList = ArrayList<HintLabel>()
    override fun update() {
        val required = getItemTotal()
        hintPanel.removeAll()
        hintList.clear()
        val found = MainFrame.ITEMS.count { it == itemID }
        for (a in 0 until (required - found)) {
            val hint = HintLabel("")
            hint.addMouseListener(listener)
            hintList.add(hint)
            hintPanel.add(makeSpacer())
            hintPanel.add(hint)
        }
        itemCount.text = "$found/$required"
        updateHints()
    }

    override fun updateHints() {
        val hintInfo = MainFrame.CONN.findOwnHintItemList(itemID).filter { !it.found }
        for (a in 0 until hintList.size) {
            if (a >= hintInfo.size) hintList[a].setItemData(null)
            else hintList[a].setItemData(hintInfo[a])
        }
    }
}

class TrackEntry(val track: Track) : HintableEntry() {
    val hintItemSpacer = makeSpacer()
    val hintItem = HintLabel("Unlocks at")
    val playRewardSpacer = makeSpacer()
    val hintPlay = HintLabel("Play reward")
    val hintBeat = HintLabel("Beat reward")

    init {
        val titlePane = JPanel()
        titlePane.isOpaque = false
        titlePane.layout = BoxLayout(titlePane, BoxLayout.X_AXIS)
        add(titlePane)

        titlePane.add(titleText(track.name))
        titlePane.add(Box.createHorizontalGlue())
        titlePane.add(titleText(track.diff.toString() + "/10"))
        titlePane.add(Box.createHorizontalStrut(5))
        titlePane.add(titleText((track.len / 60).toString() + ":" + String.format("%02d", track.len % 60)))

        // hint data
        add(hintItemSpacer)
        add(hintItem)
        add(playRewardSpacer)
        add(hintPlay)
        add(makeSpacer())
        add(hintBeat)

        update()
        updateHints()

        val listener = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                if (e == null) return
                if (e.clickCount == 2) {
                    if (MainFrame.getTrackStatus(track.ID) == TrackStatus.LOCKED) {
                        val hint = MainFrame.CONN.findOwnHintItem(track.ID)
                        if (hint != null) return

                        val pts = MainFrame.CONN.hintPoints
                        val cost = MainFrame.CONN.hintCost
                        if (pts < cost) {
                            JOptionPane.showMessageDialog(
                                MainFrame.INST, "Can't afford hint.\nCosts $cost, you have $pts.",
                                "Can't afford hint", JOptionPane.PLAIN_MESSAGE
                            )
                        }
                        else {
                            val res = JOptionPane.showConfirmDialog(
                                MainFrame.INST, "Hint location for ${track.fullName}?\n${if (cost == 0) "Hints are free!" else "Costs $cost, you have $pts."}", "Hint Track",
                                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null
                            )
                            if (res == JOptionPane.YES_OPTION) MainFrame.CONN.requestItemHint(track.ID)
                        }
                        return
                    }

                    var res = JOptionPane.showOptionDialog(
                        MainFrame.INST, track.fullName, "Track Entry",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, arrayOf("D", "C", "B", "A", "S"), null)
                    if (res != JOptionPane.CLOSED_OPTION) {
                        MainFrame.CONN.sendLocation(track.ID)
                        res--
                        val need = MainFrame.getCurRatingTarget()
                        if (res >= need) {
                            MainFrame.CONN.sendLocation(track.ID + 1000)
                        }
                    }
                }
            }
        }
        addMouseListener(listener)
        // also add to hint labels as they are panels which block the event propagating
        hintItem.addMouseListener(listener)
        hintPlay.addMouseListener(listener)
        hintBeat.addMouseListener(listener)
    }

    override fun update() {
        val status = MainFrame.getTrackStatus(track.ID)
        if (status == TrackStatus.BEATEN) {
            // hide the entire entry
            MainFrame.scrollContents.remove(this)
            MainFrame.scrollContents.revalidate()
        }
        else if (status == TrackStatus.PLAYED) {
            // hide the play reward, it's been collected
            remove(playRewardSpacer)
            remove(hintPlay)
        }
        if (status != TrackStatus.LOCKED || (track == MainFrame.SETTINGS.goalTrack && MainFrame.ITEMS.contains(track.ID))) {
            // hide the item location, it's been collected
            remove(hintItemSpacer)
            remove(hintItem)
        }

        background = when (status) {
            TrackStatus.LOCKED -> Color(.7f, .7f, .7f)
            TrackStatus.AVAILABLE -> Color(1f, .7f, .7f)
            TrackStatus.PLAYED -> {
                if (MainFrame.expectedToBeatTrack(track)) Color(1f, .7f, .7f)
                else Color(1f, 1f, .5f)
            }
            TrackStatus.BEATEN -> Color(.7f, 1f, .7f) // should never be visible
        }
    }

    override fun updateHints() {
        hintItem.setItemData(MainFrame.CONN.findOwnHintItem(track.ID))
        hintPlay.setLocationData(MainFrame.CONN.findOwnHintLoc(track.ID))
        hintBeat.setLocationData(MainFrame.CONN.findOwnHintLoc(track.ID + 1000))
    }
}
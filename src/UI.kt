import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableCellRenderer

fun main() {
    MainFrame()
}

enum class TrackStatus {
    LOCKED, AVAILABLE, PLAYED, BEATEN
}

class MainFrame : JFrame("Tromboner AP Client") {
    companion object {
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
            0, 3, 3, 2, 1, 10, true, true, true, true
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
            //TABLE.revalidate()
            //TABLE.repaint()
            val target = getCurRatingTarget()
            val chars = listOf("C", "B", "A", "S")
            neededRating.text = "Target Rating: " + chars[target]
            if (target != SETTINGS.goalRating) neededRating.text += "(${chars[SETTINGS.goalRating]})"

            val goalTrack = Track.getGoalTrack(SETTINGS)
            goalTarget.text = if (goalTrack != null) "Goal: " + goalTrack.name
            else "Goal: ${trackList.count { getTrackStatus(it.ID) == TrackStatus.BEATEN }}/${SETTINGS.goalTracks}(${trackList.size}) tracks"

            for (entry in trackEntries) entry.value.update()
        }

        val trackEntries = emptyMap<Track, TrackEntry>().toMutableMap()
        fun updateTrackList(tracks: List<Track>) {
            // happens when connecting
            trackList = tracks
            trackEntries.clear()
            scrollContents.removeAll()
            for (track in tracks) {
                val entry = TrackEntry(track)
                trackEntries[track] = entry
                scrollContents.add(entry)
            }
        }

        fun updateHints() {
            // separate update function for just hints as they separately send data
            for (entry in trackEntries) entry.value.updateHints()
        }

        fun addChatMessage(message: String, bg: Color = Color.WHITE) {
            if (chatHistoryPane.componentCount >= 100) chatHistoryPane.remove(chatHistoryPane.componentCount - 1)
            val panel = JPanel()
            panel.background = bg
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(TrackEntry.makeSpacer())
            panel.add(JLabel("<html><body style='width:215px;color:black'>$message</body></html>"))
            chatHistoryPane.add(panel, 0)
        }
    }

    init {
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

        val mainPane = JPanel()
        mainPane.layout = BoxLayout(mainPane, BoxLayout.X_AXIS)

        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        mainPane.add(scrollPane)
        content.add(mainPane)
        scrollPane.preferredSize = Dimension(297, 500)
        scrollContents.layout = BoxLayout(scrollContents, BoxLayout.Y_AXIS)
        scrollPane.verticalScrollBar.unitIncrement = 15

        content.add(neededRating)
        content.add(goalTarget)
        // TODO: other info? number of reductions expected to exist?

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
            hintText.text = "${data.getFinderName()}'s world"
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
            hintText.text = "${data.getReceiverName()}'s world"
            toolTipText = "${data.getReceiverName()}'s ${data.getItemName()}"
        }
    }
}

class TrackEntry(val track: Track) : JPanel() {
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

    val hintItemSpacer = makeSpacer()
    val hintItem = HintLabel("Unlocks at")
    val playRewardSpacer = makeSpacer()
    val hintPlay = HintLabel("Play reward")
    val hintBeat = HintLabel("Beat reward")

    init {
        isOpaque = true
        border = CompoundBorder(LineBorder(Color.BLACK, 1, false), EmptyBorder(0, 2, 2, 2))
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

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
                        if (hint != null) {
                            println(hint)
                            return
                        }
                        // TODO: check if hint exists
                        val pts = MainFrame.CONN.hintPoints
                        val cost = MainFrame.CONN.hintCost
                        if (pts < cost) {
                            JOptionPane.showMessageDialog(
                                null, "Can't afford hint.\nCosts $cost, you have $pts.",
                                "Can't afford hint", JOptionPane.PLAIN_MESSAGE
                            )
                        }
                        else {
                            val res = JOptionPane.showConfirmDialog(
                                null, "Hint location for ${track.fullName}?\n${if (cost == 0) "Hints are free!" else "Costs $cost, you have $pts."}", "Hint Track",
                                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null
                            )
                            if (res == JOptionPane.YES_OPTION) MainFrame.CONN.requestItemHint(track.name)
                        }
                        return
                    }

                    var res = JOptionPane.showOptionDialog(
                        null, track.fullName, "Track Entry",
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

    fun update() {
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
        if (status != TrackStatus.LOCKED) {
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

    fun updateHints() {
        hintItem.setItemData(MainFrame.CONN.findOwnHintItem(track.ID))
        hintPlay.setLocationData(MainFrame.CONN.findOwnHintLoc(track.ID))
        hintBeat.setLocationData(MainFrame.CONN.findOwnHintLoc(track.ID + 1000))
    }
}

/*class TrackTableModel(tracks: List<Track>) : AbstractTableModel() {
    public var trackList: List<Track> = tracks

    override fun getRowCount() = trackList.size
    override fun getColumnCount() = 4

    override fun getColumnName(column: Int): String? {
        return when (column) {
            0 -> "Track"
            1 -> "Diff."
            2 -> "Len"
            3 -> "Status"
            else -> null
        }
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        if (rowIndex < 0 || rowIndex >= rowCount || columnIndex < 0 || columnIndex >= columnCount) return null
        val track = trackList[rowIndex]
        return when (columnIndex) {
            0 -> track.name
            1 -> track.diff.toString() + "/10"
            2 -> (track.len / 60).toString() + ":" + String.format("%02d", track.len % 60)
            3 -> {
                val status = MainFrame.getTrackStatus(track.ID)
                when (status) {
                    TrackStatus.LOCKED -> "Locked"
                    TrackStatus.AVAILABLE -> "Available"
                    TrackStatus.PLAYED -> "Played"
                    TrackStatus.BEATEN -> "Beaten"
                }
            }
            else -> null
        }
    }
}

class TrackTableRenderer : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component? {
        if (table == null) return null
        val comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        val track = MainFrame.TABLE.MODEL.trackList[row]
        val status = MainFrame.getTrackStatus(track.ID)
        val col = when (status) {
            TrackStatus.LOCKED -> Color.GRAY
            TrackStatus.AVAILABLE -> Color.RED
            TrackStatus.PLAYED -> {
                if (MainFrame.expectedToBeatTrack(track)) Color.RED
                else Color.YELLOW
            }
            TrackStatus.BEATEN -> Color.GREEN
        }
        comp.background = col
        return comp
    }
}

class TrackTable : JTable() {
    val MODEL = TrackTableModel(Track.getTrackList(MainFrame.SETTINGS))

    init {
        model = MODEL
        autoResizeMode = AUTO_RESIZE_OFF
        tableHeader.reorderingAllowed = false
        columnModel.getColumn(0).preferredWidth = 150
        columnModel.getColumn(1).preferredWidth = 40
        columnModel.getColumn(2).preferredWidth = 30
        columnModel.getColumn(3).preferredWidth = 60
        columnModel.getColumn(0).cellRenderer = TrackTableRenderer()
        columnModel.getColumn(1).cellRenderer = TrackTableRenderer()
        columnModel.getColumn(2).cellRenderer = TrackTableRenderer()
        columnModel.getColumn(3).cellRenderer = TrackTableRenderer()

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                if (e == null) return
                if (e.clickCount == 2) {
                    val point = e.point
                    val row = rowAtPoint(point)
                    val track = MODEL.trackList[row]
                    if (MainFrame.getTrackStatus(track.ID) == TrackStatus.LOCKED) {
                        // testing: hint it
                        MainFrame.CONN.requestItemHint(track.name)
                        return
                    }

                    var res = JOptionPane.showOptionDialog(
                        null, track.fullName, "Track Entry",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, arrayOf("D", "C", "B", "A", "S"), null)
                    if (res != JOptionPane.CLOSED_OPTION) {
                        if (!MainFrame.LOCS.contains(track.ID)) {
                            MainFrame.CONN.sendLocation(track.ID)
                            MainFrame.LOCS.add(track.ID)
                            MainFrame.TABLE.repaint()
                        }
                        res--
                        val need = MainFrame.getCurRatingTarget()
                        if (res >= need) {
                            if (!MainFrame.LOCS.contains(track.ID + 1000)) {
                                MainFrame.CONN.sendLocation(track.ID + 1000)
                                MainFrame.LOCS.add(track.ID + 1000)
                                MainFrame.TABLE.repaint()
                                MainFrame.checkWin()
                            }
                        }
                    }
                }
            }
        })
    }
}*/
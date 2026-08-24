import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class DelayedCall extends JFrame {

    // The engine does all of the actual simulation work.
    private TelephoneEngine engine;

    // ---------- Fields that show the current state ----------
    private final JTextField[] lineFields = new JTextField[TelephoneEngine.NUM_LINES];
    private JTextField maxLinksField, inUseLinksField;
    private JTextField clockField;
    private JTextField fromField, toField, lengthField, arrivalField;
    private final JTextField[] progressFrom = new JTextField[TelephoneEngine.MAX_LINKS];
    private final JTextField[] progressTo = new JTextField[TelephoneEngine.MAX_LINKS];
    private final JTextField[] progressEnd = new JTextField[TelephoneEngine.MAX_LINKS];
    private JTextArea delayedQueueArea;
    private JTextField processedField, completedField, blockedField, busyField;
    private JTextArea logArea;

    public DelayedCall() {
        super("Delayed Call Simulation System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 970);
        setLocationRelativeTo(null);
        setLayout(null);
        UITheme.styleFrame(this);

        engine = new TelephoneEngine(true);   // true = Delayed Call mode

        buildLinesPanel();
        buildLinksPanel();
        buildClockPanel();
        buildNextCallPanel();
        buildCallsInProgressPanel();
        buildDelayedQueuePanel();
        buildCountersPanel();
        buildButtonsAndLog();

        refreshDisplay();
    }

    // ================================================================
    //  BUILDING EACH BOXED PANEL
    //  (same grid as LostCall.java, with one extra row added for the
    //   DELAYED CALLS box - see the guide document for the diagram)
    // ================================================================

    private JPanel titledPanel(String title, int x, int y, int w, int h) {
        JPanel panel = UITheme.titledPanel(title, x, y, w, h);
        add(panel);
        return panel;
    }

    /** Column A (x = 20-200): the box showing all 8 lines: 0 = free, 1 = busy. */
    private void buildLinesPanel() {
        JPanel panel = titledPanel("LINES  (0 = free, 1 = busy)", 20, 20, 180, 340);
        panel.setLayout(new GridLayout(TelephoneEngine.NUM_LINES, 2, 6, 6));

        for (int i = 0; i < TelephoneEngine.NUM_LINES; i++) {
            JLabel lineLabel = new JLabel("Line " + (i + 1) + ":");
            lineLabel.setFont(UITheme.FONT_LABEL);
            panel.add(lineLabel);
            lineFields[i] = UITheme.valueField();
            panel.add(lineFields[i]);
        }
    }

    /** Column B, top (x = 220-400, y = 20-140): links available vs in use. */
    private void buildLinksPanel() {
        JPanel panel = titledPanel("LINKS", 220, 20, 180, 120);
        panel.setLayout(new GridLayout(2, 2, 8, 8));

        maxLinksField = UITheme.valueField();
        maxLinksField.setText(String.valueOf(TelephoneEngine.MAX_LINKS));

        inUseLinksField = UITheme.valueField();

        JLabel maxLbl = new JLabel("Max:");
        JLabel inUseLbl = new JLabel("In use:");
        maxLbl.setFont(UITheme.FONT_LABEL);
        inUseLbl.setFont(UITheme.FONT_LABEL);

        panel.add(maxLbl);
        panel.add(maxLinksField);
        panel.add(inUseLbl);
        panel.add(inUseLinksField);
    }

    /** Column B, below LINKS (x = 220-400, y = 150-240): the simulation clock. */
    private void buildClockPanel() {
        JPanel panel = titledPanel("CLOCK", 220, 150, 180, 90);
        panel.setLayout(new GridLayout(1, 1, 8, 8));

        clockField = UITheme.valueField();
        clockField.setFont(UITheme.FONT_CLOCK);
        clockField.setForeground(UITheme.PRIMARY_DARK);
        panel.add(clockField);
    }

    /** Column C, top (x = 420-860, y = 20-140): the call that is about to arrive next. */
    private void buildNextCallPanel() {
        JPanel panel = titledPanel("NEXT CALL TO ARRIVE", 420, 20, 440, 120);
        panel.setLayout(new GridLayout(1, 4, 10, 8));

        fromField = UITheme.valueField();
        toField = UITheme.valueField();
        lengthField = UITheme.valueField();
        arrivalField = UITheme.valueField();

        JPanel fromBox = labeledBox("FROM", fromField);
        JPanel toBox = labeledBox("TO", toField);
        JPanel lengthBox = labeledBox("LENGTH", lengthField);
        JPanel arrivalBox = labeledBox("ARRIVAL TIME", arrivalField);

        panel.add(fromBox);
        panel.add(toBox);
        panel.add(lengthBox);
        panel.add(arrivalBox);
    }

    /** Small helper: a label stacked above one field. */
    private JPanel labeledBox(String labelText, JTextField field) {
        JPanel box = new JPanel(new BorderLayout(2, 2));
        box.setBackground(UITheme.PANEL_BG);
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_MUTED);
        box.add(label, BorderLayout.NORTH);
        box.add(field, BorderLayout.CENTER);
        return box;
    }

    /** A small bold column header used above the calls-in-progress grid. */
    private JLabel headerLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_MUTED);
        return label;
    }

    /** Column C, below NEXT CALL (x = 420-860, y = 150-360): every call currently connected. */
    private void buildCallsInProgressPanel() {
        JPanel panel = titledPanel("CALLS IN PROGRESS  (max " + TelephoneEngine.MAX_LINKS + " at once)", 420, 150, 440, 210);
        panel.setLayout(new GridLayout(TelephoneEngine.MAX_LINKS + 1, 3, 6, 6));

        panel.add(headerLabel("FROM"));
        panel.add(headerLabel("TO"));
        panel.add(headerLabel("ENDS AT"));

        for (int i = 0; i < TelephoneEngine.MAX_LINKS; i++) {
            progressFrom[i] = UITheme.valueField();
            progressTo[i] = UITheme.valueField();
            progressEnd[i] = UITheme.valueField();
            panel.add(progressFrom[i]);
            panel.add(progressTo[i]);
            panel.add(progressEnd[i]);
        }
    }

    /**
     * Column C, below CALLS IN PROGRESS (x = 420-860, y = 370-520): every call
     * that could not connect yet and is waiting in the queue. Unlike the fixed
     * 3 slots above, this list can be any length, so a scrollable text area is
     * used instead of a fixed grid of fields.
     */
    private void buildDelayedQueuePanel() {
        JPanel panel = titledPanel("DELAYED CALLS  (waiting queue)", 420, 370, 440, 150);
        panel.setLayout(new BorderLayout());

        delayedQueueArea = new JTextArea();
        delayedQueueArea.setEditable(false);
        UITheme.styleLog(delayedQueueArea);
        panel.add(new JScrollPane(delayedQueueArea), BorderLayout.CENTER);
    }

    /** Full width, below everything else (x = 20-860, y = 530-620): the four running totals. */
    private void buildCountersPanel() {
        JPanel panel = titledPanel("CALL COUNTERS", 20, 530, 840, 90);
        panel.setLayout(new GridLayout(2, 4, 8, 4));

        processedField = UITheme.valueField();
        completedField = UITheme.valueField();
        blockedField = UITheme.valueField();
        busyField = UITheme.valueField();
        completedField.setForeground(UITheme.FREE_FG);
        blockedField.setForeground(UITheme.BUSY_FG);
        busyField.setForeground(UITheme.BUSY_FG);

        panel.add(headerLabel("Processed"));
        panel.add(headerLabel("Completed"));
        panel.add(headerLabel("Blocked"));
        panel.add(headerLabel("Busy"));
        panel.add(processedField);
        panel.add(completedField);
        panel.add(blockedField);
        panel.add(busyField);
    }

    /** The "Next Event" / "Restart" buttons and the plain-English event log below them. */
    private javax.swing.Timer autoTimer;

    private void buildButtonsAndLog() {
        JButton nextEventButton = UITheme.button("Next Event ->", UITheme.PRIMARY);
        nextEventButton.setBounds(20, 630, 150, 38);
        nextEventButton.addActionListener(e -> {
            String message = engine.runOneEvent();
            appendLog(message);
            refreshDisplay();
        });
        add(nextEventButton);

        JButton autoButton = UITheme.button("Auto Run ▶", UITheme.RUN_BG);
        autoButton.setBounds(180, 630, 150, 38);
        autoButton.addActionListener(e -> {
            if (autoTimer != null && autoTimer.isRunning()) {
                autoTimer.stop();
                autoButton.setText("Auto Run ▶");
                autoButton.setBackground(UITheme.RUN_BG);
            } else {
                autoTimer = new javax.swing.Timer(700, ev -> {
                    appendLog(engine.runOneEvent());
                    refreshDisplay();
                });
                autoTimer.start();
                autoButton.setText("Pause ❚❚");
                autoButton.setBackground(UITheme.PAUSE_BG);
            }
        });
        add(autoButton);

        JButton restartButton = UITheme.button("Restart", UITheme.RESTART_BG);
        restartButton.setBounds(340, 630, 150, 38);
        restartButton.addActionListener(e -> {
            if (autoTimer != null) {
                autoTimer.stop();
                autoButton.setText("Auto Run ▶");
                autoButton.setBackground(UITheme.RUN_BG);
            }
            engine = new TelephoneEngine(true);
            logArea.setText("");
            appendLog("Simulation restarted.\n");
            refreshDisplay();
        });
        add(restartButton);

        logArea = new JTextArea();
        logArea.setEditable(false);
        UITheme.styleLog(logArea);
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Event Log"));
        scroll.setBounds(20, 680, 840, 250);
        add(scroll);
    }

    private void refreshDisplay() {
        for (int i = 0; i < TelephoneEngine.NUM_LINES; i++) {
            UITheme.paintLineStatus(lineFields[i], engine.lineStatus[i]);
        }

        inUseLinksField.setText(String.valueOf(engine.linksInUse));
        clockField.setText(String.valueOf(engine.clock));

        fromField.setText(String.valueOf(engine.nextFrom));
        toField.setText(String.valueOf(engine.nextTo));
        lengthField.setText(String.valueOf(engine.nextLength));
        arrivalField.setText(String.valueOf(engine.nextArrivalTime));

        for (int i = 0; i < TelephoneEngine.MAX_LINKS; i++) {
            if (i < engine.callsInProgress.size()) {
                TelephoneEngine.Call c = engine.callsInProgress.get(i);
                progressFrom[i].setText(String.valueOf(c.from));
                progressTo[i].setText(String.valueOf(c.to));
                progressEnd[i].setText(String.valueOf(c.endTime));
            } else {
                progressFrom[i].setText("-");
                progressTo[i].setText("-");
                progressEnd[i].setText("-");
            }
        }

        StringBuilder queueText = new StringBuilder();
        if (engine.delayedQueue.isEmpty()) {
            queueText.append("(no calls waiting)\n");
        } else {
            for (TelephoneEngine.Call c : engine.delayedQueue) {
                queueText.append("Line ").append(c.from).append(" <-> Line ").append(c.to)
                        .append("   length ").append(c.length).append("s\n");
            }
        }
        delayedQueueArea.setText(queueText.toString());

        processedField.setText(String.valueOf(engine.processed));
        completedField.setText(String.valueOf(engine.completed));
        blockedField.setText(String.valueOf(engine.blocked));
        busyField.setText(String.valueOf(engine.busy));
    }

    // Keeps the on-screen log from growing forever during a long click-through
    // session. Without this cap, logArea's internal Document keeps every line
    // ever appended, which slowly bloats memory and makes the Swing text
    // component more expensive to repaint the longer the simulation runs.
    private static final int MAX_LOG_CHARS = 20000;

    private void appendLog(String text) {
        logArea.append(text);
        trimLogIfTooLong();
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void trimLogIfTooLong() {
        int len = logArea.getDocument().getLength();
        if (len > MAX_LOG_CHARS) {
            try {
                logArea.getDocument().remove(0, len - MAX_LOG_CHARS);
            } catch (javax.swing.text.BadLocationException ignored) {
                // Can't happen: the offsets above are always within bounds.
            }
        }
    }
}

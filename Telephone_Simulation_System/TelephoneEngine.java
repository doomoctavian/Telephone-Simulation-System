import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class TelephoneEngine {

    public static final int NUM_LINES = 8;   // lines numbered 1 to 8
    public static final int MAX_LINKS = 3;   // only 3 calls can be connected at once

    // lineStatus[i] = 0 means line (i+1) is FREE, 1 means BUSY
    public int[] lineStatus = new int[NUM_LINES];

    public int linksInUse = 0;     // how many links are currently being used
    public int clock = 1000;       // simulation clock, starts at an arbitrary time
    public final boolean delayedMode;   // false = Lost Call, true = Delayed Call

    // ---------- Counters for the report ----------
    public int processed = 0;
    public int completed = 0;
    public int blocked = 0;
    public int busy = 0;

    private final Random random = new Random();

    /** A "Call" remembers who is calling whom, for how long, and when it ends. */
    public static class Call {
        public final int from, to, length;
        public int endTime;
        public Call(int from, int to, int length, int endTime) {
            this.from = from;
            this.to = to;
            this.length = length;
            this.endTime = endTime;
        }
    }

    public final ArrayList<Call> callsInProgress = new ArrayList<>();
    public final Queue<Call> delayedQueue = new LinkedList<>();   // used only when delayedMode is true

    // Attributes of the call that has NOT arrived yet (the "next" call)
    public int nextFrom, nextTo, nextLength, nextArrivalTime;

    public TelephoneEngine(boolean delayedMode) {
        this.delayedMode = delayedMode;
        generateNextCall();   // bootstrap: create the very first call that will arrive
    }

    /**
     * Creates the random attributes of the next call that will arrive:
     * who it is from, who it is to, how long it will last, and at what
     * clock time it will arrive. This is the "bootstrap method" used to
     * generate calls in the classic telephone-system example.
     */
    public void generateNextCall() {
        nextFrom = random.nextInt(NUM_LINES) + 1;           // a line from 1-8
        do {
            nextTo = random.nextInt(NUM_LINES) + 1;          // a different line
        } while (nextTo == nextFrom);

        nextLength = 20 + random.nextInt(150);               // lasts 20-169 seconds
        nextArrivalTime = clock + 5 + random.nextInt(30);    // arrives 5-34 sec later
    }

    /** A call can be connected only if both lines are free AND a link is free. */
    public boolean canConnect(int from, int to) {
        return lineStatus[from - 1] == 0
                && lineStatus[to - 1] == 0
                && linksInUse < MAX_LINKS;
    }

    /** Marks both lines busy, uses up one link, and adds the call to the list. */
    private void connectCall(int from, int to, int length) {
        lineStatus[from - 1] = 1;
        lineStatus[to - 1] = 1;
        linksInUse++;
        callsInProgress.add(new Call(from, to, length, clock + length));
    }

    /** Returns the end time of the call that will finish soonest. */
    private int earliestFinishTime() {
        int earliest = Integer.MAX_VALUE;
        for (Call c : callsInProgress) {
            if (c.endTime < earliest) {
                earliest = c.endTime;
            }
        }
        return earliest;
    }

    /**
     * Removes and finishes every call whose end time equals the clock.
     */
    private void finishCompletedCalls(StringBuilder log) {
        ArrayList<Call> finishedCalls = new ArrayList<>();
        for (Call c : callsInProgress) {
            if (c.endTime == clock) {
                finishedCalls.add(c);
            }
        }

        for (Call c : finishedCalls) {
            lineStatus[c.from - 1] = 0;   // free up both lines
            lineStatus[c.to - 1] = 0;
            linksInUse--;                 // free up the link
            completed++;
            callsInProgress.remove(c);

            log.append("Call between Line ").append(c.from)
                    .append(" and Line ").append(c.to).append(" has ENDED.\n");

            // In delayed-call mode, a waiting call may now be able to connect.
            if (delayedMode) {
                tryConnectAWaitingCall(log);
            }
        }
    }

    /** Tries to connect one waiting call, if any waiting call can now be served. */
    private void tryConnectAWaitingCall(StringBuilder log) {
        Iterator<Call> it = delayedQueue.iterator();
        while (it.hasNext()) {
            Call waiting = it.next();
            if (canConnect(waiting.from, waiting.to)) {
                connectCall(waiting.from, waiting.to, waiting.length);
                it.remove();
                log.append("Waiting call Line ").append(waiting.from)
                        .append(" <-> Line ").append(waiting.to)
                        .append(" is now CONNECTED.\n");
                break;   // only one call is connected per finished call
            }
        }
    }

    /** Handles the arrival of the "next" call that was generated earlier. */
    private void processArrival(StringBuilder log) {
        processed++;

        if (canConnect(nextFrom, nextTo)) {
            connectCall(nextFrom, nextTo, nextLength);
            log.append("New call Line ").append(nextFrom).append(" -> Line ").append(nextTo)
                    .append(" CONNECTED (ends at ").append(clock + nextLength).append(").\n");
        } else {
            // Decide WHY the call could not be connected.
            if (lineStatus[nextFrom - 1] == 1 || lineStatus[nextTo - 1] == 1) {
                busy++;      // the other person's line was already busy
                log.append("New call Line ").append(nextFrom).append(" -> Line ").append(nextTo)
                        .append(" is BUSY.\n");
            } else {
                blocked++;   // both lines were free, but no link was available
                log.append("New call Line ").append(nextFrom).append(" -> Line ").append(nextTo)
                        .append(" is BLOCKED (no free link).\n");
            }

            if (delayedMode) {
                // The call is not lost - it waits in the queue instead.
                delayedQueue.add(new Call(nextFrom, nextTo, nextLength, -1));
                log.append("   -> placed in the waiting queue.\n");
            }
        }

        generateNextCall();   // schedule the following call (bootstrap again)
    }

    /**
     * Runs exactly one event of the simulation (a call finishing and/or the
     * next call arriving, whichever comes first) and returns a short plain
     * -English description of what just happened, for the on-screen log.
     */
    public String runOneEvent() {
        StringBuilder log = new StringBuilder();

        int nextFinishTime = earliestFinishTime();
        int nextEventTime = Math.min(nextFinishTime, nextArrivalTime);
        clock = nextEventTime;

        finishCompletedCalls(log);

        if (clock == nextArrivalTime) {
            processArrival(log);
        }

        return log.toString();
    }
}

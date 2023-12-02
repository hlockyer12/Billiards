package PoolGame;

/** Holds static final data. */
public class Config {
    private static final double TABLEBUFFER = 50;
    private static final double TABLEEDGE = 10;

    private static final double SCOREBOARDBUFFER = 75;

    /**
     * Returns the buffer around the table.
     * 
     * @return buffer
     */
    public static double getTableBuffer() {
        return TABLEBUFFER;
    }

    /*********
     * Returns the edge of the table.
     * 
     * @return edge length.
     */
    public static double getTableEdge() {
        return TABLEEDGE;
    }

    public static double getScoreboardbuffer() {
        return SCOREBOARDBUFFER;
    }
}

package PoolGame.strategy;

public class BrownStrategy extends PocketStrategy {
    /** Creates a new brown strategy. */
    public BrownStrategy() {
        this.lives = 3;
    }

    public BrownStrategy(PocketStrategy strat) {
        this.lives = strat.getLives();
    }

    public void reset() {
        this.lives = 3;
    }
}

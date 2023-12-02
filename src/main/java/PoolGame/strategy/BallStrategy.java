package PoolGame.strategy;

public class BallStrategy extends PocketStrategy {
    /**
     * Creates a new ball strategy.
     */
    public BallStrategy() {
        this.lives = 1;
    }

    public BallStrategy(PocketStrategy strat) {
        this.lives = strat.getLives();
    }

    public void reset() {
        this.lives = 1;
    }
}

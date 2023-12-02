package PoolGame;

import PoolGame.objects.Ball;
import PoolGame.strategy.BallStrategy;
import PoolGame.strategy.BlueStrategy;
import PoolGame.strategy.BrownStrategy;
import javafx.scene.paint.Paint;

import java.util.List;

public class Scoreboard {

    private List<Ball> balls;

    private final int totalScore;

    public Scoreboard(List<Ball> balls) {
        this.balls = balls;
        int score = 0;
        for (Ball b : this.balls) {
            score = score + (b.getPoints() * b.getStrategy().getLives());
        }
        this.totalScore = score;
    }

    public int getTotalScore() {
        return this.totalScore;
    }

    /**
     * Calculates the current score of the game
     *
     * @return int
     */
    public int calculateScore() {
        int score = 0;
        for (Ball b : balls) {
            score = score + (b.getPoints() * b.getStrategy().getLives());
        }
        return this.totalScore - score;
    }
}

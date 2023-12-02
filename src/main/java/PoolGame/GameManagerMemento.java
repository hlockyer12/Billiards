package PoolGame;

import PoolGame.objects.Ball;
import PoolGame.objects.Table;

import java.util.List;

public final class GameManagerMemento {

    private final List<Ball> balls;
    private final Table table;
    private final int score;

    private final long elapsedTime;


    public GameManagerMemento(Table table, List<Ball> balls, int score, long elapsedTime) {
        this.table = table;
        this.balls = balls;
        this.score = score;
        this.elapsedTime = elapsedTime;
    }


    public Table getTable() {
        return table;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public int getScore() {
        return score;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

}

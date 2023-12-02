package PoolGame.objects;

import PoolGame.strategy.BrownStrategy;
import PoolGame.strategy.PocketStrategy;
import PoolGame.strategy.BallStrategy;
import PoolGame.strategy.BlueStrategy;

/** Builds pool balls. */
public class PoolBallBuilder implements BallBuilder {
    // Required Parameters
    private String colour;
    private double xPosition;
    private double yPosition;
    private double xVelocity;
    private double yVelocity;
    private double mass;

    // Variable Parameters
    private boolean isCue = false;
    private PocketStrategy strategy;

    private int points;

    @Override
    public void setColour(String colour) {
        this.colour = colour;
    };

    @Override
    public void setxPos(double xPosition) {
        this.xPosition = xPosition;
    };

    @Override
    public void setyPos(double yPosition) {
        this.yPosition = yPosition;
    };

    @Override
    public void setxVel(double xVelocity) {
        this.xVelocity = xVelocity;
    };

    @Override
    public void setyVel(double yVelocity) {
        this.yVelocity = yVelocity;
    };

    @Override
    public void setMass(double mass) {
        this.mass = mass;
    };

    /**
     * Builds the ball.
     * 
     * @return ball
     */
    public Ball build() {
        if (colour.equals("white")) {
            isCue = true;
            strategy = new BallStrategy();
            points = 0;
        } else if (colour.equals("red")) {
            points = 1;
            strategy = new BallStrategy();
        } else if (colour.equals("yellow")) {
            points = 2;
            strategy = new BallStrategy();
        } else if (colour.equals("green")) {
            points = 3;
            strategy = new BlueStrategy();
        } else if (colour.equals("brown")) {
            points = 4;
            strategy = new BrownStrategy();
        } else if (colour.equals("blue")) {
            points = 5;
            strategy = new BlueStrategy();
        } else if (colour.equals("purple")) {
            points = 6;
            strategy = new BlueStrategy();
        } else if (colour.equals("black")) {
            points = 7;
            strategy = new BrownStrategy();
        } else if (colour.equals("orange")) {
            points = 8;
            strategy = new BallStrategy();
        }



        return new Ball(colour, xPosition, yPosition, xVelocity, yVelocity, mass, isCue, strategy, points);
    }
}

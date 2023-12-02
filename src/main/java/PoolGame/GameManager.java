package PoolGame;

import PoolGame.config.BallReaderFactory;
import PoolGame.config.Reader;
import PoolGame.config.ReaderFactory;
import PoolGame.config.TableReaderFactory;
import PoolGame.objects.*;
import java.util.ArrayList;

import java.util.List;

import javafx.geometry.Point2D;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.geometry.VPos;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Line;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.paint.Paint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;

/**
 * Controls the game interface; drawing objects, handling logic and collisions.
 */
public class GameManager {
    private Table table;
    private List<Ball> balls = new ArrayList<Ball>();
    private Line cue = new Line();
    private Line cueDraw;
    private boolean cueSet = false;
    private boolean cueActive = false;
    private boolean isCueDrawn = false;
    private boolean winFlag = false;
    private boolean gameStartedFlag = false;
    private int score = 0;

    private final double TABLEBUFFER = Config.getTableBuffer();
    private final double TABLEEDGE = Config.getTableEdge();
    private final double SCOREBOARDBUFFER = Config.getScoreboardbuffer();
    private final double FORCEFACTOR = 0.1;
    private Scene scene;
    private GraphicsContext gc;
    private List<GameManagerMemento> mementos = new ArrayList<>();
    private Scoreboard scoreBoard;
    private long startTime;
    private long elapsedTime;

    /**
     * Initialises timeline and cycle count.
     */
    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17),
                t -> this.draw()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Builds GameManager properties such as initialising pane, canvas,
     * graphicscontext, and setting events related to clicks.
     */
    public void buildManager() {
        Pane pane = new Pane();
        setClickEvents(pane);
        this.scene = new Scene(pane, table.getxLength() + TABLEBUFFER * 2, table.getyLength() + TABLEBUFFER * 2 + SCOREBOARDBUFFER);
        setKeyBoardEvents(this.scene);
        Canvas canvas = new Canvas(table.getxLength() + TABLEBUFFER * 2, table.getyLength() + TABLEBUFFER * 2 + SCOREBOARDBUFFER);
        gc = canvas.getGraphicsContext2D();
        pane.getChildren().add(canvas);
    }

    /**
     * Draws all relevant items - table, cue, balls, pockets - onto Canvas.
     * Used Exercise 6 as reference.
     */
    private void draw() {
        if (!gameStartedFlag) {
            gc.setFont(new Font("Impact", 40));
            gc.setTextBaseline(VPos.CENTER);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(Paint.valueOf("purple"));
            gc.fillText("Please select a difficulty", (table.getxLength()+ TABLEBUFFER*2)/2, (table.getyLength()+ TABLEBUFFER*2)/2);
        } else {

            tick();

            // Fill in background
            gc.setFill(Paint.valueOf("white"));
            gc.fillRect(0, 0, table.getxLength() + TABLEBUFFER * 2, table.getyLength() + TABLEBUFFER * 2);

            // Fill in edges
            gc.setFill(Paint.valueOf("brown"));
            gc.fillRect(TABLEBUFFER - TABLEEDGE, TABLEBUFFER - TABLEEDGE, table.getxLength() + TABLEEDGE * 2,
                    table.getyLength() + TABLEEDGE * 2);

            // Fill in Table
            gc.setFill(table.getColour());
            gc.fillRect(TABLEBUFFER, TABLEBUFFER, table.getxLength(), table.getyLength());

            // Fill in Pockets
            for (Pocket pocket : table.getPockets()) {
                gc.setFill(Paint.valueOf("black"));
                gc.fillOval(pocket.getxPos() - pocket.getRadius(), pocket.getyPos() - 0.5 * pocket.getRadius(),
                        pocket.getRadius() * 2, pocket.getRadius() * 2);
            }

            // Cue
            if (this.cue != null && cueActive) {
                //gc.strokeLine(cue.getStartX(), cue.getStartY(), cue.getEndX(), cue.getEndY());
                gc.strokeLine(cueDraw.getStartX(), cueDraw.getStartY(), cueDraw.getEndX(), cueDraw.getEndY());
            }

            for (Ball ball : balls) {
                if (ball.isActive()) {
                    gc.setFill(ball.getColour());
                    gc.fillOval(ball.getxPos() - ball.getRadius(),
                            ball.getyPos() - ball.getRadius(),
                            ball.getRadius() * 2,
                            ball.getRadius() * 2);
                }

            }

            gc.setFill(Paint.valueOf("white"));
            gc.fillRect(0, table.getyLength()+TABLEBUFFER*2, table.getxLength()+TABLEBUFFER*2, SCOREBOARDBUFFER);

            //Scoreboard
            gc.setFont(new Font("Impact", 40));
            gc.setTextBaseline(VPos.TOP);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(Paint.valueOf("purple"));
            gc.fillText("SCORE: " + this.score + "    TIME: " + formatTimer(), (table.getxLength()+ TABLEBUFFER*2)/2, (table.getyLength()+ TABLEBUFFER*2));


            // Win
            if (winFlag) {
                gc.setStroke(Paint.valueOf("white"));
                gc.setFont(new Font("Impact", 80));
                gc.strokeText("Win and bye", (table.getxLength()+ TABLEBUFFER*2)/2, (table.getyLength()+ TABLEBUFFER*2)/2);
            }
        }



    }

    /**
     * Updates positions of all balls, handles logic related to collisions.
     * Used Exercise 6 as reference.
     */
    public void tick() {
        // Check if game is won
        int notActiveBalls = 0;
        for (Ball b : balls) {
            if (!b.isActive()) {
                notActiveBalls++;
            }
        }
        if (notActiveBalls == balls.size()-1) {
            winFlag = true;
        }

        // Stop timer if game is won
        if (!winFlag) {
            this.elapsedTime = System.currentTimeMillis()/1000 - startTime;
        }

        for (Ball ball : balls) {
            ball.tick();

            if (ball.isCue() && cueSet) {
                hitBall(ball);
            }

            double width = table.getxLength();
            double height = table.getyLength();

            // Check if ball landed in pocket
            for (Pocket pocket : table.getPockets()) {
                if (pocket.isInPocket(ball)) {
                    if (ball.isCue()) {
                        this.reset();
                    } else {
                        if (ball.remove()) {
                            this.score = this.scoreBoard.calculateScore();
                        } else {
                            // Check if when ball is removed, any other balls are present in its space. (If
                            // another ball is present, blue ball is removed)
                            for (Ball otherBall : balls) {
                                double deltaX = ball.getxPos() - otherBall.getxPos();
                                double deltaY = ball.getyPos() - otherBall.getyPos();
                                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                                if (otherBall != ball && otherBall.isActive() && distance < 10) {
                                    ball.remove();
                                }
                            }

                        }

                    }
                    break;

                }
            }
            //System.out.println("SCORE: " + this.score);

            // Handle the edges (balls don't get a choice here)
            if (ball.getxPos() + ball.getRadius() > width + TABLEBUFFER) {
                ball.setxPos(width - ball.getRadius());
                ball.setxVel(ball.getxVel() * -1);
            }
            if (ball.getxPos() - ball.getRadius() < TABLEBUFFER) {
                ball.setxPos(ball.getRadius());
                ball.setxVel(ball.getxVel() * -1);
            }
            if (ball.getyPos() + ball.getRadius() > height + TABLEBUFFER) {
                ball.setyPos(height - ball.getRadius());
                ball.setyVel(ball.getyVel() * -1);
            }
            if (ball.getyPos() - ball.getRadius() < TABLEBUFFER) {
                ball.setyPos(ball.getRadius());
                ball.setyVel(ball.getyVel() * -1);
            }

            // Apply table friction
            double friction = table.getFriction();
            ball.setxVel(ball.getxVel() * friction);
            ball.setyVel(ball.getyVel() * friction);

            // Check ball collisions
            for (Ball ballB : balls) {
                if (checkCollision(ball, ballB)) {
                    Point2D ballPos = new Point2D(ball.getxPos(), ball.getyPos());
                    Point2D ballBPos = new Point2D(ballB.getxPos(), ballB.getyPos());
                    Point2D ballVel = new Point2D(ball.getxVel(), ball.getyVel());
                    Point2D ballBVel = new Point2D(ballB.getxVel(), ballB.getyVel());
                    Pair<Point2D, Point2D> changes = calculateCollision(ballPos, ballVel, ball.getMass(), ballBPos,
                            ballBVel, ballB.getMass(), false);
                    calculateChanges(changes, ball, ballB);
                }
            }
        }
    }

    /**
     * Resets the game.
     */
    public void reset() {
        for (Ball ball : balls) {
            ball.reset();
        }
        this.startTime = System.currentTimeMillis()/1000;
        this.score = 0;
    }

    /**
     * @return scene.
     */
    public Scene getScene() {
        return this.scene;
    }

    /**
     * Sets the table of the game.
     *
     * @param table
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * @return table
     */
    public Table getTable() {
        return this.table;
    }

    /**
     * Sets the balls of the game.
     *
     * @param balls
     */
    public void setBalls(List<Ball> balls) {
        this.balls = balls;
        this.scoreBoard = new Scoreboard(this.balls);
    }

    /**
     * Hits the ball with the cue, distance of the cue indicates the strength of the
     * strike.
     *
     * @param ball
     */
    private void hitBall(Ball ball) {
        this.mementos.add(createMemento());
        double deltaX = ball.getxPos() - cue.getStartX();
        double deltaY = ball.getyPos() - cue.getStartY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Check that start of cue is within cue ball
        if (distance < ball.getRadius()) {
            // Collide ball with cue
            double hitxVel = (cue.getStartX() - cue.getEndX()) * FORCEFACTOR;
            double hityVel = (cue.getStartY() - cue.getEndY()) * FORCEFACTOR;

            ball.setxVel(hitxVel);
            ball.setyVel(hityVel);
        }

        cueSet = false;


    }

    /**
     * Changes values of balls based on collision (if ball is null ignore it)
     *
     * @param changes
     * @param ballA
     * @param ballB
     */
    private void calculateChanges(Pair<Point2D, Point2D> changes, Ball ballA, Ball ballB) {
        ballA.setxVel(changes.getKey().getX());
        ballA.setyVel(changes.getKey().getY());
        if (ballB != null) {
            ballB.setxVel(changes.getValue().getX());
            ballB.setyVel(changes.getValue().getY());
        }
    }

    /**
     * Sets the cue to be drawn on click, and manages cue actions
     *
     * @param pane
     */
    private void setClickEvents(Pane pane) {
        double cueLength = 100;
        pane.setOnMousePressed(event -> {
            if (clickOnCueBall(event.getX(), event.getY()) && !isCueDrawn) {
                cue = new  Line(event.getX(), event.getY(), event.getX(), event.getY());
                cueDraw = new Line(event.getX(), event.getY(), event.getX() + cueLength, event.getY());
                cueDraw.setStrokeWidth(4);
                cueDraw.setStroke(Paint.valueOf("orange"));
                pane.getChildren().add(1, cueDraw);
                cueSet = false;
                isCueDrawn = true;
            } else if (clickOnCueStick(event.getX(), event.getY(), cueLength) && isCueDrawn) {
                cueActive = true;
            }

        });

        pane.setOnMouseDragged(event -> {
            if (cueActive) {
                cue.setEndX(event.getX());
                cue.setEndY(event.getY());
                Point2D startPoints = calculateCueStartPoints(cueLength);
                cueDraw.setStartX(startPoints.getX());
                cueDraw.setStartY(startPoints.getY());
                cueDraw.setEndX(event.getX());
                cueDraw.setEndY(event.getY());
            }

            // drag cue around
        });

        pane.setOnMouseReleased(event -> {
            if (isCueDrawn && cueActive) {
                pane.getChildren().remove(cueDraw);
                isCueDrawn = false;
            }
            cueSet = true;
            cueActive = false;

            //hit ball and remove cue
        });

    }

    /**
     * Manages the button actions for changing difficulty, cheating and undoing an action.
     *
     * @param scene
     */
    private void setKeyBoardEvents(Scene scene) {
        scene.setOnKeyPressed(event -> {
            // SETTING DIFFICULTLY
            if (event.getCode() == KeyCode.E) {
                // EASY
                changeDifficulty("src/main/resources/config_easy.json");
                reset();
                gameStartedFlag = true;
            } else if (event.getCode() == KeyCode.N) {
                // NORMAL
                changeDifficulty("src/main/resources/config_normal.json");
                reset();
                gameStartedFlag = true;
            } else if (event.getCode() == KeyCode.H) {
                // HARD
                changeDifficulty("src/main/resources/config_hard.json");
                reset();
                gameStartedFlag = true;
            }

            // CHEATING
            if (event.getCode() == KeyCode.DIGIT1) {
                // RED BALLS
                removeBalls("red");
            } else if (event.getCode() == KeyCode.DIGIT2) {
                // YELLOW BALLS
                removeBalls("yellow");
            } else if (event.getCode() == KeyCode.DIGIT3) {
                // GREEN BALLS
                removeBalls("green");
            } else if (event.getCode() == KeyCode.DIGIT4) {
                // BROWN BALLS
                removeBalls("brown");
            } else if (event.getCode() == KeyCode.DIGIT5) {
                // BLUE BALLS
                removeBalls("blue");
            } else if (event.getCode() == KeyCode.DIGIT6) {
                // PURPLE BALLS
                removeBalls("purple");
            } else if (event.getCode() == KeyCode.DIGIT7) {
                // BLACK BALLS
                removeBalls("black");
            } else if (event.getCode() == KeyCode.DIGIT8) {
                // ORANGE BALLS
                removeBalls("orange");
            }

            // UNDO
            if (event.getCode() == KeyCode.BACK_SPACE) {
                if (this.mementos.size() > 0) {
                    restore(this.mementos.get(this.mementos.size()-1));
                }
            }
        });
    }


    /**
     * Checks if two balls are colliding.
     * Used Exercise 6 as reference.
     *
     * @param ballA
     * @param ballB
     * @return true if colliding, false otherwise
     */
    private boolean checkCollision(Ball ballA, Ball ballB) {
        if (ballA == ballB) {
            return false;
        }

        return Math.abs(ballA.getxPos() - ballB.getxPos()) < ballA.getRadius() + ballB.getRadius() &&
                Math.abs(ballA.getyPos() - ballB.getyPos()) < ballA.getRadius() + ballB.getRadius();
    }

    /**
     * Collision function adapted from assignment, using physics algorithm:
     * http://www.gamasutra.com/view/feature/3015/pool_hall_lessons_fast_accurate_.php?page=3
     *
     * @param positionA The coordinates of the centre of ball A
     * @param velocityA The delta x,y vector of ball A (how much it moves per tick)
     * @param massA     The mass of ball A (for the moment this should always be the
     *                  same as ball B)
     * @param positionB The coordinates of the centre of ball B
     * @param velocityB The delta x,y vector of ball B (how much it moves per tick)
     * @param massB     The mass of ball B (for the moment this should always be the
     *                  same as ball A)
     *
     * @return A Pair in which the first (key) Point2D is the new
     *         delta x,y vector for ball A, and the second (value) Point2D is the
     *         new delta x,y vector for ball B.
     */
    public static Pair<Point2D, Point2D> calculateCollision(Point2D positionA, Point2D velocityA, double massA,
                                                            Point2D positionB, Point2D velocityB, double massB, boolean isCue) {

        // Find the angle of the collision - basically where is ball B relative to ball
        // A. We aren't concerned with
        // distance here, so we reduce it to unit (1) size with normalize() - this
        // allows for arbitrary radii
        Point2D collisionVector = positionA.subtract(positionB);
        collisionVector = collisionVector.normalize();

        // Here we determine how 'direct' or 'glancing' the collision was for each ball
        double vA = collisionVector.dotProduct(velocityA);
        double vB = collisionVector.dotProduct(velocityB);

        // If you don't detect the collision at just the right time, balls might collide
        // again before they leave
        // each others' collision detection area, and bounce twice.
        // This stops these secondary collisions by detecting
        // whether a ball has already begun moving away from its pair, and returns the
        // original velocities
        if (vB <= 0 && vA >= 0 && !isCue) {
            return new Pair<>(velocityA, velocityB);
        }

        // This is the optimisation function described in the gamasutra link. Rather
        // than handling the full quadratic
        // (which as we have discovered allowed for sneaky typos)
        // this is a much simpler - and faster - way of obtaining the same results.
        double optimizedP = (2.0 * (vA - vB)) / (massA + massB);

        // Now we apply that calculated function to the pair of balls to obtain their
        // final velocities
        Point2D velAPrime = velocityA.subtract(collisionVector.multiply(optimizedP).multiply(massB));
        Point2D velBPrime = velocityB.add(collisionVector.multiply(optimizedP).multiply(massA));

        return new Pair<>(velAPrime, velBPrime);
    }

    /**
     * Changes the difficulty of the game based on the config file provided.
     *
     * @param configPath

     */
    private void changeDifficulty(String configPath) {
        this.mementos.add(createMemento());
        ReaderFactory tableFactory = new TableReaderFactory();
        Reader tableReader = tableFactory.buildReader();
        tableReader.parse(configPath, this);

        ReaderFactory ballFactory = new BallReaderFactory();
        Reader ballReader = ballFactory.buildReader();
        ballReader.parse(configPath, this);
        // START TIMER
        startTime = System.currentTimeMillis()/1000;
    }

    /**
     * Creates a new GameChangerMemento to store the state of the game.
     *
     * @return GameManagerMemento
     */
    private GameManagerMemento createMemento() {
        List<Ball> newBalls = new ArrayList<>();
        for (Ball b : balls) {
            newBalls.add(b.clone());
        }
        return new GameManagerMemento(table, newBalls, score, elapsedTime);
    }

    /**
     * Restores the state of the game to a previously saved state.
     *
     * @param memento
     */
    private void restore(GameManagerMemento memento) {
        setTable(memento.getTable());
        setBalls(memento.getBalls());
        this.score = memento.getScore();
        this.startTime = System.currentTimeMillis()/1000 - memento.getElapsedTime();
        this.mementos.remove(memento);
    }

    /**
     * Removes a set of balls from the game depending on the colour provided
     *
     * @params colour
     */
    private void removeBalls(String colour) {
        this.mementos.add(createMemento());
        for (Ball b : balls) {
            if (b.getColour().equals(Paint.valueOf(colour)) && b.isActive()) {
                b.removeImmediately();
            }
        }
    }

    /**
     * Formats the timer into a better looking format
     *
     * @return String
     */
    private String formatTimer() {
        long seconds = this.elapsedTime % 60;
        long minutes = (this.elapsedTime - seconds) / 60;
        String timeformat = String.format("%02d:%02d", minutes, seconds);
        return timeformat;
    }

    /**
     * Checks if the mouse has clicked on the cue ball
     *
     * @param xPos
     * @param yPos
     * @return boolean
     */
    private boolean clickOnCueBall(double xPos, double yPos) {
        Ball cueBall = null;
        for (Ball b : balls) {
            if (b.isCue()) {
                cueBall = b;
                break;
            }
        }
        return (Math.abs(xPos - cueBall.getxPos()) < cueBall.getRadius()*2 && Math.abs(yPos - cueBall.getyPos()) < cueBall.getRadius()*2);
    }

    /**
     * Checks if the mouse has clicked on the cue stick
     *
     * @param xPos
     * @param yPos
     * @return boolean
     */
   private boolean clickOnCueStick(double xPos, double yPos, double cueLength) {
        Ball cueBall = null;
        for (Ball b : balls) {
            if (b.isCue()) {
                cueBall = b;
                break;
            }
        }
        return (Math.abs(xPos - cueBall.getxPos()) < cueBall.getRadius()*2 + cueLength && Math.abs(yPos - cueBall.getyPos()) < cueBall.getRadius()*2);
   }

    /**
     * Calculates the starting point for the cue stick based off a certain length
     *
     * @param cueLength
     * @return Point2D
     */
   private Point2D calculateCueStartPoints(double cueLength) {
        double theta = Math.atan2(Math.abs(cue.getStartX()-cue.getEndX()), Math.abs(cue.getStartY()-cue.getEndY()));
        double deltaY = cueLength * Math.cos(theta);
        double deltaX = cueLength * Math.sin(theta);
        double startX = 0;
        double startY = 0;
        if (cue.getEndX() > cue.getStartX()) {
            startX = cue.getEndX() - deltaX;
        } else {
            startX = cue.getEndX() + deltaX;
        }
        if (cue.getEndY() > cue.getStartY()) {
            startY = cue.getEndY() - deltaY;
        } else {
            startY = cue.getEndY() + deltaY;
        }
        return new Point2D(startX, startY);
    }

}

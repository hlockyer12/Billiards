# Pool Game Builder

To run the application, please use:

gradle run

To generate a javadoc, please use:

gradle javadoc

The javadoc can be found in build/docs/javadoc/index.html

# Game Notes
- In order to hit the ball, click on the white cue ball.
- Once the cue has appeared, click on the end of it to begin to drag the cue.
- Then, drag your cursor away (in the angle you'd like to hit), and then release.
- The power of your hit will be based on the length of your drag (although ball velocity is capped). 

## Cheating
- To cheat, press the number on the keypad corresponding to the number of points a ball is worth.
- The table below shows what each ball is worth

| Colour | Points |
|--------|--------|
| Red    |   1    |
| Yellow |   2    |
| Green  |   3    |
| Brown  |   4    |
| Blue   |   5    |
| Purple |   6    |
| Black  |   7    |
| Orange |   8    |

## Undo
- To undo an action, press the Backspace key

## Change Difficulty
- To select or change difficulty press the key in the table below

| Difficulty | Key |
|------------|-----|
|    Easy    |  E  |
|   Normal   |  N  |
|    Hard    |  H  |

# Config Notes
When entering config details, please note the following restrictions:
- Friction must be value between 0 - 1 (not inclusive). [Would reccomend switching between 0.95, 0.9, 0.85 to see changes].
- Ball X and Y positions must be within the size of the table width and length, including the ball radius (10).
- Ball colours must be Paint string values as expected.

# Design Patterns
## Builder Pattern
Classes involved:
- TableBuilder
- PoolTableBuilder
- Pocket
- Table

## Memento Design Pattern
Classes involved:
- GameManager
- GameManagerMemento

## Prototype Design Pattern
Classes involved:
- Ball
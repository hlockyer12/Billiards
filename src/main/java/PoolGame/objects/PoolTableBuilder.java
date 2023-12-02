package PoolGame.objects;

import java.util.ArrayList;
import java.util.List;

public class PoolTableBuilder implements TableBuilder{

    private String colour;
    private long tableX;
    private long tableY;
    private double friction;
    private List<Pocket> pockets;

    @Override
    public void setColour(String colour) {
        this.colour = colour;
    }

    @Override
    public void setXLength(long tableX) {
        this.tableX = tableX;
    }

    @Override
    public void setYLength(long tableY) {
        this.tableY = tableY;
    }

    @Override
    public void setFriction(double friction) {
        this.friction = friction;
    }

    @Override
    public void setPockets(List<List<Double>> specs) {
        List<Pocket> pockets = new ArrayList<>();
        for (List<Double> pocketSpec : specs) {
            Pocket pocket = new Pocket(pocketSpec.get(0), pocketSpec.get(1), pocketSpec.get(2));
            pockets.add(pocket);
        }
        this.pockets = pockets;
    }

    /**
     * Builds the table
     *
     * @return Table
     */
    public Table build() {
        return new Table(this.colour, this.tableX, this.tableY, this.friction, this.pockets);
    }
}

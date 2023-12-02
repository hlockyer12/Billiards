package PoolGame.objects;

import java.util.ArrayList;
import java.util.List;

public interface TableBuilder {
    public void setColour(String colour);

    public void setXLength(long tableX);

    public void setYLength(long tableY);

    public void setFriction(double friction);

    public void setPockets(List<List<Double>> specs);

}

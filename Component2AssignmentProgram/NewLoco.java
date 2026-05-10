import uk.ac.leedsbeckett.oop.*;

// Custom locomotive
public class NewLoco extends Locomotive
{
    private int carriageCount;
    private int locoId;   // store ID

    public NewLoco(GameWorld world, int x, int y, int carriages, int id)
    {
        super(world, x, y);
        this.carriageCount = carriages;
        this.locoId = id;
    }

    // Required: override toString()
    @Override
    public String toString()
    {
        return "NewLoco ID: " + locoId + " Position: " + getCellPosition();
    }

    public int getCarriageCount()
    {
        return carriageCount;
    }

    public int getLocoId()
    {
        return locoId;
    }
}
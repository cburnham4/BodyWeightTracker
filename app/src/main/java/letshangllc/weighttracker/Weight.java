package letshangllc.weighttracker;

/**
 * Created by cvburnha on 2/12/2016.
 */
public class Weight {
    public int id;
    public String date;
    public double weight;


    public Weight(int id, double weight, String date) {
        this.id = id;
        this.date = date;
        this.weight = weight;

    }

    public Weight(double weight, String date) {
        this.date = date;
        this.weight = weight;
    }
}

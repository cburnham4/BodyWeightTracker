package letshangllc.weighttracker;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by cvburnha on 2/13/2016.
 */
public class MockedData {

    public static final ArrayList<Weight> mockedWeights = new ArrayList<>(Arrays.asList(
            new Weight(0, 170, "01-18-2016"),
            new Weight(0, 169, "01-16-2016"),
            new Weight(0, 167, "01-12-2016"),
            new Weight(0, 168, "01-09-2016"),
            new Weight(0, 166.8, "01-06-2016"),
            new Weight(0, 166, "01-03-2016"),
            new Weight(0, 165, "01-01-2016")
            )
        );

    public static final ArrayList<Weight> mockedWeightsReverse = new ArrayList<>(Arrays.asList(
            new Weight(0, 165, "01-01-2016"),
            new Weight(0, 166, "01-03-2016"),
            new Weight(0, 166.8, "01-06-2016"),

            new Weight(0, 168, "01-09-2016"),
            new Weight(0, 167, "01-12-2016"),
            new Weight(0, 169, "01-16-2016"),
            new Weight(0, 170, "01-18-2016")






    )
    );
}

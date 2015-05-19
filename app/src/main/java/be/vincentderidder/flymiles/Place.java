package be.vincentderidder.flymiles;

import com.orm.SugarRecord;

/**
 * Created by Vincent on 2/05/2015.
 */
public class Place extends SugarRecord<Place> {
    public String address;
    public String id;
    public double lat;
    public double lng;
    public int dist;
    public int pos;
    public String toString(){return address;}
}

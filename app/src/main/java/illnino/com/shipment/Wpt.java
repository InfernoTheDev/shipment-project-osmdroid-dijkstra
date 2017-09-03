package illnino.com.shipment;
import org.osmdroid.util.GeoPoint;

/**
 * Created by No'_Mercy on 8/23/2014.
 */
public class Wpt {
    GeoPoint geoPoint;
    String name;

    public Wpt (GeoPoint argGeopoint, String argName){
        geoPoint = argGeopoint;
        name = argName;

    }
}

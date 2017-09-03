package illnino.com.shipment;

import org.osmdroid.util.GeoPoint;

/**
 * Created by No'_Mercy on 8/17/2014.
 */
public class Position {
    GeoPoint geoPoint;
    Vertex vertex;

        public Position (GeoPoint argGeopoint, Vertex argVertex){
                geoPoint = argGeopoint;
                vertex = argVertex;

        }
}

package illnino.com.shipment;

/**
 * Created by No'_Mercy on 8/17/2014.
 */
public class Edge {

        public final Vertex target;
        public final double weight;

        public Edge(Vertex argTarget, double argWeight){
            target = argTarget; weight = argWeight;
        }
        public Vertex getVertex(){
            return target;
        }

}

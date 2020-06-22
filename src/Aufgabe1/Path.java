package Aufgabe1;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;

public class Path {
   private Vertex ambulance;
   private GraphWalk<Vertex, DefaultWeightedEdge> path;
   private double extraCosts;

   public Path(Vertex ambulance, GraphWalk<Vertex, DefaultWeightedEdge> path, double extraCosts) {
      this.ambulance = ambulance;
      this.path = path;
      this.extraCosts = extraCosts;
   }

   public Vertex getAmbulance() {
      return this.ambulance;
   }

   public void setAmbulance(Vertex ambulance) {
      this.ambulance = ambulance;
   }

   public GraphWalk<Vertex, DefaultWeightedEdge> getPath() {
      return this.path;
   }

   public void setPath(GraphWalk<Vertex, DefaultWeightedEdge> path) {
      this.path = path;
   }

   public double getExtraCosts() {
      return this.extraCosts;
   }

   public void setExtraCosts(double extraCosts) {
      this.extraCosts = extraCosts;
   }
}

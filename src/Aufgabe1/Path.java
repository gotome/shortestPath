package Aufgabe1;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Path {
  Vertex ambulance;
  GraphPath<Vertex, DefaultWeightedEdge> path;

  public Path(Vertex ambulance, GraphPath<Vertex, DefaultWeightedEdge> path) {
    this.ambulance = ambulance;
    this.path = path;
  }

}

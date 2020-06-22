package Aufgabe2;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.graph.*;
import org.jgrapht.nio.*;
import org.jgrapht.nio.dot.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class RadioNetwork {

  /**
   * render a graph in dot format
   *
   * @param g graph to print
   * @param spanningTree computed spanning tree for the graph
   *
   */
  private static void renderRadioNetwork(Graph<Transmitter, DefaultWeightedEdge> g,
                                         SpanningTreeAlgorithm.SpanningTree spanningTree)
          throws ExportException {

    DOTExporter<Transmitter, DefaultWeightedEdge> exporter =
            new DOTExporter<>(v -> (v.getName())); //+ v.getX() + v.getY()).replace(".", "_").replace("-", "min"));
    exporter.setVertexAttributeProvider((v) -> {
      Map<String, Attribute> map = new LinkedHashMap<>();
      map.put("label", DefaultAttribute.createAttribute(v.toString()));
      return map;
    });

    exporter.setEdgeAttributeProvider((e) -> {
      Map<String, Attribute> map = new LinkedHashMap<>();
      DecimalFormat df = new DecimalFormat("#.###");
      double edgeWeight = g.getEdgeWeight(e);
      map.put("label", DefaultAttribute.createAttribute(df.format(edgeWeight)));
      map.put("color", DefaultAttribute.createAttribute(spanningTree.getEdges().contains(e) ? "green" : "grey"));
      return map;
    });


    Writer writer = new StringWriter();
    exporter.exportGraph(g, writer);
    System.out.println(writer.toString());
  }

  /**
   * evaluates if a given string is a double
   * this function is only used by the file parser
   *
   * @param strNum string parameter
   *
   * @return true if String is convertable to double
   */
  public static boolean isNumeric(String strNum) {
    if (strNum == null) {
      return false;
    }
    try {
      double d = Double.parseDouble(strNum);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  /**
   * This file scanner reads a given file and
   * safes all results into a Stack. With this function
   * its possible to build a graph
   *
   *
   * @return Stack<Transmitter> with the graph nodes
   */
  public static Stack<Transmitter> fileScanner() {
    Stack<Transmitter> tStack = new Stack<Transmitter>();
    //creating File instance to reference text file in Java
    String filename = "C:/Users/Geri/OneDrive/FH-Hagenberg/Semester4/AMS/Projekt/Aufgabe 2 Beispiel 1/Aufgabe2Beispiel1.txt";

    File text = new File(filename);
    // create a new scanner
    // with the specified String Object
    Scanner scanner = null;
    try {
      scanner = new Scanner(text).useDelimiter("\\}|\\(|\\)|\\{|\\,\\s+");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    int transmitterNr = 1;
    while (scanner.hasNext()) {
      double x = 0.0;
      double y = 0.0;
      String actual = scanner.next();

      if (isNumeric(actual)) {
        x = Double.valueOf(actual);
        actual = scanner.next();
        if (isNumeric(actual)) {
          y = Double.valueOf(actual);
          String name = "Transmitter" + String.valueOf(transmitterNr++);
          tStack.add(new Transmitter(name, x, y));
        }
      }
    }
    scanner.close();

    return tStack;
  }

  /**
   *          MAIN METHOD
   *
   * @param args console arguments
   *
   */
  public static void main(String[] args) {
    Stack<Transmitter> tStack = fileScanner();
    //create an undirected weighted graph
    Graph<Transmitter, DefaultWeightedEdge> g =
            new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);
    //add Vertexes to graph
    while (!tStack.isEmpty()) {
      g.addVertex(tStack.pop());
    }
    //connect all nodes with each other
    for (Transmitter t : g.vertexSet()) {
      for (Transmitter tNext : g.vertexSet()) {
        if (t != tNext) {
          DefaultWeightedEdge e = g.addEdge(t, tNext);
          //if edge already exists
          if (e != null) {
            g.setEdgeWeight(e, Distance.Euclid(t, tNext));
          }
        }
      }
    }
    //compute the minimum spanning tree of the graph network
    SpanningTreeAlgorithm.SpanningTree<DefaultWeightedEdge> minSpanningTree =
            new PrimMinimumSpanningTree<>(g).getSpanningTree();
    //render network
    System.out.println("(http://www.webgraphviz.com/):");
    renderRadioNetwork(g, minSpanningTree);

    //set transmitting power for each transmitter
    for(Transmitter t: g.vertexSet()) {
      for (Transmitter tNext : g.vertexSet()) {
        if (t != tNext) {
          DefaultWeightedEdge e = g.getEdge(t, tNext);
          if (e != null && minSpanningTree.getEdges().contains(e)) {
            if (t.getDistance() < g.getEdgeWeight(e)) {
              t.setDistance(g.getEdgeWeight(e));
            }
          }
        }
      }
    }
    //print result
    DecimalFormat df = new DecimalFormat("#.###");
    for(Transmitter t: g.vertexSet()) {
      System.out.println(t + " distance= " + df.format(t.getDistance()));
    }

  }

}

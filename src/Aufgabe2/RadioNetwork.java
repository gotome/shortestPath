package Aufgabe2;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.graph.*;
import org.jgrapht.nio.*;
import org.jgrapht.nio.dot.*;
import org.jgrapht.traverse.*;

import javax.swing.*;
import java.awt.geom.Arc2D;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

public class RadioNetwork {

  /**
   * Render a graph in DOT format.
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


  public static void main(String[] args) {
    Stack<Transmitter> tStack = new Stack<Transmitter>();

    //creating File instance to reference text file in Java
    String filename = "C:/Users/Geri/OneDrive/FH-Hagenberg/Semester4/AMS/Projekt/Aufgabe2Beispiel1/Aufgabe2Beispiel1.txt";

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


  /*
    tStack.add(new Transmitter("Transmitter1", 54.2308, -66.0395));
    tStack.add(new Transmitter("Transmitter2",-44.7392, 24.194));
    tStack.add(new Transmitter("Transmitter3",-53.3391, -22.3683));
    tStack.add(new Transmitter("Transmitter4",10.9936, 99.1662));
    tStack.add(new Transmitter("Transmitter5",-90.4302, 99.2572));
    tStack.add(new Transmitter("Transmitter6",43.2174, 28.7044));
    tStack.add(new Transmitter("Transmitter7",-2.87929, -13.0876));
    tStack.add(new Transmitter("Transmitter8",-86.1593, -32.9225));
    tStack.add(new Transmitter("Transmitter9",-51.6195, 21.8886));
    tStack.add(new Transmitter("Transmitter10",74.1398, -68.3909));


    tStack.add(new Transmitter("Transmitter1",0, 0));
    tStack.add(new Transmitter("Transmitter2",1, 1));
    tStack.add(new Transmitter("Transmitter3",3, 2));
    tStack.add(new Transmitter("Transmitter4",5, 5));
    tStack.add(new Transmitter("Transmitter5",-0.5, -1));
*/
    Graph<Transmitter, DefaultWeightedEdge> g =
            new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);

    while (!tStack.isEmpty()) {
      g.addVertex(tStack.pop());
    }

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

    SpanningTreeAlgorithm.SpanningTree<DefaultWeightedEdge> minSpanningTree =
            new PrimMinimumSpanningTree<>(g).getSpanningTree();

    System.out.println("-- renderHrefGraph output");
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

    DecimalFormat df = new DecimalFormat("#.###");
    for(Transmitter t: g.vertexSet()) {
      System.out.println(t + " distance= " + df.format(t.getDistance()));
    }

  }

}

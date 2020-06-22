//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package Aufgabe1;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.InvalidPathException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.dot.DOTExporter;

public final class EmergencyPlaner {
    //walk weight calculator (used for concat paths)
    private static final Function<GraphWalk<Vertex, DefaultWeightedEdge>, Double> walkWeightCalculator = (graphWalk) -> {
        return graphWalk.getEdgeList().stream().flatMapToDouble((edge) -> {
            return DoubleStream.of(graphWalk.getGraph().getEdgeWeight(edge));
        }).sum();
    };

    private EmergencyPlaner() {
    }

    /**
     * Create graph
     *
     * @param edges graph edges
     * @param hospitals hospital nodes
     * @param ambulances ambulance nodes
     * @param ambulanceStatus ambulance status
     * @param accidents accident nodes
     * @param weights edge weights
     *
     * @return Graph<Vertex, DeafaultWeightedEdge> simple weighted graph
     */
    private static Graph<Vertex, DefaultWeightedEdge> createVertexGraph(Integer[][] edges, Integer[] hospitals, Integer[] ambulances, Integer[] ambulanceStatus, Integer[] accidents, Double[] weights) {
        Graph<Vertex, DefaultWeightedEdge> g = new SimpleWeightedGraph(DefaultWeightedEdge.class);
        int srcIndex = 0;
        int dstIndex = 1;

        for(int i = 0; i < edges.length; ++i) {
            Double currWeight = weights[i];
            Integer[] currEdge = edges[i];
            Integer srcVertex = currEdge[0];
            Integer destVertex = currEdge[1];

            Set<Vertex> srcVertexSet = getVertices(srcVertex, accidents, hospitals, ambulances, ambulanceStatus);
            Set<Vertex> dstVertexSet = getVertices(destVertex, accidents, hospitals, ambulances, ambulanceStatus);

            for (Vertex fromVertex : srcVertexSet){
                for (Vertex toVertex : dstVertexSet){
                    g.addVertex(fromVertex);
                    g.addVertex(toVertex);
                    Graphs.addEdge(g, fromVertex, toVertex, currWeight);
                }
            }
        }

        return g;
    }

    /**
     * Initialise the graph with the propper vertices
     *
     * Following nodes are available:
     *  - standard
     *  - ambulance
     *  - hospital
     *  - accident
     *
     * @param g graph
     *
     * @return a set with all initialised verticses
     */
    private static Set<Vertex> getVertices(Integer index, Integer[] accidents, Integer[] hospitals, Integer[] ambulance, Integer[] ambulanceStatus) {
        Set<Vertex> vertices = new HashSet();

        if (!Arrays.stream(accidents).anyMatch((ix) -> {
            return ix == index;
        }) && !Arrays.stream(hospitals).anyMatch((ix) -> {
            return ix == index;
        }) && !Arrays.stream(ambulance).anyMatch((ix) -> {
            return ix == index;
        })) {
            vertices.add(new Vertex("Knoten" + index, VertexDefinition.STANDARD));
            return vertices;

        } else {
            int nr;
            for(int i = 0; i < accidents.length; ++i) {
                if (accidents[i] == index) {
                    nr = i + 1;
                    vertices.add(new Vertex("Unfall" + nr, VertexDefinition.ACCIDENT));
                }
            }

            for(int i = 0; i < hospitals.length; ++i) {
                if (hospitals[i] == index) {
                    nr = i + 1;
                    vertices.add(new Vertex("Krankenhaus" + nr, VertexDefinition.HOSPITAL));
                }
            }

            for(int i = 0; i < ambulance.length; ++i) {
                if (ambulance[i] == index) {
                    nr = i + 1;
                    Integer var10000 = ambulanceStatus[i];
                    vertices.add(new Vertex("Rettung" + nr, VertexDefinition.AMBULANCE, ambulanceStatus[i]));
                }
            }

            return vertices;
        }
    }

    /**
     * This method computes all neccessary walks for the
     * accident scene. You will get a path for each accident
     * from a ambulance to an accident.
     *
     * @param g graph
     *
     * @return map with all shortest accident paths
     */
    public static Map<Vertex, Path> computeWalks(Graph<Vertex, DefaultWeightedEdge> g) {
        Map<Vertex, Path> emergencyWalks = new Hashtable();
        List<Vertex> hospitals = (List)g.vertexSet().stream().filter((v) -> {
            return v.getDefinition().equals(VertexDefinition.HOSPITAL);
        }).collect(Collectors.toList());
        List<Vertex> accidents = (List)g.vertexSet().stream().filter((v) -> {
            return v.getDefinition().equals(VertexDefinition.ACCIDENT);
        }).collect(Collectors.toList());

        DijkstraShortestPath<Vertex, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath(g);

        for (Vertex accident : accidents) {
            List<Vertex> ambulances = (List)g.vertexSet().stream().filter((v) -> {
                return v.getDefinition().equals(VertexDefinition.AMBULANCE);
            }).collect(Collectors.toList());

            double currExtraCosts = 0.0;
            GraphWalk<Vertex, DefaultWeightedEdge> currShortestPath = null;

            for (Vertex ambulance : ambulances.stream().filter(v -> v.getStatus() < 3).collect(Collectors.toList())) {
                if (!emergencyWalks.values().stream().anyMatch(path -> path.getAmbulance().equals(ambulance))) {
                    switch(ambulance.getStatus()) {
                        case 0:
                            currExtraCosts = 0.0;
                            currShortestPath = shortestPathForAmbulance(hospitals, ambulance, accident, dijkstraShortestPath, 0);
                            break;
                        case 1:
                            currExtraCosts = Const.PAUSE_WEIGHT;
                            currShortestPath = shortestPathForAmbulance(hospitals, ambulance, accident, dijkstraShortestPath, 1);
                            break;
                        case 2:
                            currExtraCosts = Const.HOSPITAL_WEIGHT;
                            currShortestPath = shortestPathForAmbulance(hospitals, ambulance, accident, dijkstraShortestPath, 2);
                        case 3:
                    }
                }

                if (currShortestPath != null) {
                    if (!emergencyWalks.containsKey(accident)) {
                        emergencyWalks.put(accident, new Path(ambulance, currShortestPath, currExtraCosts));
                    }
                    else {
                        double oldWeight = ((Path)emergencyWalks.get(accident)).getExtraCosts() + ((Path)emergencyWalks.get(accident)).getPath().getWeight();
                        int oldLength = ((Path)emergencyWalks.get(accident)).getPath().getLength();
                        double currWeight = currExtraCosts + currShortestPath.getWeight();
                        int currLenght = currShortestPath.getLength();

                        if (oldWeight > currWeight || oldWeight == currWeight && oldLength > currLenght) {
                            emergencyWalks.put(accident, new Path(ambulance, currShortestPath, currExtraCosts));
                        }
                    }
                }
            }

            if (!emergencyWalks.containsKey(accident)) {
                throw new InvalidPathException("kein Pfad gefunden", accident.toString());
            }
        }

        return emergencyWalks;
    }

    /**
     * prints path/ walk statistiks to console
     *
     * @param hospitals hospitals
     * @param ambulance current ambulance
     * @param accident current accident
     * @param dijkstraShortestPath dijkstra shortest path
     * @param ambulanceStatus ambulance status
     *
     * @return shortest for ambulance path to an accident
     */
    // TODO dieser Code wurde durch recompile erzeugt ! Muss noch refactored werden !
    private static GraphWalk<Vertex, DefaultWeightedEdge> shortestPathForAmbulance(List<Vertex> hospitals, Vertex ambulance, Vertex accident, DijkstraShortestPath<Vertex, DefaultWeightedEdge> dijkstraShortestPath, int ambulanceStatus) {
        GraphWalk<Vertex, DefaultWeightedEdge> shortestPath = null;
        GraphWalk hospitalWalk;

        if (ambulanceStatus != 2) {
            hospitalWalk = (GraphWalk)dijkstraShortestPath.getPath(ambulance, accident);
            shortestPath = hospitalWalk;
            return shortestPath;
        } else {
            shortestPath = null;
            GraphPath<Vertex, DefaultWeightedEdge> minimumHospitalWalk = null;
            Iterator var9 = hospitals.iterator();

            while(true) {
                GraphPath tempGraphPath;
                do {
                    if (!var9.hasNext()) {
                        hospitalWalk = (GraphWalk)minimumHospitalWalk;
                        if (hospitalWalk == null) {
                            throw new InvalidPathException("keine Krankenhaueser gefunden %s", ambulance.toString());
                        }

                        GraphWalk<Vertex, DefaultWeightedEdge> shortestPathToAccident = (GraphWalk)dijkstraShortestPath.getPath((Vertex)hospitalWalk.getEndVertex(), accident);
                        shortestPath = hospitalWalk.concat(shortestPathToAccident, walkWeightCalculator);
                        return shortestPath;
                    }

                    Vertex hospital = (Vertex)var9.next();
                    tempGraphPath = dijkstraShortestPath.getPath(ambulance, hospital);
                } while(minimumHospitalWalk != null && tempGraphPath.getWeight() >= minimumHospitalWalk.getWeight());

                minimumHospitalWalk = tempGraphPath;
            }
        }
    }

    /**
     * prints path/ walk statistiks to console
     *
     * @param foundPaths all found paths
     *
     */
    public static void printWalks(Map<Vertex, Path> foundPaths) {
        for (Entry<Vertex, Path> element: foundPaths.entrySet()) {
            double totalWeight = ((Path)element.getValue()).getPath().getWeight() + ((Path)element.getValue()).getExtraCosts();
            System.out.println("********************************************************");
            System.out.println("Path for accident: " + element.getKey());
            System.out.println("Ambulance Car " + ((Path)element.getValue()).getAmbulance());
            System.out.println("Total Path Weight: " + totalWeight);
            System.out.println(((Path)element.getValue()).getPath());
            System.out.println("********************************************************");
        }
    }

    /**
     * render graph for a better visualization
     *
     * @param graph given graph
     *
     */
    private static void renderGraph(Graph<Vertex, DefaultWeightedEdge> graph) throws ExportException {
        DOTExporter<Vertex, DefaultWeightedEdge> exporter = new DOTExporter<>(v -> v.getDesignation());

        System.out.println("(http://www.webgraphviz.com/):");
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            map.put("fillcolor", DefaultAttribute.createAttribute(v.getStatusColor()));
            map.put("style", DefaultAttribute.createAttribute("filled"));
            return map;
        });
        exporter.setEdgeAttributeProvider((e) -> {
            Map<String, Attribute> map = new LinkedHashMap();
            map.put("weight", DefaultAttribute.createAttribute((int)graph.getEdgeWeight(e)));
            map.put("label", DefaultAttribute.createAttribute(graph.getEdgeWeight(e)));
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        System.out.println(writer.toString());
    }

    /**
     * rendering emergency paths
     *
     * @param graph given graph
     * @param path emergency paths
     *
     * @return Stack<Transmitter> with the graph nodes
     */
    private static void renderEmergencyPaths(Graph<Vertex, DefaultWeightedEdge> graph, Path path) throws ExportException {
        DOTExporter<Vertex, DefaultWeightedEdge> exporter = new DOTExporter<>(v -> v.getDesignation());

        System.out.println("(http://www.webgraphviz.com/):");
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            map.put("fillcolor", DefaultAttribute.createAttribute(v.getStatusColor()));
            map.put("style", DefaultAttribute.createAttribute("filled"));
            return map;
        });
        exporter.setEdgeAttributeProvider((e) -> {
            Map<String, Attribute> map = new LinkedHashMap();
            map.put("weight", DefaultAttribute.createAttribute((int)graph.getEdgeWeight(e)));
            map.put("label", DefaultAttribute.createAttribute(graph.getEdgeWeight(e)));
            map.put("color", DefaultAttribute.createAttribute(path != null && path.getPath().getEdgeList().contains(e) ? "ORANGE" : "BLACK"));
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        System.out.println(writer.toString());
    }

    /**
     *          MAIN METHOD
     *
     * @param args console arguments
     *
     */
    public static void main(String[] args) throws ExportException {
        //example 1
        /*
        Integer[][] edges = {{1, 2}, {1, 6}, {2, 3}, {2, 7}, {3, 8}, {4, 5}, {4, 9}, {5, 10}, {6, 7}, {6, 11}, {7, 8}, {8, 9}, {8, 13}, {9, 10}, {9, 14}, {10, 15}, {11, 12}, {12, 13}, {12, 17}, {13, 14}, {13, 18}, {14, 15}, {14, 19}, {15, 20}, {16, 17}, {16, 21}, {17, 18}, {17, 22}, {18, 19}, {18, 23}, {19, 20}, {19, 24}, {20, 25}, {21, 26}, {22, 23}, {22, 27}, {23, 24}, {23, 28}, {24, 25}, {24, 29}, {25, 30}, {26, 27}, {27, 28}, {27, 32}, {28, 29}, {29, 30}, {29, 34}, {30, 35}, {31, 32}, {31, 36}, {32, 33}, {33, 34}, {33, 38}, {34, 35}, {34, 39}, {35, 40}, {36, 37}, {36, 41}, {37, 38}, {37, 42}, {38, 39}, {38, 43}, {39, 44}, {40, 45}, {41, 42}, {41, 46}, {42, 43}, {43, 44}, {44, 45}, {44, 49}, {45, 50}, {46, 47}, {47, 48}, {48, 49}, {49, 50}};
        Integer[] hospitals = {28};
        Integer[] ambulances = {27, 1, 46, 31, 27};
        Integer[] ambulanceStatus = {2, 0, 0, 1, 3};
        Integer[] accidents = {18, 21, 44};
        Double[] weights = {0.623319D, 0.782928D, 0.724415D, 0.663301D, 0.980314D, 0.534384D, 0.857525D, 0.802711D, 0.690864D, 1.01628D, 1.01964D, 1.07021D, 1.27433D, 0.786696D, 1.09202D, 1.039D, 1.17553D, 0.893182D, 1.30296D, 0.90763D, 1.23687D, 0.747427D, 1.21788D, 1.19712D, 0.929205D, 0.824945D, 0.843394D, 1.27442D, 0.825103D, 1.16764D, 0.677995D, 1.30186D, 1.31337D, 0.904508D, 0.632366D, 1.35271D, 0.715957D, 1.02362D, 0.559852D, 1.38604D, 1.37545D, 1.05876D, 0.660787D, 1.69842D, 0.843769D, 0.319187D, 1.56455D, 1.36498D, 1.41519D, 1.30702D, 0.942232D, 0.708194D, 1.10823D, 0.409737D, 1.35155D, 1.27066D, 0.444337D, 1.17571D, 0.858115D, 0.609474D, 0.671463D, 0.841348D, 1.18433D, 1.15872D, 0.527241D, 1.01031D, 0.765726D, 0.746467D, 0.715457D, 1.08177D, 0.917307D, 0.814914D, 0.812392D, 1.01424D, 0.734067D};
         *//*
        //example 2
        Integer[][] edges = {{1, 2}, {1, 11}, {2, 3}, {3, 4}, {3, 13}, {4, 5}, {4, 14}, {5, 15}, {6, 7}, {6, 16}, {7, 8}, {7, 17}, {8, 9}, {8, 18}, {9, 10}, {9, 19}, {10, 20}, {11, 12}, {11, 21}, {12, 13}, {13, 14}, {13, 23}, {14, 24}, {15, 25}, {16, 17}, {16, 26}, {17, 18}, {17, 27}, {18, 19}, {18, 28}, {19, 20}, {19, 29}, {20, 30}, {21, 22}, {21, 31}, {22, 32}, {23, 24}, {23, 33}, {24, 25}, {24, 34}, {25, 26}, {25, 35}, {26, 27}, {26, 36}, {27, 28}, {27, 37}, {28, 29}, {28, 38}, {29, 30}, {29, 39}, {30, 40}, {31, 32}, {31, 41}, {32, 33}, {32, 42}, {33, 34}, {33, 43}, {35, 36}, {36, 37}, {36, 46}, {37, 38}, {37, 47}, {38, 39}, {38, 48}, {39, 40}, {39, 49}, {40, 50}, {41, 42}, {41, 51}, {42, 43}, {42, 52}, {43, 44}, {43, 53}, {44, 54}, {45, 46}, {45, 55}, {46, 56}, {47, 48}, {47, 57}, {48, 49}, {48, 58}, {49, 50}, {49, 59}, {51, 52}, {51, 61}, {52, 53}, {52, 62}, {53, 54}, {53, 63}, {54, 55}, {54, 64}, {55, 56}, {55, 65}, {56, 57}, {56, 66}, {57, 58}, {57, 67}, {58, 59}, {59, 60}, {59, 68}, {61, 70}, {62, 71}, {63, 64}, {63, 72}, {64, 65}, {65, 66}, {65, 74}, {66, 75}, {67, 76}, {68, 69}, {68, 78}, {69, 79}, {70, 71}, {71, 72}, {71, 81}, {72, 82}, {73, 83}, {74, 84}, {75, 76}, {75, 85}, {76, 77}, {76, 86}, {77, 78}, {77, 87}, {78, 79}, {78, 88}, {79, 89}, {80, 81}, {80, 90}, {81, 82}, {81, 91}, {82, 83}, {83, 84}, {84, 85}, {84, 94}, {85, 86}, {85, 95}, {86, 87}, {86, 96}, {87, 88}, {87, 97}, {88, 89}, {88, 98}, {90, 91}, {91, 92}, {92, 93}, {94, 95}, {95, 96}, {97, 98}, {98, 99}};
        Integer[] hospitals = {28, 85};
        Integer[] ambulances = {13, 72, 65, 44, 59, 49, 41, 42, 30, 67, 28, 86};
        Integer[] ambulanceStatus = {1, 0, 0, 2, 0, 3, 0, 3, 2, 0, 0, 2};
        Integer[] accidents = {93, 1, 10, 77, 55};
        Double[] weights = {0.703375, 0.784261, 0.829004, 0.869329, 0.748562, 0.893425, 0.653708, 0.957589, 0.667363, 0.805526, 0.774444, 0.73563, 0.755074, 0.69531, 0.614959, 0.672444, 0.624784, 0.760929, 1.03738, 0.89427, 0.795905, 0.999363, 1.00218, 1.0546, 0.837626, 1.16429, 0.901573, 0.944696, 0.837826, 0.891127, 0.664876, 0.862421, 0.781678, 0.556984, 1.00128, 0.81561, 1.1012, 1.2091, 1.65717, 1.02598, 1.66182, 1.00957, 1.29639, 1.10049, 1.1061, 1.04713, 0.937068, 0.988113, 0.712034, 0.973212, 0.816868, 0.696852, 0.99837, 1.08486, 1.01099, 1.02945, 1.34983, 1.09116, 1.24841, 1.44935, 1.07811, 1.1665, 0.929216, 1.02973, 0.726059, 1.08606, 0.737615, 0.728661, 0.975, 0.915643, 1.0634, 0.936564, 1.15438, 0.985634, 0.964043, 0.967595, 1.19505, 0.888192, 1.248, 0.884907, 0.964052, 0.852116, 1.30489, 0.828469, 0.905975, 1.12908, 1.09785, 1.22939, 1.08866, 1.49718, 0.805292, 1.36009, 0.995144, 1.48768, 1.20279, 1.22501, 1.23988, 1.07607, 0.666634, 1.41805, 0.84532, 1.02544, 1.12996, 1.20816, 1.22105, 1.15477, 1.06479, 1.22923, 1.19593, 0.801484, 1.19898, 0.668349, 0.882984, 0.953381, 1.16892, 1.00683, 0.663063, 0.954983, 1.09698, 1.00384, 1.13421, 0.957261, 1.0309, 0.735248, 0.681852, 0.944594, 0.619566, 0.807938, 0.618474, 1.26809, 1.15403, 1.42848, 1.49422, 1.37849, 0.803128, 1.23825, 0.642581, 1.27841, 0.767574, 0.968954, 0.860625, 0.669729, 0.845074, 0.328445, 0.937256, 0.672901, 0.737365, 0.713149, 0.628288, 0.712777};
        //example 3
        */
        Integer[][] edges = {{1, 2}, {1, 6}, {2, 4}, {2, 3}, {3, 5}, {3, 4}, {4, 6}, {4, 5}, {5, 7}, {5, 8}, {6, 7}, {6, 10}, {7, 8}, {7,9}, {8,9}, {9, 10}};
        Integer[] hospitals = {2};
        Integer[] ambulances = {3, 6};
        Integer[] ambulanceStatus = {0, 2};
        Integer[] accidents = {8, 10};
        Double[] weights = {1.0, 1.0, 3.0, 2.0, 8.0, 6.0, 2.0, 2.0, 2.0, 3.0, 5.0, 2.0, 2.0, 1.0, 4.0, 2.0};


        Graph<Vertex, DefaultWeightedEdge> g = createVertexGraph(edges, hospitals, ambulances, ambulanceStatus, accidents, weights);
        renderGraph(g);
        Map<Vertex, Path> emergencyWalks = computeWalks(g);

        for (Map.Entry<Vertex, Path> entry : emergencyWalks.entrySet()) {
          System.out.println("*************************************************");
          System.out.println("  EMERGENCY WALK FOR ACCIDENT: " + entry.getKey());
          System.out.println("*************************************************");
          renderEmergencyPaths(g, (Path)entry.getValue());
        }

        printWalks(emergencyWalks);
    }
}

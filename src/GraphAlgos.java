import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

public class GraphAlgos {

    private static GraphAlgos singleton = new GraphAlgos();

    /*
     * In all of our graph algorithms, we will represent the graph
     * via adjacency lists. One restriction we'll have is that each node
     * has a distinct key.
     * 
     * We also assume that all shortest paths' weights do not exceed (Integer.MAX_VALUE - 1).
     * We reserve this particular value to represent +infinity
     * 
     * For vertices that have no neighbors, it will also appear in the adjacency
     * list data structure.
     */
    
    public static int[] generateTopologicalOrder(HashMap<Integer, HashSet<Integer>> graph) {
        // Child nodes: nodes with incoming neighbors/parents
        var isChildNode = new HashMap<Integer, Boolean>();

        // Whether or not a node has been DFSed on or not
        var isDFSed = new HashMap<Integer, Boolean>();

        for (var kv : graph.entrySet()) {
            if (!isChildNode.containsKey(kv.getKey()))
                isChildNode.put(kv.getKey(), false); 
            isDFSed.put(kv.getKey(), false);

            for (var n : kv.getValue())
                isChildNode.put(n, true);
        }

        var topoOrder = new int[isChildNode.size()];

        int prevIdx = -1;
        // Call DFS on root nodes (nodes without incoming neighbors/parents)
        for (var k : isChildNode.keySet()) {
            if (!isChildNode.get(k))
                prevIdx = topoOrderDFS(topoOrder, k, prevIdx + 1, graph, isDFSed);
        }

        return topoOrder;
    }

    private static int topoOrderDFS(int[] topoOrder, int node, int nodeIdx, HashMap<Integer, HashSet<Integer>> graph, HashMap<Integer, Boolean> isDFSed) {
        isDFSed.put(node, true);

        topoOrder[nodeIdx] = node;

        int prevIdx = nodeIdx;
        for (var n : graph.get(node)) {
            if (isDFSed.get(n)) continue;
            prevIdx = topoOrderDFS(topoOrder, n, prevIdx + 1, graph, isDFSed);
        }

        return prevIdx;
    }

    public static HashMap<Integer, Integer> getShortestPathsDAGRelaxation(HashMap<Integer, HashSet<Integer>> graph, HashMap<Integer, HashMap<Integer, Integer>> weights) {
        var shortestPaths = new HashMap<Integer, Integer>();
        var topoOrder = generateTopologicalOrder(graph);

        for (var n : topoOrder) {
            for (var neighbor : graph.get(n)) {
                if (!shortestPaths.containsKey(n))
                    shortestPaths.put(neighbor, weights.get(n).get(neighbor));
                else {
                    shortestPaths.put(neighbor, shortestPaths.get(n) + weights.get(n).get(neighbor));
                }
            }
        }
        
        return shortestPaths;
    }

    /*
     * An implementation of Dijkstra's algorithm
     */
    public static HashMap<Integer, Integer> getShortestPathsDijkstra(HashMap<Integer, HashMap<Integer, Integer>> graph, int source) {
        // Intiailization
        var dist = new HashMap<Integer, Integer>();
        for (var n : graph.keySet()) 
            dist.put(n, Integer.MAX_VALUE);

        dist.put(0, 0);

        // Represents the nodes that have been removed from the priority queue
        var processed = new HashSet<Integer>();

        var pq = new PriorityQueue<Pair<Integer, Integer>>();
        pq.add(singleton.new Pair<Integer,Integer>(source, 0));

        while (!pq.isEmpty()) {
            var smallestPair = pq.poll();
            var node = smallestPair.key;

            // Attempts to relax the neighbors
            for (var neighborAndWeight : graph.get(node).entrySet()) {
                var neighbor = neighborAndWeight.getKey();
                var weight = neighborAndWeight.getValue();

                // When does this occur?
                if (processed.contains(neighbor)) continue;

                // Alternative path from source to that neighbor: the path to that neighbor, that goes through node "n"
                var altNeighborDist = dist.get(node) + weight;
                
                if (altNeighborDist < dist.get(neighbor)) {
                    dist.put(neighbor, altNeighborDist);
                    pq.add(singleton.new Pair<Integer, Integer>(neighbor, altNeighborDist));
                }
            }
        }

        // At the end of the algorithm, the "dist" map is the map of all nodes to its respective shortest paths
        return dist;
    }

    private class Pair<K, V extends Comparable<V>> implements Comparable<Pair<K,V>>{
        public K key;
        public V value;

        public Pair(K k, V v) {
            this.key = k;
            this.value = v;
        }

        @Override
        public int compareTo(Pair<K, V> o) {
            return this.value.compareTo(o.value);
        }
    }

    public static HashMap<Integer, Integer> getShortestPathsBELLMANFORD(int source, HashMap<Integer, HashSet<int[]>> graph) {
        var shortestPaths = new HashMap<Integer, Integer>();
        shortestPaths.put(source, 0);
        for (var kv : graph.entrySet()) {
            if (kv.getKey() == source) continue;
            shortestPaths.put(kv.getKey(), Integer.MAX_VALUE);
        }

        var verticesCount = graph.size();
        for (int i = 0; i < verticesCount - 1; i++) {
            for (var nodeAndNeighbors : graph.entrySet()) {
                var node = nodeAndNeighbors.getKey();

                for (var neighborAndWeight : nodeAndNeighbors.getValue()) {
                    var neighbor = neighborAndWeight[0];
                    var weight = neighborAndWeight[1];

                    if (shortestPaths.get(node) != Integer.MAX_VALUE && shortestPaths.get(node) + weight < shortestPaths.get(neighbor))
                        shortestPaths.put(neighbor, shortestPaths.get(node) + weight);
                }
            }
        }
        
        for (var nodeAndNeighbors : graph.entrySet()) {
            var node = nodeAndNeighbors.getKey();

            for (var neighborAndWeight : nodeAndNeighbors.getValue()) {
                var neighbor = neighborAndWeight[0];
                var weight = neighborAndWeight[1];

                if (shortestPaths.get(node) != Integer.MAX_VALUE && shortestPaths.get(node) + weight < shortestPaths.get(neighbor))
                    System.out.println(" Negative-weight cycle!");
            }
        }
        
        return shortestPaths;
    }
    
    /*
     * This algorithm assumes that all nodes are labeled 1 through n
     */
    public static int[][] getShortestPathsFLOYDWARSHALL(int[][] adjMatrix) {
        var shortestPaths = adjMatrix.clone();

        for (int k = 0; k < adjMatrix.length; k++) {
            for (int i = 0; i < adjMatrix.length; i++) {
                for (int j = 0; j < adjMatrix.length; j++) {
                    try {
                        shortestPaths[i][j] = Math.min(shortestPaths[i][j], Math.addExact(shortestPaths[i][k], shortestPaths[k][j]));
                    } catch (ArithmeticException e) {
                        
                    }
                }
            }
        }

        return shortestPaths;
    }

    public static int[] getSingleSourceShortestPathFLOYDWARSHALL(int[][] adjMatrix, int source) {
        var shortestPaths = getShortestPathsFLOYDWARSHALL(adjMatrix);
        return shortestPaths[source];
    }

    
    /*
     * An implementation of Floyd's algorithm for finding a cycle in a functional graph
     */
    public static ArrayList<Integer> findCycleInFunctionalGraph(HashMap<Integer, Integer> graph, int head) {
        var i = head;
        var j = head;

        do {
            i = graph.get(i);
            j = graph.get( graph.get(j) );
        } while (i != j);

        i = head;
        while (i != j) {
            i = graph.get(i);
            j = graph.get(j);
        }

        var nodesInCycle = new ArrayList<Integer>();
        do {
            nodesInCycle.add(j);
            j = graph.get(j);
        } while (j != i);
        
        return nodesInCycle;
    }

    public static ArrayList<ArrayList<Integer>> getSCCsUsingKosaraju(HashMap<Integer, HashMap<Integer, Integer>> graph) {
        var SCCs = new ArrayList<ArrayList<Integer>>();

        var dfs1Stack = new Stack<Integer>();
        var dfsVisited = new HashSet<Integer>();

        // Phase 1 of the algorithm : doing the first DFS
        for (var node : graph.keySet()) {
            if (dfsVisited.contains(node)) continue;
            kosarajuDFS1(node, graph, dfsVisited, dfs1Stack);
        }


        // Phase 2 of the algorithm

        // Generates transpose of the graph
        var reversedGraph = new HashMap<Integer, HashMap<Integer, Integer>>();
        for (var node : graph.keySet())
            reversedGraph.put(node, new HashMap<Integer, Integer>());

        for (var kv : graph.entrySet()) {
            for (var neighborAndWeight : kv.getValue().entrySet()) {
                var neighbor = neighborAndWeight.getKey();
                var weight = neighborAndWeight.getValue();
                reversedGraph.get(neighbor).put(kv.getKey(), weight);
            }
        }

        // Running the second DFS

        // Reuse the dfsVisited in DFS-1 as the dfsVisited in DFS-2
        dfsVisited.clear();
        while (!dfs1Stack.empty()) {
            var node = dfs1Stack.pop();
            if (dfsVisited.contains(node)) continue;
            var comp = new ArrayList<Integer>();
            kosarajuDFS2(node, comp, reversedGraph, dfsVisited);
            SCCs.add(comp);
        }

        return SCCs;
    }

    private static void kosarajuDFS1(int currNode, HashMap<Integer, HashMap<Integer, Integer>> graph, 
            HashSet<Integer> dfs1Visited, Stack<Integer> dfs1Stack) {
        dfs1Visited.add(currNode);
        for (var neighborAndWeight : graph.get(currNode).entrySet()) {
            if (dfs1Visited.contains(neighborAndWeight.getKey()))
                continue;
            kosarajuDFS1(neighborAndWeight.getKey(), graph, dfs1Visited, dfs1Stack);
        }

        dfs1Stack.push(currNode);
    }

    private static void kosarajuDFS2(int node, ArrayList<Integer> component, 
            HashMap<Integer, HashMap<Integer, Integer>> reversedGraph, HashSet<Integer> visited) {
        component.add(node);
        visited.add(node);

        for (var neighborAndWeight : reversedGraph.get(node).entrySet()) {
            var neighbor = neighborAndWeight.getKey();
            if (visited.contains(neighbor)) continue;
            kosarajuDFS2(neighbor, component, reversedGraph, visited);
        }
    }
}
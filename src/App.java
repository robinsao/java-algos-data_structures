import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class App {
    public static void main(String[] args) throws Exception {
        testGraph();
    }  

    static void testSort() {
        int[] data = { 1,-3,-3,1, -10, -5, 2, 8 };

    
        SortingAlgos.quickSortHoare(data);

        for (var i : data) {
            System.out.print(i + " ");
        }
    }

    static void testGraph() {
        var graph = new HashMap<Integer, HashMap<Integer,Integer>>();

        for (int i = 1; i <= 14; i++) {
            graph.put(i, new HashMap<Integer, Integer>());
        }

        // Node 14 is its own SCC

        // First SCC
        graph.get(1).put(2, 1);
        graph.get(2).put(3, 1);
        graph.get(3).put(1, 1);

        graph.get(1).put(4, 1);
        graph.get(3).put(9, 1);
        
        // Second SCC
        graph.get(4).put(5, 1);
        graph.get(5).put(6, 1);
        graph.get(6).put(4, 1);
        graph.get(6).put(7, 1);
        graph.get(7).put(5, 1);

        graph.get(7).put(8, 1);

        // Third SCC
        graph.get(8).put(12, 1);

        // Fourth SCC
        graph.get(9).put(11, 1);
        graph.get(10).put(9, 1);
        graph.get(11).put(12, 1);
        graph.get(12).put(10, 1);
        graph.get(12).put(13, 1);
        graph.get(13).put(9, 1);

        var sccs = GraphAlgos.getSCCsUsingKosaraju(graph);
        System.out.println(sccs.size());
        for (var a : sccs) {
            for (var i : a) {
                System.out.print(i + ", ");
            }

            System.out.println();
        }
    }

    static ArrayList<Integer> testFloydAlgoForCycleDetection() {
        var graph = new HashMap<Integer, Integer>();
        
        for (int i = 0; i < 10; i++) {
            graph.put(i, i+1);
        }
        graph.put(10, 5);

        return GraphAlgos.findCycleInFunctionalGraph(graph, 0);
    }

    static HashSet<int[]> createMap(int... args) {
        var map = new HashSet<int[]>();
        for (int i = 0; i < args.length - 1; i += 2) {
            map.add( new int[] { args[i], args[i + 1] } );
        }
        return map;
    }

    static void testBST() {
        var rootVal = 4;
        int[] otherVals = { 5, 3, 7, 9, 2, 6 };

        var root = new BinarySearchTreeNode(rootVal);

        for (var v : otherVals) 
            root.insertNode(v);

        System.out.println(root.getTraversalOrder());
        root.removeNode(5);
        System.out.println(root.getTraversalOrder());
    }

    static void testBinaryHeap() {
        var data = new int[] { 5, 1, 8, 2, 9, 4, 3 };

        var heap = new BinaryHeap();
        for (var i : data)
            heap.insert(i);

        heap.printBfs();

        heap.remove(9);

        System.out.println();
        heap.printBfs();
        
        System.out.println(" Done ");
    }   

    static void testAVLTree() {
        var root = new AVLTreeNode(10);
        
        root.addNode(9);
        root.addNode(11);
        root.addNode(7);
        root.addNode(8);
        root.addNode(6);
        root.addNode(8.5f);
        root.addNode(12);

        root.printTree();
        System.out.println();

        root.removeNode(6);
        root.printTree();
        System.out.println();

        System.out.println("Done");
    }
}

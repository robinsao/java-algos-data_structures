import java.lang.reflect.Array;
import java.util.List;

public class BinaryHeap {
    
    private int[] heap = new int[2];
    private int arrExpansionFactor = 2;

    private float loadFactor = 0.75f;
    private int occupiedSlots = 0;

    public BinaryHeap() {  }

    public BinaryHeap(int initialSize) {
        heap = new int[initialSize];
    }

    public void insert(int val) {
        if (occupiedSlots >= (heap.length * loadFactor))
            expandArr();
        
        int newItemIdx = occupiedSlots;
        heap[newItemIdx] = val;
        occupiedSlots++;
        maxHeapifyUp();
    }

    public void remove(int val) {
        if (occupiedSlots <= (heap.length * loadFactor))
            shrinkArr();
        
        var targetIdx =  getIndexOf(val);
        if (targetIdx == -1) return;

        heap[targetIdx] = heap[occupiedSlots - 1];

        occupiedSlots--;
        maxHeapifyDown(targetIdx);
    }

    public int[] bfs() {
        return heap.clone();
    }

    public void printBfs() {
        int nodesPrintedToStartNewLine = 1;
        int nodesPrinted = 0;

        for (int i = 0; i < occupiedSlots; i++) {
            if (nodesPrinted == nodesPrintedToStartNewLine) {
                System.out.print("\n");    
                nodesPrinted = 0;
                nodesPrintedToStartNewLine *= 2;
            }
            System.out.print(heap[i] + "  ");
            nodesPrinted++;
        }
    }

    // ---------------- HELPER METHODS ----------------
    private void expandArr() {
        var previous = heap;
        heap = new int[previous.length * arrExpansionFactor];
        for (int i = 0; i < previous.length; i++)
            heap[i] = previous[i];
    }

    private void shrinkArr() {
        var previous = heap;
        heap = new int[occupiedSlots];
        for (int i = 0; i < occupiedSlots; i ++)
            heap[i] = previous[i];
    }

    private int getIndexOf(int val) {
        for (int i = 0; i < heap.length; i++) {
            if (heap[i] == val)
                return i;
        }

        return -1;
    }

    private void maxHeapifyUp() {
        int lastItemIdx = occupiedSlots - 1;

        int childIdx = lastItemIdx;
        int parentIdx = (childIdx - 1) / 2;

        while (heap[parentIdx] < heap[childIdx]) {
            var parentVal = heap[parentIdx];
            heap[parentIdx] = heap[childIdx];
            heap[childIdx] = parentVal;

            childIdx = parentIdx;
            parentIdx = (childIdx - 1) / 2;
        }
    }

    private void maxHeapifyDown(int startIdx) {
        int parentIdx = startIdx;
        int leftChildIdx = parentIdx * 2 + 1;
        int rightChildIdx = leftChildIdx + 1;

        while (heap[parentIdx] < heap[leftChildIdx] || heap[parentIdx] < heap[rightChildIdx]) {
            var newParentIdx = heap[leftChildIdx] > heap[rightChildIdx] ? leftChildIdx : rightChildIdx;

            var newParentVal = heap[newParentIdx];
            heap[newParentIdx] = heap[parentIdx];
            heap[parentIdx] = newParentVal;
            
            parentIdx = newParentIdx;
            leftChildIdx = parentIdx * 2 + 1;
            rightChildIdx = leftChildIdx + 1;
        }
    }
}

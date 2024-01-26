import java.util.Arrays;
import java.util.HashMap;

public class SortingAlgos {

    public static void bubbleSort(int[] data) {
        int ct = 1;

        while (ct <= data.length - 1) {
            for (int i = 0; i < data.length - 1; i++) {
                if (data[i] <= data[i+1])
                    continue;
                var dummy = data[i];
                data[i] = data[i+1];
                data[i+1] = dummy;
            }
            ct++;
        }
    }

    public static void insertionSort(int[] data) {
        for (int i = 1; i < data.length; i++) {
            var left = i - 1;
            var right = i;
            
            while (left >= 0 && data[left] > data[right]) {
                var dummy = data[right];
                data[right] = data[left];
                data[left] = dummy;
                left--;
                right--;
            }
        }
    }

    public static void selectionSort(int[] data) {
        for (int i = 0; i < data.length; i++) {
            var suffixMin = Integer.MAX_VALUE;
            var suffixMinIdx = -1;

            for (int j = i+1; j < data.length; j++) {
                if (data[j] < suffixMin) {
                    suffixMin = data[j];
                    suffixMinIdx = j;
                }
            }

            if (data[i] <= suffixMin)
                continue;

            var dummy = data[i];
            data[i] = suffixMin;
            data[suffixMinIdx] = dummy;
        }
    }

    /*
     * An implementation of quicksort using lomuto's partitioning scheme
     */
    public static void quickSortLomuto(int[] data) {
        quickSortLomuto(data, 0, data.length);
    }

    /*
     * end is exclusive
     */
    private static void quickSortLomuto(int[] data, int start, int end) {
        if (start >= end - 1) return;

        var pivot = data[end - 1];

        var divisionIdx = start - 1;
        
        for (int i = start; i < end; i++) {
            if (data[i] <= pivot) {
                divisionIdx++;

                // Swapping data[i] with data[pointOfDivision]
                var dummy = data[divisionIdx];
                data[divisionIdx] = data[i];
                data[i] = dummy;
            }
        }

        quickSortLomuto(data, start, divisionIdx);
        quickSortLomuto(data, divisionIdx, end);
    }

    /*
     * An implementation of quicksort using hoare's partitioning scheme
     */
    public static void quickSortHoare(int[] data) {
        quickSortHoare(data, 0, data.length);
    }

    private static void quickSortHoare(int[] data, int start, int end) {
        if (start >= end - 1) return;

        int pivotVal = data[(start + end)/2];

        int left = start - 1;
        int right = end;

        while (true) {
            do left++;
            while (data[left] < pivotVal);

            do right--;
            while (data[right] > pivotVal);

            if (left >= right) break;

            var dummy = data[left];
            data[left] = data[right];
            data[right] = dummy;
        }

        quickSortHoare(data, start, left);
        quickSortHoare(data, left, end);
    }

    /*
     * This isn't an in-place implementation
     */
    public static void mergeSort(int[] data) {
        mergeSort(data, 0, data.length, new int[data.length]);
    }

    /*
     * This isn't an in-place implementation
     * end is exclusive
     */
    private static void mergeSort(int[] data, int start, int end, int[] tmp) {
        if (start >= end - 1) return;

        int midIdx = (start + end) / 2;
        mergeSort(data, start, midIdx, tmp);
        mergeSort(data, midIdx, end, tmp);

        // Merging stage
        int left = start;
        int right = midIdx;

        int tmpIdx = start;

        while (left < midIdx || right < end) {
            if (right == end || (left < midIdx && data[left] <= data[right])) {
                tmp[tmpIdx] = data[left];
                left++;
            }
            else {
                tmp[tmpIdx] = data[right];
                right++;
            }
            tmpIdx++;
        }

        // Copying tmp back into data
        for (int i = start; i < end; i++) {
            data[i] = tmp[i];
        }
    }

    public static void countingSort(int[] data) {
        // Represents the number of occurences of an element inside the array
        var occurence = new HashMap<Integer, Integer>();
        
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        // Building the occurence map
        for (var e : data) {
            if (occurence.containsKey(e))
                occurence.replace(e, occurence.get(e) + 1);
            else
                occurence.put(e, 1);
            
            if (e < min)
                min = e;
            if (e > max)
                max = e;
        }

        /*
         * Builds the sorted array:
         * We're not gonna create a new array. Instead, we're gonna reuse the original array by
         * replacing each element, say at index i, with the the ith in-the-sorted-order element.
         */

        
        int idx = 0;
        for (int i = min; i <= max; i++) {
            if (!occurence.containsKey(i))
                continue;
            
            int count = occurence.get(i);
            int j = 0;
            while (j < count) {
                data[idx] = i;
                idx++;
                j++;
            }
        }
    }
}

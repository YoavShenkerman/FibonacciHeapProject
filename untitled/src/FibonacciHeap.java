/**
 * FibonacciHeap
 * An implementation of Fibonacci heap over positive integers.
 *
 */
public class FibonacciHeap
{
    public int c;
    public HeapNode first = null;
    public HeapNode min = null;
    public int links = 0;
    public int cuts = 0;
    public int size = 0;
    public int trees = 0;

    /**
     *
     * Constructor to initialize an empty heap.
     * pre: c >= 2.
     *
     */
    public FibonacciHeap(int c)
    {
        this.c = c;
    }

    /**
     *
     * pre: key > 0
     * Insert (key,info) into the heap and return the newly generated HeapNode.
     *
     */
    public HeapNode insert(int key, String info) {
        this.trees++;
        this.size++;
        if (this.first == null){
            this.first = new HeapNode(key, info);
            this.min = this.first;
            this.first.next = this.first;
            this.first.prev = this.first;
            return this.first;
        }
        else{
            HeapNode last = this.first.prev;
            last.next = new HeapNode(key, info);
            HeapNode newNode = last.next;
            newNode.prev = last;
            newNode.next = this.first;
            this.first.prev = newNode;
            if (newNode.key < this.min.key){
                this.min = newNode;
            }
            return newNode;
        }
    }

    /**
     *
     * Return the minimal HeapNode, null if empty.
     *
     */
    public HeapNode findMin()
    {
        return this.min;
    }


    /**
     *
     * Delete the minimal item.
     * Return the number of links.
     *
     */
    public int deleteMin() {
        java.util.ArrayList<Object> buckets = new java.util.ArrayList<>();
        if (this.size == 0) {
            return 0; // Nothing to delete
        }

        if (this.size == 1) {
            this.min = null;
            this.first = null;
            this.trees = 0;
            this.size = 0;
            return 0;
        }

        int linksBefore = this.links;

        HeapNode minToDelete = this.min;
        this.size--;

        // Step 1: Add all children of min to root list
        HeapNode minChild = minToDelete.child;
        if (minChild != null) {
            HeapNode current = minChild;
            do {
                HeapNode next = current.next;
                current.parent = null; // Important: remove parent pointer
                current = next;
            } while (current != minChild);
        }

        // Step 2: Remove min from root list and merge children into root list
        HeapNode newRootList = null;

        // Add all roots except the min
        if (this.trees > 1) {
            HeapNode current = minToDelete.next;
            HeapNode last = null;

            while (current != minToDelete) {
                HeapNode next = current.next;
                if (newRootList == null) {
                    newRootList = current;
                    current.next = current;
                    current.prev = current;
                    last = current;
                }
                else {
                    // Insert into circular list
                    current.next = newRootList;
                    current.prev = last;
                    last.next = current;
                    newRootList.prev = current;
                    last = current;
                }
                current = next;
            }
        }

        // Add children of min to root list
        if (minChild != null) {
            if (newRootList == null) {
                newRootList = minChild;
            }
            else {
                // Merge two circular lists
                HeapNode lastRoot = newRootList.prev;
                HeapNode lastChild = minChild.prev;

                lastRoot.next = minChild;
                minChild.prev = lastRoot;
                lastChild.next = newRootList;
                newRootList.prev = lastChild;
            }
        }

        if (newRootList == null) {
            this.min = null;
            this.first = null;
            this.trees = 0;
            return this.links - linksBefore;
        }

        // Step 3: Consolidate trees of same rank
        HeapNode current = newRootList;
        do {
            HeapNode next = current.next;

            // Ensure ArrayList is large enough
            while (current.rank >= buckets.size()) {
                buckets.add(null);
            }

            if (buckets.get(current.rank) == null) {
                buckets.set(current.rank, current);
            }
            else {
                // Consolidate trees of same rank
                HeapNode existing = (HeapNode)buckets.get(current.rank);
                buckets.set(current.rank, null);

                HeapNode consolidated = consolidate(existing, current);

                // Keep consolidating until we find an empty slot
                while (consolidated.rank < buckets.size() && buckets.get(consolidated.rank) != null) {
                    HeapNode other = (HeapNode)buckets.get(consolidated.rank);
                    buckets.set(consolidated.rank, null);
                    consolidated = consolidate(consolidated, other);
                }

                // Ensure ArrayList is large enough for the consolidated node
                while (consolidated.rank >= buckets.size()) {
                    buckets.add(null);
                }

                buckets.set(consolidated.rank, consolidated);
            }
            current = next;
        } while (current != newRootList);

        // Step 4: Rebuild root list from consolidated trees and find new min
        HeapNode newFirst = null;
        HeapNode newMin = null;
        HeapNode last = null;
        int treeCount = 0;

        for (int i = 0; i < buckets.size(); i++) {
            if (buckets.get(i) != null) {
                treeCount++;
                if (newFirst == null) {
                    newFirst = (HeapNode) buckets.get(i);
                    newMin = (HeapNode) buckets.get(i);
                    ((HeapNode)buckets.get(i)).next = (HeapNode)buckets.get(i);
                    ((HeapNode)buckets.get(i)).prev = (HeapNode)buckets.get(i);
                    last = (HeapNode) buckets.get(i);
                } else {
                    // Insert into circular list
                    ((HeapNode)buckets.get(i)).next = newFirst;
                    ((HeapNode)buckets.get(i)).prev = last;
                    last.next = (HeapNode)buckets.get(i);
                    newFirst.prev = (HeapNode)buckets.get(i);
                    last = (HeapNode)buckets.get(i);

                    if (((HeapNode)buckets.get(i)).key < newMin.key) {
                        newMin = (HeapNode)buckets.get(i);
                    }
                }
            }
        }

        this.first = newFirst;
        this.min = newMin;
        this.trees = treeCount;

        return this.links - linksBefore;
    }
    // Helper method for consolidating two trees
    public HeapNode consolidate(HeapNode tree1, HeapNode tree2) {
        if (tree1.key <= tree2.key) {
            return link(tree1, tree2);
        } else {
            return link(tree2, tree1);
        }
    }


    public HeapNode link(HeapNode tree1, HeapNode tree2) {
        tree2.parent = tree1;

        if (tree1.child == null) {
            tree1.child = tree2;
            tree2.next = tree2;
            tree2.prev = tree2;
        } else {
            // Insert tree2 into child list
            HeapNode lastChild = tree1.child.prev;
            tree2.next = tree1.child;
            tree2.prev = lastChild;
            lastChild.next = tree2;
            tree1.child.prev = tree2;

            // 🧠 Update tree1.child to point to the child with highest rank
            if (tree2.rank > tree1.child.rank) {
                tree1.child = tree2;
            }
        }

        tree1.rank++;
        this.links++;
        return tree1;
    }

    /**
     *
     * cuts x when y is x.parent
     *
     */
    public void cut(HeapNode x, HeapNode y){
        this.cuts++;
        this.trees++;
        x.parent = null;
        x.mark = 0;
        y.rank = y.rank - 1;

        if (x.next == x){
            y.child = null;
        }
        else{
            y.child = x.next;
            x.prev.next = x.next;
            x.next.prev = x.prev;
        }
        HeapNode last = this.first.prev;
        last.next = x;
        x.prev = last;
        x.next = this.first;
        this.first.prev = x;
        if (x.key < this.min.key){
            this.min = x;
        }
    }
    public int cascadingCut(HeapNode x, HeapNode y){
        cut(x, y);
        int cnt = 1;
        if (y.parent != null){
            if (y.mark != this.c-1){
                y.mark++;
            }
            else{
                cnt += cascadingCut(y, y.parent);
            }
        }
        return cnt;
    }
    /**
     *
     * pre: 0<diff<x.key
     * Decrease the key of x by diff and fix the heap.
     * Return the number of cuts.
     *
     */
    public int decreaseKey(HeapNode x, int diff) {
        if (x == this.min){
            x.key -= diff;
            return 0;
        }
        int cuts = 0;
        x.key = x.key - diff;
        if (x.parent != null && x.key < x.parent.key){
            cuts = cascadingCut(x, x.parent);

        }
        else{
            if (x.key < this.min.key){
                this.min = x;
            }
        }
        return cuts;
    }

    /**
     *
     * Delete the x from the heap.
     * Return the number of links.
     *
     */
    public int delete(HeapNode x) {
        if (x != null){
            decreaseKey(x,x.key);
            return deleteMin();
        }
        return 0;
    }


    /**
     *
     * Return the total number of links.
     *
     */
    public int totalLinks() {
        return this.links;
    }


    /**
     *
     * Return the total number of cuts.
     *
     */
    public int totalCuts() {
        return this.cuts;
    }


    /**
     *
     * Meld the heap with heap2
     *
     */
    public void meld(FibonacciHeap heap2) {
        HeapNode last1 = this.first.prev;
        HeapNode last2 = heap2.first.prev;
        last1.next = heap2.first;
        heap2.first.prev = last1;
        last2.next = this.first;
        this.first.prev = last2;

        if (heap2.min.key < this.min.key){
            this.min = heap2.min;
        }
        this.size = this.size + heap2.size;
        this.trees = this.trees + heap2.trees;
        this.links = this.links + heap2.links;
        this.cuts = this.cuts + heap2.cuts;

        heap2.min = null;
        heap2.first = null;
        heap2.size = 0;
        heap2.trees = 0;
        heap2.links = 0;
        heap2.cuts = 0;
    }

    /**
     *
     * Return the number of elements in the heap
     *
     */
    public int size() {
        return this.size;
    }


    /**
     *
     * Return the number of trees in the heap.
     *
     */
    public int numTrees() {
        return this.trees;
    }

    /**
     * Class implementing a node in a Fibonacci Heap.
     *
     */
    public static class HeapNode{
        public int key;
        public String info;
        public HeapNode child = null;
        public HeapNode next = null;
        public HeapNode prev = null;
        public HeapNode parent = null;
        public int rank;
        public int mark = 0;

        public HeapNode(int key, String info){
            this.key = key;
            this.info = info;
            this.rank = 0;
        }

    }
}
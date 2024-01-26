/*
 * This is an implementation of a BST
 * It includes 3 subtree properties: height, depth, size
 * It also includes basic functions such as
 * - insert
 * - remove
 * - min
 * - max
 * - getTraversalOrder(x): A traversal order of x is a string of tree's nodes' values
 *                          where all nodes' values in x's left subtree comes before x's value in the string, and
 *                          all nodes' values in x's right subtree comes after x's value in the string
 * It also includes helper methods such as updateHeight(), getMinNodeAndParent(), getMaxNodeAndParent()
 */

public class BinarySearchTreeNode {
    private BinarySearchTreeNode left;
    private BinarySearchTreeNode right;
    private int val;
    
    // If THIS NODE is the root, this represents the height of the tree
    // Otherwise, it's the subtree height rooted at THIS NODE
    private int height = 0;

    // Represents the depth of THIS NODE in the OVERALL tree
    private int depth = 0;

    // The size of the subtree rooted at this node
    private int size = 1;

    public BinarySearchTreeNode(int val) { 
        this.val = val;
    }

    public BinarySearchTreeNode(int val, BinarySearchTreeNode left,  BinarySearchTreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    public void insertNode(int val) {
        this.insertNode(new BinarySearchTreeNode(val), 1);
    }

    /*
     * Inserts a node recursively. The recursive nature makes it easier to update subtree properties
     */
    private void insertNode(BinarySearchTreeNode newNode, int newNodeDepth) {
        if (newNode.val >= this.val) {
            if (this.right != null)
                this.right.insertNode(newNode, newNodeDepth + 1);
            else {
                newNode.depth = newNodeDepth;
                this.right = newNode;
            }
        }
        else {
            if (this.left != null)
                this.left.insertNode(newNode, newNodeDepth + 1);
            else {
                newNode.depth = newNodeDepth;
                this.left = newNode;
            }
        }

        this.updateHeight();
        this.size++;
    }

    /*
     * Helper method to update heights of nodes
     */
    private void updateHeight() {
        var leftHeight = this.left == null ? -1 : this.left.height;
        var rightHeight = this.right == null ? -1 : this.right.height;

        if (rightHeight > leftHeight)
            this.height = rightHeight + 1;
        else
            this.height = leftHeight + 1;
    }

    private void updateSize() {
        var leftSize = this.left == null ? 0 : this.left.size;
        var rightSize = this.right == null ? 0 : this.right.size;
        this.size = leftSize + rightSize + 1;
    }

    /*
     * Returns the traversal order of the subtree of this node
     */
    public String getTraversalOrder() {
        StringBuilder traversalOrder = new StringBuilder();

        if (this.left != null) {
            traversalOrder.append( this.left.getTraversalOrder() );
        }

        traversalOrder.append(this.val);
        traversalOrder.append(", ");

        if (this.right != null)
            traversalOrder.append( this.right.getTraversalOrder() );

        return traversalOrder.toString();
    }

    /*
     * Returns the value of the node with the minimum value in the tree
     */
    public int getMin() {
        if (this.left == null)
            return this.val;
        else
            return this.left.getMin();
    }

    /*
     * Returns the value of the node with the maximum value in the tree
     */
    public int getMax() {
        if (this.right == null) 
            return this.val;
        else  
            return this.right.getMax();
    }

    public void removeNode(int val) {
        var targetNodeAndParent = getNodeWithValAndItsParent(val, null);

        // If the target node isn't found
        if (targetNodeAndParent == null) return;
        targetNodeAndParent[0].removeNode(targetNodeAndParent[1]);
    }

    /*
     * Removes a node by replacing the THIS NODE's value with new value of the root
     * of the post-removal tree
     */
    private void removeNode(BinarySearchTreeNode parent) {
        // If the tree rooted at THIS NODE only has 1 node, which is itself
        // then, disown self from parent
        if (this.left == null && this.right == null) {
            if (this == parent.left) 
                parent.left = null;
            else
                parent.right = null;
            return;
        }

        if (this.left != null) {
            var predecessorAndParent = this.getPredecessorAndParent();
            var predecessor = predecessorAndParent[0];

            // Predecessor's parent can't be null, because the IF-block above
            // is true when the this.left != null, thereby the predecessor's parent cannot := null
            var predecessorParent = predecessorAndParent[1];

            var predecessorVal = predecessor.val;
            predecessor.val = this.val;
            this.val = predecessorVal;
            
            predecessor.removeNode(predecessorParent);
            this.left.updateHeightAndSizeForAncestorsOfMaxNode();
        } 
        // Otherwise, we know that the right child isn't null
        else {
            var successorAndParent = getSuccessorAndParent();
            var successor = successorAndParent[0];

            // Since the right child isn't null, the successor's parent can't be null
            var successorParent = successorAndParent[1];

            var successorVal = successor.val;
            successor.val = this.val;
            this.val = successorVal;

            successor.removeNode(successorParent);
            this.right.updateHeightAndSizeForAncestorsOfMinNode();
        }
        updateHeight();
        updateSize();
    }

    /*
     * The "ancestors" refers to the ancestors from THIS NODE down
     */
    private void updateHeightAndSizeForAncestorsOfMaxNode() {
        if (this.right != null) 
            this.right.updateHeightAndSizeForAncestorsOfMaxNode();
        updateHeight(); 
        updateSize();
    }

    /*
     * The "ancestors" refers to the ancestors from THIS NODE down
     */
    private void updateHeightAndSizeForAncestorsOfMinNode() {
        if (this.left != null)
            this.left.updateHeightAndSizeForAncestorsOfMinNode();
        updateHeight();
        updateSize();
    }

    /*
     * Gets the maximum node in the subtree rooted at THIS NODE.
     * This method returns [THIS NODE, null] if THIS NODE is the maximum node
     * - parent: refers to the parent of THIS NODE.
     */
    private BinarySearchTreeNode[] getMaxNodeAndParent(BinarySearchTreeNode parent) {
        if (this.right == null)
            return new BinarySearchTreeNode[] { this, parent };
        return this.right.getMaxNodeAndParent(this);
    }

    private BinarySearchTreeNode[] getPredecessorAndParent() {
        if (this.left == null)
            return new BinarySearchTreeNode[] { this, null };
        return this.left.getMaxNodeAndParent(this);
    }

    private BinarySearchTreeNode[] getMinNodeAndParent(BinarySearchTreeNode parent) {
        if (this.left == null)
            return new BinarySearchTreeNode[] { this, parent };
        return this.left.getMinNodeAndParent(this);
    }

    private BinarySearchTreeNode[] getSuccessorAndParent() {
        if (this.right == null)
            return null;
        return this.right.getMinNodeAndParent(this);
    }

    private BinarySearchTreeNode[] getNodeWithValAndItsParent(int val, BinarySearchTreeNode parent) {
        if (this.val == val)
            return new BinarySearchTreeNode[] { this, parent };

        if (val >= this.val) 
            if (this.right != null)
                return this.right.getNodeWithValAndItsParent(val, this);
            else 
                return null;
        else
            if (this.left != null)
                return this.left.getNodeWithValAndItsParent(val, this);
            else
                return null;
    }
}

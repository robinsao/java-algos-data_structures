import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/*
 * Haven't finished implementing remove method yet
 * 
 * In hindsight, it's better to create another class called AVLTree which represents
 * the actual tree, and this class just represents a node in the tree: stores val, left, right, height
 * balance, etc.
 */
public class AVLTreeNode {
    private float val;
    private int height;
    private AVLTreeNode left;
    private AVLTreeNode right;

    public AVLTreeNode(float val) {
        this.val = val;
    }

    public AVLTreeNode findNode(float val) {
        if (val == this.val) 
            return this;
        if (val > this.val)
            return this.right == null ? null : this.right.findNode(val);
        
        return this.left == null ? null : this.left.findNode(val);
    }

    public void addNode(float val) {
        // Finding the spot to insert the new node
        if (val >= this.val) {
            if (this.right == null)
                this.right = new AVLTreeNode(val);
            else
                this.right.addNode(val);
        }
        else {
            if (this.left == null)
                this.left = new AVLTreeNode(val);
            else
                this.left.addNode(val);
        }
        
        // Updating the height and rebalance if necessary
        updateHeight();
        balance();
    }

    public void removeNode(float val) {
        this.removeNode(val, null);
    }

    public void removeNode(float val, AVLTreeNode parent) {
        // Steps:
        // 1. Remove the node
        // 2. Update height
        // 3. Rebalance the tree if necessary

        // TODO: Implement rebalance 

        // The reason why we need a parent is because the node on which this method is called on
        // might be a leaf node

        if (val > this.val) {
            if (this.right != null)
                this.right.removeNode(val, this);
        }
        else if (val < this.val) {
            if (this.left != null)
                this.left.removeNode(val, this);
        }
        // Case this.val == val, meaning that this node is the node to be removed
        else {
            if (this.left != null) {
                var predecessorAndAncestors = this.getSubtreePredecessorAndAncestors();
                var predecessor = predecessorAndAncestors.get( predecessorAndAncestors.size() - 1 );
                var predecessorParent = predecessorAndAncestors.size() == 1 ? this : predecessorAndAncestors.get( predecessorAndAncestors.size() - 2 );

                // In other words, the predecessor is this.left
                if (predecessorParent == this) {
                    this.val = this.left.val;
                    this.left = null;
                }
                else {
                    this.swapValues(predecessor);
                    predecessor.removeNode(predecessor.val, predecessorParent);

                    this.updateHeightAndBalanceNodesInReverse(predecessorAndAncestors);
                }
                

                // updating this.height and rebalancing "this" node is taken care of at near the end of this method definition
            }
            else if (this.right != null) {
                var successorAndAncestors = this.getSubtreeSuccessorAndAncestors();
                var successor = successorAndAncestors.get( successorAndAncestors.size() - 1 );
                var successorParent = successorAndAncestors.size() == 1 ? this : successorAndAncestors.get( successorAndAncestors.size() - 2 );

                if (successorParent == this) {
                    this.val = this.right.val;
                    this.right = null;
                }
                else {
                    this.swapValues(successor);
                    successor.removeNode(successor.val, successorParent);
                    
                    this.updateHeightAndBalanceNodesInReverse(successorAndAncestors);
                }
                
                // updating this.height and rebalancing "this" node is taken care of at near the end of this method definition
            }
            // Subcase: This node is a leaf node
            else {
                // If "this" node is the root node, there's nothing to do
                if (parent == null) return;

                if (this == parent.left) 
                    parent.left = null;
                else
                    parent.right = null;
            }
        }

        this.updateHeight();
        this.balance();
    }

    // Prints the tree in BFS order
    public void printTree() {
        var queue = new LinkedList<AVLTreeNode>();
        var currDepthSize = 1;

        queue.addLast(this);
        while (!queue.isEmpty()) {
            var head = queue.removeFirst();
            
            System.out.print(head.val + " ");

            if (head.left != null)
                queue.addLast(head.left);
                
            if (head.right != null)
                queue.addLast(head.right);

            currDepthSize--;
            if (currDepthSize == 0) {
                System.out.println();
                currDepthSize = queue.size();
            }
        }
    }

    /*
     * If the node needs no balancing this method will do nothing; otherwise, it will balance the node
     */
    private void balance() {
        var leftHeight = this.left == null ? -1: this.left.height;
        var rightHeight = this.right == null ? -1 : this.right.height;

        if (Math.abs(leftHeight - rightHeight) <= 1) return;

        // Left-heavy
        if (leftHeight > rightHeight) {
            // What about this.left itself? Is it left heavy or right heavy?
            var leftLeftHeight = this.left.left == null ? -1: this.left.left.height;
            var leftRightHeight = this.left.right == null ? -1 : this.left.right.height;

            if (leftLeftHeight == -1 && leftRightHeight == -1) return;

            // left heavy
            if (leftLeftHeight > leftRightHeight) {
                this.rotateRight();
            }
            // right heavy 
            else {
                this.left.rotateLeft();
                this.rotateRight();
            }
            
            
        }
        // Right-heavy
        else {
            // What about this.right itself? Is it left heavy or right heavy?
            if (right.left == null || right.right == null) return;

            // right heavy
            if (right.right.height > right.left.height) {
                this.rotateLeft();
            }
            // left heavy
            else {
                this.right.rotateRight();
                this.rotateLeft();
            }
        }
    }

    private void rotateRight() {
        var thisVal = this.val;
        this.val = left.val;
        left.val = thisVal;

        var left = this.left;
        this.left = this.left.left;

        left.left = left.right;
        left.right = this.right;
        this.right = left;
        this.right.updateHeight();
        this.updateHeight();
    }

    private void rotateLeft() {
        var thisVal = this.val;
        this.val = this.right.val;
        this.right.val = thisVal;

        var right = this.right;
        this.right = this.right.right;
        right.right = right.left;
        right.left = this.left;
        this.left = right;

        this.left.updateHeight();
        this.updateHeight();
    }

    /*
     * UPdates the height of this node
     */
    private void updateHeight() {
        var leftHeight = this.left == null ? -1: this.left.height;
        var rightHeight = this.right == null ? -1 : this.right.height;

        this.height = Math.max(leftHeight, rightHeight) + 1;
    }

    /*
     * This method returns the predecessor and its ancestors in the subtree rooted at this node.
     * This means that if the predecessor is an ancestor of this node, then this method will return an empty list.
     * 
     * Returns an array list where the last item is the predecessor itself, and the nodes
     * before it are its ancestors from lowest to highest; so the first node adjacent to it
     * is its parent, the 2nd node away is its grandparent, and so on. The first node in the list
     * is left node of the node on which this method is called on
     */
    private ArrayList<AVLTreeNode> getSubtreePredecessorAndAncestors() {
        var predecessorAndAncestors = new ArrayList<AVLTreeNode>();

        var predecessor = this.left;

        while (predecessor != null) {
            predecessorAndAncestors.add(predecessor);
            predecessor = predecessor.right;
        }

        return predecessorAndAncestors;
    }
    
    /*
     * Updates the heights and balance nodes in the given arraylist from the biggest index to the smallest index
     */
    private void updateHeightAndBalanceNodesInReverse(ArrayList<AVLTreeNode> nodes) {
        var size = nodes.size();

        for (int i = size - 1; i >= 0; i--) {
            nodes.get(i).updateHeight();
            nodes.get(i).balance();
        }
    }

    /*
     * This method returns the successor and its ancestors in the subtree rooted at this node.
     * This means that if the successor is an ancestor of this node, then this method will return an empty list
     * 
     * Returns an array list where the last item is the successor itself, and the nodes
     * before it are its ancestors from lowest to highest; so the first node adjacent to it
     * is its parent, the 2nd node away is its grandparent, and so on. The first node in the list
     * is left node of the node on which this method is called on
     */
    private ArrayList<AVLTreeNode> getSubtreeSuccessorAndAncestors() {
        var successorAndAncestors = new ArrayList<AVLTreeNode>();

        var successor = this.right;

        while (successor != null) {
            successorAndAncestors.add(successor);
            successor = successor.left;
        }

        return successorAndAncestors;
    }

    /*
     * Swaps values with another node
     */
    private void swapValues(AVLTreeNode node) {
        var thisVal = this.val;
        this.val = node.val;
        node.val = thisVal;
    }


}

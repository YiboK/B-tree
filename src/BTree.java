import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * B+Tree Structure
 * Key - StudentId
 * Leaf Node should contain [ key,recordId ]
 */
class BTree {

    /**
     * Pointer to the root node.
     */
    private BTreeNode root;
    /**
     * Number of key-value pairs allowed in the tree/the minimum degree of B+Tree
     **/
    private int t;

    BTree(int t) {
        this.root = null;
        this.t = t;
    }

    long search(long studentId) {
        /**
         * TODO: Implement this function to search in the B+Tree. Return recordID for the given
         * StudentID. Otherwise, print out a message that the given studentId has not been found in the
         * table and return -1.
         */
        BTreeNode cur = root;
        // go to a leaf node
        while (cur.leaf == false) {
            System.out.println("1111111111111111111111");
            for (int i = 0; i < cur.n; i++) {
                if (studentId < cur.keys[i]) {
                    cur = cur.children[i];
                    System.out.println("STU: Id" + cur.keys[i]);
                    break;
                } else if (studentId >= cur.keys[i] && i == cur.n - 1) {
                    cur = cur.children[cur.n];
                    break;
                }
            }
        }
        // check the lead node
        for (int i = 0; i < 2 * t - 1; i++) {
            System.out.println("STU: Id" + cur.keys[i]);
            if (cur.keys[i] == studentId) {
                return cur.values[i];
            }
        }

        System.out.println("the given studentId has not been found in the table");

        return -1;
    }

    /**
     * This method is to insert a new value into this tree
     *
     * @param student contains the information will be inserted
     * @return this tree
     */
    BTree insert(Student student) {

        //start a new root
        if (this.root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = student.studentId;
            root.values[0] = student.recordId;
            System.out.println("****" + root.keys[0]);
            root.n++;
            return null;
        }
        BTreeNode cur = root;
        // go to a leaf node
        while (cur.leaf == false) {
            System.out.println("Root n: " + cur.n + " " + cur.keys[0]);

            for (int i = 0; i < cur.n; i++) { // error!!!!!!!!!!!
                if (student.studentId < cur.keys[i]) {
                    cur = cur.children[i];
                    break;
                } else if (student.studentId >= cur.keys[i] && i == cur.n - 1) {
                    cur = cur.children[cur.n];
                    break;
                }
            }
        }

        // base case
        // do not need to split
        if (cur.n < t * 2 - 1) {
            cur.keys[cur.n] = student.studentId;
            cur.values[cur.n] = student.recordId;
            cur.n++;
            long tempKey[] = new long[cur.n];
            long tempValue[] = new long[cur.n];
            System.arraycopy(cur.keys, 0, tempKey, 0, cur.n);
            System.arraycopy(cur.values, 0, tempValue, 0, cur.n);
            Arrays.sort(tempKey);
            System.arraycopy(tempKey, 0, cur.keys, 0, cur.n);

            int keyIndex = -1;
            for (int i = 0; i < cur.n; i++) {
                // get key index
                if (tempKey[i] == student.studentId) {
                    keyIndex = i;
                    break;
                }
            }

            for (int i = cur.n - 1; i > keyIndex; i--) {
                tempValue[i] = tempValue[i - 1];
            }
            tempValue[keyIndex] = student.recordId;
            System.arraycopy(tempValue, 0, cur.values, 0, cur.n);


        } else {
            // special case
            // need to split node
            BTreeNode left = new BTreeNode(t, true);
            BTreeNode right = new BTreeNode(t, true);

            long tempKey[] = new long[2 * t];
            long tempValue[] = new long[2 * t];
            // copy array's keys
            for (int i = 0; i < 2 * t - 1; i++) {
                tempKey[i] = cur.keys[i];
            }
            tempKey[2 * t - 1] = student.studentId;
            // copy array's values
            for (int i = 0; i < 2 * t - 1; i++) {
                tempValue[i] = cur.values[i];
            }
            tempValue[2 * t - 1] = student.recordId;
            // sort key
            Arrays.sort(tempKey);
            // sort value
            int keyIndex = -1;
            for (int i = 0; i < 2 * t; i++) {
                // get key index
                if (tempKey[i] == student.studentId) {
                    keyIndex = i;
                    break;
                }
            }

            for (int i = cur.n - 1; i > keyIndex; i--) {
                tempValue[i] = tempValue[i - 1];
            }
            tempValue[keyIndex] = student.recordId;
            System.arraycopy(tempValue, 0, cur.values, 0, cur.n);


            // copy to left (which will be copied back to cur)
            System.out.print(" Left keys: ");
            for (int i = 0; i < t; i++) {
                // copy key
                left.keys[i] = tempKey[i];
                System.out.print(" " + left.keys[i]);
                left.values[i] = tempValue[i];
                left.n++;
            }

            System.out.print("\nRight keys: ");

            // copy to left while will be send to helper method
            for (int i = t; i < cur.n + 1; i++) {
                right.keys[i - t] = tempKey[i];
                System.out.print(" " + right.keys[i - t]);
                right.values[i - t] = tempValue[i];
                right.n++;
            }

            //copy back to cur because left is a temp node
            System.arraycopy(left.keys, 0, cur.keys, 0, cur.n);
            System.arraycopy(left.values, 0, cur.values, 0, cur.n);
            System.arraycopy(left.children, 0, cur.children, 0, cur.n);
            cur.n = left.n;

            //set next to keep the next pointer between two pointers right
            if (!cur.equals(root))
                right.next = cur.next;
            cur.next = right;

            //call helper method to keep tree's properties
            insertHelper(cur.parent, tempKey[t], cur, right);
        }

        for (int i = 0; i < 2 * t - 1; i++) {
            System.out.print(root.keys[i] + "  ");
        }
        System.out.println("  " + root.n);


        for (int x = 0; x < 2 * t; x++) {
            if (root.children[x] != null) {
                for (int i = 0; i < 2 * t - 1; i++) {
                    System.out.print(root.children[x].keys[i] + "cur.n: " + root.children[x].n + " ");
                    /**
                     * if (root.children[x].leaf == false && root.children[x].children[i] != null) { for (int
                     * y = 0; y < 2 * t -1; y++) System.out.print(root.children[x].children[i].keys[y] + " ");
                     * } System.out.print(" ");
                     */
                }
                System.out.print("|Parent: " + root.children[x].parent.keys[0] + " |");
            }
            System.out.print("   ");
        }
        System.out.println();

        for (int x = 0; x < 2 * t; x++) {
            if (root.children[x] != null) {
                for (int i = 0; i < 2 * t - 1; i++) {
                    if (root.children[x].leaf == false && root.children[x].children[i] != null) {
                        for (int y = 0; y < 2 * t - 1; y++)
                            System.out.print(root.children[x].children[i].keys[y] + " ");
                        if (root.children[x].children[i].next != null)
                            System.out.print("Next: " + root.children[x].children[i].next.keys[0] + "cur.n: " + root.children[x].children[i].n + " | ");
                    }
                    System.out.print(" |Parent: " + root.children[x].keys[0] + " |");

                }
            }
        }
        System.out.println();

        return this;
    }

    /**
     * This method will be called recursively to split the b+Tree
     *
     * @param cur   current node
     * @param key   the key value will be send to upper layer
     * @param midLC the Left child of the key
     * @param midRC the right child of the key
     * @return a integer of which indicates of branches
     */
    int insertHelper(BTreeNode cur, long key, BTreeNode midLC, BTreeNode midRC) {

        //base case 1
        //cur is null (we need to generate a new root)
        //create a new root
        if (cur == null) {
            cur = new BTreeNode(t, false);
            root = cur;
            System.out.println("Key: " + key);
            insertHelper(cur, key, midLC, midRC);
            return -1;
        }

        //base case 2
        //current node is not full
        if (cur.n < 2 * t - 1) {
            int cPosition = -1;
            cur.keys[cur.n] = key;
            cur.n++;
            long tempKey[] = new long[cur.n];
            System.arraycopy(cur.keys, 0, tempKey, 0, cur.n);
            Arrays.sort(tempKey);
            System.arraycopy(tempKey, 0, cur.keys, 0, cur.n);

            //check the current position of key
            for (int i = 0; i < cur.n; i++) {
                if (tempKey[i] == key)
                    cPosition = i;
            }

            //right shift other children already
            for (int i = cur.n; i > cPosition; i--) {
                cur.children[i] = cur.children[i - 1];
            }
            //add new children
            cur.children[cPosition] = midLC;
            cur.children[cPosition + 1] = midRC;
            //renew parents
            midLC.parent = cur;
            midRC.parent = cur;
            return cPosition;
        }


        // recursive part
        long tempKey[] = new long[2 * t];
        BTreeNode tempChildren[] = new BTreeNode[2 * t + 1];
        // copy array's keys
        for (int i = 0; i < 2 * t - 1; i++) {
            tempKey[i] = cur.keys[i];
        }
        tempKey[2 * t - 1] = key;
        // sort
        Arrays.sort(tempKey);
        // copy to temp children
        // check the position of the children
        int cPosition = -1;

        for (int i = 0; i < 2 * t; i++) {
            if (tempKey[i] == key)
                cPosition = i;
        }

        //save the children to the right place into tempchildren
        for (int i = 0; i < 2 * t + 1; i++) {
            if (i < cPosition)
                tempChildren[i] = cur.children[i];
            else if (i == cPosition)
                tempChildren[i] = midLC;
            else if (i == cPosition + 1)
                tempChildren[i] = midRC;
            else
                tempChildren[i] = cur.children[i - 1];
        }


        BTreeNode left = new BTreeNode(t, cur.leaf);
        BTreeNode right = new BTreeNode(t, cur.leaf);

        // copy to left, redistributes keys and children
        for (int i = 0; i < t; i++) {
            left.keys[i] = tempKey[i];
            left.children[i] = tempChildren[i];
            left.children[i].parent = left;
            left.n++;
        }
        left.children[t] = tempChildren[t];
        left.children[t].parent = left;

        // copy to right, redistributes keys and children
        for (int i = t; i < cur.n + 1; i++) {
            if (i != t) {
                right.keys[i - t - 1] = tempKey[i];
                right.n++;
            }
            right.children[i - t] = tempChildren[i + 1];
            right.children[i - t].parent = right;
        }

        for (int i = 0; i < 2 * t - 1; i++) {
            System.out.println(left.keys[i] + " " + right.keys[i]);
        }

        insertHelper(cur.parent, tempKey[t], left, right);

        return -1;
    }


    boolean delete(long studentId) {
        // find which child the studentID is in
        BTreeNode curr = root; // store the B tree node we currently in
        int childNum = 0; // record what number is the curr
        while (!curr.leaf) {
            for (int i = 0; i < curr.n; i++) {
                if (studentId < curr.keys[i]) {
                    curr = curr.children[i];
                    childNum = i;
                    break;
                } else if (i == curr.n - 1) {
                    curr = curr.children[curr.n];
                    childNum = i + 1;
                    break;
                }
            }
        }
        // now, we are in a leaf that may contain studentID
        for (int i = 0; i < curr.keys.length; i++) {
            // if we find target studentID, delete it.
            if (studentId == curr.keys[i]) {
                // if target is the last one in node, we simply delete it
                if (i != curr.keys.length - 1) {
                    // if target is not the last one, we need to move other keys
                    for (int j = i; j < curr.keys.length - 1; j++) {
                        curr.keys[j] = curr.keys[j + 1];
                        curr.values[j] = curr.values[j + 1];
                    }
                    curr.keys[curr.n - 1] = 0;
                    curr.values[curr.n - 1] = 0;
                } else {
                    curr.keys[i] = 0;
                    curr.values[i] = 0;
                }

                // update number in B tree
                curr.n--;

                BTreeNode parent = curr.parent;

                // check number of entities in the node
                int num = curr.n;
                //if node is at least half-full, DONE
                if (num >= t) {
                    return true;
                } else if (curr.equals(root) && curr.n <= t - 1) {
                    return true;
                } else {

                    // redistribute

                    // check previous sibling's size
                    if (!parent.children[0].equals(curr)) {
                        // find possible previous sibling
                        BTreeNode preNode = parent.children[0];
                        while (!preNode.next.equals(curr)) {
                            preNode = preNode.next;
                        }

                        int preNum = preNode.n;
                        if (preNum >= t + 1) {
                            // evenly distribute the entities
                            int sum = preNum + num;
                            num = sum / 2;
                            preNum = sum - num;

                            // get the entities from previous node
                            long[] preLast = new long[preNode.n - preNum];
                            long[] preValue = new long[preNode.n - preNum];
                            for (int j = preNum; j < preNode.n; j++) {
                                preLast[j - preNum] = preNode.keys[j];
                                preValue[j - preNum] = preNode.values[j];
                                preNode.keys[j] = 0;
                                preNode.values[j] = 0;
                            }

                            // update number of entities in previous
                            preNode.n = preNum;

                            // move all entities in curr right
                            for (int j = num - curr.n + 1; j > 0; j--) {
                                curr.keys[j] = curr.keys[j - 1];
                                curr.values[j] = curr.values[j - 1];
                            }

                            // copy the entity from previous to curr
                            for (int j = 0; j < preLast.length; j++) {
                                curr.keys[j] = preLast[j];
                                curr.values[j] = preValue[j];
                            }

                            // change immediate parent node
                            parent.keys[childNum - 1] = preLast[0];

                            // update current's n
                            curr.n = num;

                            // we have done
                            return true;
                        }
                    }

                    // if previous sibling does not have enough entities, find possible next sibling's size
                    if (curr.next != null) {
                        int nextNum = curr.next.n;
                        // check if they are true sibling
                        if (curr.next.parent.equals(parent) && nextNum >= t + 1) {

                            // evenly distribute the entities
                            int sum = nextNum + num;
                            num = sum / 2;
                            nextNum = sum - num;

                            // get the entities from next node
                            long[] nextFirst = new long[curr.next.n - nextNum];
                            long[] nextValue = new long[curr.next.n - nextNum];
                            for (int j = 0; j < curr.next.n - nextNum; j++) {
                                nextFirst[j] = curr.next.keys[j];
                                nextValue[j] = curr.next.values[j];
                            }

                            //move entities in next node left
                            for (int j = 0; j < curr.next.keys.length; j++) {
                                if (j == curr.next.keys.length - 1) {
                                    curr.next.keys[j] = 0;
                                    curr.next.values[j] = 0;
                                } else {
                                    curr.next.keys[j] = curr.next.keys[j + 1];
                                    curr.next.values[j] = curr.next.values[j + 1];
                                }
                            }

                            // update number of entities in previous
                            curr.next.n = nextNum;


                            // copy the entity from next
                            for (int j = 0; j < nextFirst.length; j++) {
                                curr.keys[curr.n + j] = nextFirst[j];
                                curr.values[curr.n+ j] = nextValue[j];
                            }

                            // change immediate parent node
                            parent.keys[childNum] = curr.next.keys[0];

                            //update current's n
                            curr.n = num;

                            // we have done
                            return true;
                        }
                    }

                    // if both siblings do not have enough entities, we merge
                    // if curr is the first child of its parent, it does not have a previous sibling
                    if (parent.children[0].equals(curr)) {
                        // we merge curr with its next sibling
                        BTreeNode nextNode = curr.next;
                        for (int j = 0; j < nextNode.n; j++) {
                            curr.keys[num + j] = nextNode.keys[j];
                            curr.values[num + j] = nextNode.values[j];
                        }
                        // change curr.next
                        curr.next = nextNode.next;

                        // update n
                        num = curr.n + nextNode.n;
                        curr.n = num;

                        // update parent
                        updateParent(parent, 0);
                    } else { // if curr is not the first child, we need to check previous and next sibling
                        // find previous sibling first
                        BTreeNode preNode = parent.children[childNum - 1];

                        // merge with previous
                        int preNum = preNode.n;
                        for (int j = preNum; j < preNode.keys.length; j++) {
                            preNode.keys[j] = curr.keys[j - preNum];
                            preNode.values[j] = curr.values[j - preNum];
                        }

                        // change next
                        BTreeNode next = curr.next;
                        curr = preNode;
                        curr.next = next;

                        // update n
                        num = preNum + num;
                        curr.n = num;

                        //update parent
                        updateParent(parent, childNum - 1);
                    }

                    return redistributeParent(parent, parent.n);
                }
            }
        }

        // if we do not find target studentID, return false
        return false;
    }


    /**
     * Helper method to update keys, values, and children of parent node after merge
     *
     * @param parent   the parent to update
     * @param position the position where merge happened
     */
    void updateParent(BTreeNode parent, int position) {
        // move entities in parent node left by 1
        for (int j = position; j < parent.keys.length; j++) {
            if (j == parent.keys.length - 1) {
                parent.keys[j] = 0;
            } else {
                parent.keys[j] = parent.keys[j + 1];
            }
        }
        // update parent's children
        for (int j = position + 1; j < parent.children.length; j++) {
            if (j == parent.children.length - 1) parent.children[j] = null;
            else parent.children[j] = parent.children[j + 1];
        }
        parent.n--;

    }

    /**
     * Helper method to redistribute parent nodes
     *
     * @param parent the node to merge
     * @param num    the number of valid value in parent
     * @return true, if we successfully deal with parent. false, otherwise
     */
    boolean redistributeParent(BTreeNode parent, int num) {
        // base case
        if (parent.equals(root)) return true;

        if (num >= t) return true;

        // find the position of parent in grandparent's children
        int position = 0;
        for (int i = 0; i < parent.parent.children.length; i++) {
            if (parent.parent.children[i].equals(parent)) {
                position = i;
                break;
            }
        }

        // find parent's siblings
        BTreeNode nextNode = null;
        int nextNum = 0;
        if (position < parent.parent.children.length - 1) {
            nextNode = parent.parent.children[position + 1];
        }
        // check if parent has next sibling
        if (nextNode != null) {
            nextNum = nextNode.n;
        }


        BTreeNode preNode = parent.parent.children[0];
        int prePosition = 0; // record the position of preNode
        // if parent is not the first, find its previous
        if (position != 0) {
            preNode = parent.parent.children[position - 1];
            prePosition = position - 1;
        }


        int preNum = preNode.n;
        // check siblings
        // check if we can redistribute with previous
        if (position != 0 && preNum >= t + 1) {
            return redistributePre(parent, preNode, prePosition);
            // check if we can redistribute with next
        } else if (nextNode != null && nextNum >= t + 1) {
            // redistribute with next
            return redistributeNext(parent, nextNode, position);
            // merge them
        } else {
            BTreeNode grandparent = parent.parent;
            if (nextNode == null || nextNum == 0 || preNode != parent) {
                // merge with previous
                mergeHelper(preNode, parent, grandparent, prePosition);
                return true;

            } else if (prePosition == 0) {
                // merge with next
                mergeHelper(parent, nextNode, grandparent, position);

                // deal with higher parent if it has one
                if (!parent.equals(root)) return redistributeParent(parent.parent, parent.parent.n);

                return true;

            }
        }

        return false;
    }

    /**
     * Method to redistribute parent and its previous sibling
     *
     * @param parent      the parent node
     * @param preNode     the parent node's previous sibling
     * @param prePosition the position of previous sibling in grandparent's children list
     * @return true, if successfully redistribute. false, otherwise
     */
    boolean redistributePre(BTreeNode parent, BTreeNode preNode, int prePosition) {
        int preNum = preNode.n;
        int num = parent.n;

        // redistribute with previous
        long preLast = preNode.keys[preNum - 1];
        BTreeNode preChild = preNode.children[preNum];
        preNode.keys[preNum - 1] = 0;
        preNode.children[preNum] = null;

        // update number of entities in previous
        preNode.n--;

        // move all entities in curr right by 1
        for (int j = num; j > 0; j--) {
            if (j == num) parent.children[j + 1] = parent.children[j];
            parent.keys[j] = parent.keys[j - 1];
            parent.children[j] = parent.children[j - 1];
        }

        // copy keys and values from their parent
        parent.keys[0] = parent.parent.keys[prePosition];
        // copy child from previous
        parent.children[0] = preChild;
        preChild.parent = parent;
        parent.n++;

        // change immediate parent node
        parent.parent.keys[prePosition] = preLast;

        // deal with higher parent
        return redistributeParent(parent.parent, parent.parent.n);
    }

    /**
     * Method to redistribute parent and its next sibling
     *
     * @param parent   the parent node
     * @param nextNode the parent node's next sibling
     * @param position the position of parent node in grandparent's children list
     * @return true, if successfully redistribute. false, otherwise
     */
    boolean redistributeNext(BTreeNode parent, BTreeNode nextNode, int position) {
        int nextNum = nextNode.n;
        int num = parent.n;
        // get the first entity in the next node
        long nextFirst = nextNode.keys[0];
        BTreeNode nextChild = nextNode.children[0];

        // move other keys in next node left by 1
        for (int j = 0; j < nextNum - 1; j++) {
            nextNode.keys[j] = nextNode.keys[j + 1];
            nextNode.children[j] = nextNode.children[j + 1];
        }

        nextNode.keys[nextNum - 1] = 0;
        nextNode.children[nextNum - 1] = nextNode.children[nextNum];
        nextNode.children[nextNum] = null;
        // update number of entities in next
        nextNode.n--;

        // copy the entity from their parent
        parent.keys[num] = parent.parent.keys[position];

        // copy child from next
        parent.children[num + 1] = nextChild;
        nextChild.parent = parent;
        parent.n++;

        // change immediate parent node
        parent.parent.keys[position] = nextFirst;
        // deal with higher parent
        return redistributeParent(parent.parent, parent.parent.n);


    }

    /**
     * helper method to merge one non-leaf node with its previous sibling
     *
     * @param preNode     the previous sibling
     * @param next        the non-leaf node to merge
     * @param parent      their parent node
     * @param prePosition the position of preNode in parent's children
     */
    void mergeHelper(BTreeNode preNode, BTreeNode next, BTreeNode parent, int prePosition) {
        // count all nodes' sizes
        int preNum = preNode.n;
        int parentNum = parent.n;
        int num = next.n;

        // if the nodes we are going to merge have to many entities, stop merge
        // and if one of the children-level node has 0 entities, we must merge
        if (preNum != 0 && num != 0 && preNum + 1 + num > 2 * t - 1) {
            return;
        }

        // if their parent has only 1 entity and is not root, we try to redistribute their parent and its siblings
        if (parentNum == 1 && !parent.equals(root)) {
            redistributeParent(parent, parentNum);
        } else {

            // copy the corresponding keys from grandparent to previous
            preNode.keys[preNum] = parent.keys[prePosition];

            preNum++;
            preNode.n++;

            // merge next with previous
            for (int i = 0; i < num; i++) {
                preNode.keys[preNum + i] = next.keys[i];
                next.children[i].parent = preNode;
                preNode.children[preNum + i] = next.children[i];
            }
            // since children is longer than keys, we need deal with last child
            preNode.children[preNum + num] = next.children[num];
            next.children[num].parent = preNode;

            // update n
            preNode.n += next.n;

            // change next
            preNode.next = next.next;

            // if grandparent is the root, and has only 1 value, change root
            if (parent.equals(root) && parentNum == 1) {
                root = preNode;
                preNode.parent = null;
                preNode.next = null;

                // else, move all value in grandparent left by 1
            } else {
                for (int j = prePosition; j < parentNum - 1; j++) {
                    parent.keys[j] = parent.keys[j + 1];
                    parent.children[j] = parent.children[j + 1];
                }
                parent.keys[parentNum - 1] = 0;
                parent.children[parentNum] = null;

                // update parent's n
                parent.n--;
            }
        }
    }

    List<Long> print() {

        List<Long> listOfRecordID = new ArrayList<>();

        /**
         * TODO: Implement this function to print the B+Tree. Return a list of recordIDs from left to
         * right of leaf nodes.
         *
         */

        BTreeNode cur = root;
        // go to a leaf node
        while (cur.leaf == false) {
            cur = cur.children[0];
        }

        while (cur != null) {
            for (int i = 0; i < 2 * t - 1; i++) {
                if (cur.keys[i] != 0)
                    listOfRecordID.add(cur.keys[i]);
            }
            // System.out.println(cur.keys[0] + " " + cur.keys[1] + " " + cur.keys[2] + " " + cur.keys[3]+
            // " " + cur.keys[4]);
            cur = cur.next;
            // System.out.println(cur.keys[0]);
        }

        // System.out.println(Arrays.toString(listOfRecordID.toArray()));
        return listOfRecordID;
    }
}

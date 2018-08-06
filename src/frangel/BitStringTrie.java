// A trie that stores bitstrings, e.g., "10100"

package frangel;

public class BitStringTrie {
    private static class Node {
        Node child0, child1;
        boolean isLeaf;
        Node() {
            child0 = child1 = null;
            isLeaf = false;
        }
        Node getChild(boolean bit) {
            return bit ? child1 : child0;
        }
        Node createChild(boolean bit) {
            Node newNode = new Node();
            return bit ? (child1 = newNode) : (child0 = newNode);
        }
    }

    private Node root;
    private int size;

    public BitStringTrie() {
        root = new Node();
        size = 0;
    }

    public void add(String bitstring) {
        Node cur = root;
        for (int i = 0; i < bitstring.length(); i++) {
            boolean bit = bitstring.charAt(i) != '0';
            Node next = cur.getChild(bit);
            cur = (next == null ? cur.createChild(bit) : next);
        }
        if (!cur.isLeaf) {
            size++;
            cur.isLeaf = true;
        }
    }

    // Returns true if any prefix of the provided bitstring is already in the trie.
    public boolean containsPrefix(StringBuilder bitstring) {
        Node cur = root;
        if (cur.isLeaf)
            return true;
        for (int i = 0; i < bitstring.length(); i++) {
            cur = cur.getChild(bitstring.charAt(i) != '0');
            if (cur == null)
                return false;
            if (cur.isLeaf)
                return true;
        }
        return false;
    }

    // Returns the number of unique bitstrings that were previously added.
    public int size() {
        return size;
    }
}

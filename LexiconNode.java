// name: Lily Shao

import java.util.*;

public class LexiconNode implements Comparable {
	protected char letter;
    protected boolean isWord;
    // for keeping track of the children of this LexiconNode
	protected ArrayList<LexiconNode> children;

    public LexiconNode(char letter, boolean isWord) {
		this.letter = letter;
		this.isWord = isWord;
		children = new ArrayList<LexiconNode>(); // children are in alphabetical order
    }

    // Compare this LexiconNode to another.  You should just
    public int compareTo(Object o) {
		LexiconNode other = (LexiconNode) o;
		return letter-other.letter();
    }

    /* Return letter stored by this LexiconNode */
    public char letter() {
		return letter;
    }

	public ArrayList<LexiconNode> children() {
		return children;
	}

    /* Add LexiconNode child to correct position in child data structure */
    public void addChild(LexiconNode ln) {
		// if the children ArrayList is empty, just add ln to it
		if (children.size()==0) { children.add(ln); }
		// otherwise traverse the ArrayList to insert ln at the right place
		for (int i=0; i < children.size(); i++) {
			if (ln.compareTo(children.get(i))>0) {
				children.add(ln);
				break;
			}
		}
    }

    /* Get LexiconNode child for 'ch' out of child data structure */
    public LexiconNode getChild(char ch) {
		int pos = findChild(ch);
		if (pos != -1) { return children.get(pos); }
		else return null;
    }

    /* Remove LexiconNode child for 'ch' from child data structure */
    public void removeChild(char ch) {
		int pos = findChild(ch);
		if (pos != -1) { children.remove(pos); }
    }

	// helper method for getChild and removeChild
	public int findChild(char target) {
		for (int i=0; i < children.size(); i++) {
			if (target==children.get(i).letter()) return i;
		}
		return -1;
	}

    /* Iterate over children */
    public Iterator<LexiconNode> iterator() {
		return children.iterator();
    }

	public String toString() {
		String result = "Node " + letter + " : \nisWord: " + isWord +
			" children: " + children.toString();
		return result;
	}

	// for testing
	public static void main(String s[]) {
		LexiconNode n1 = new LexiconNode(new Character('a'), false);
		LexiconNode n2 = new LexiconNode(new Character('b'), true);
		LexiconNode n3 = new LexiconNode(new Character('c'), true);
		n1.addChild(n2);
		n1.addChild(n3);
		System.out.println(n1);
		System.out.println("children size: " + n1.children.size());
	}
}

// name: Lily Shao

import java.util.*;
import java.io.FileInputStream;

public class LexiconTrie implements Lexicon {

	// total number of words stored in the trie
	protected int numWords;
	// root/start, which has no parent, no value, but a children ArrayList
	protected LexiconNode startNode;
	protected static char space = new Character(' ');

	public LexiconTrie() {
		numWords = 0;
		startNode = new LexiconNode(space, false);
	}

	// helper method for find, addWord, remove; refactor common code of searching a node's children ArrayList
	// return the index of the last node matched in current's children ArrayList
	// l is the char being matched
	public int searchChildren(LexiconNode current, char l) {
		ArrayList<LexiconNode> v = current.children();
		int index;
		// search in the children ArrayList of current;
		for (index=0; index<v.size(); index++) {
			if (v.get(index).letter()==l) break;
		}
		return index;
	}

	// returns true if the word was added (i.e. previously did
	// not appear in this lexicon) and false otherwise.
	public boolean addWord(String word) {
		word = word.toLowerCase();

		// trace out the word's path starting from the root, as if searching.
		// If any part of the path does not exist, add the missing nodes to the trie
		LexiconNode current = startNode;
		int i;
		for (i=0; i<word.length(); i++) {
			char l = word.charAt(i);

			// searched through current's children ArrayList:
			int j = searchChildren(current, l);
			// if cannot find a match for l
			if (j==current.children().size()) break;
			// if can find a match for l in current's children ArrayList, update current
			else current = current.children().get(j);
		}
		// if the word's path is completely traced, meaning it's already in the trie.
		if (i == word.length()) { return false; }
		// otherwise, cannot find a match for l
		// meaning there is not a path for str, so add the rest of the word
		for (int k=i; k<word.length();k++) {
			LexiconNode newNode = new LexiconNode(word.charAt(k) , false);
			current.children().add( newNode );
			current = newNode;
		}
		// turn on the isWord flag for the final node; increment numWords
		current.isWord = true;
		numWords++;
		return true;
	}

    public int addWordsFromFile(String filename) {
		try {
			Scanner sc = new Scanner(new FileInputStream(filename));
			int wordsAdded = 0;
			while (sc.hasNext()) {
				String nextWord = sc.next();
				// System.out.println("next word is: " + nextWord);
				addWord( nextWord );
				wordsAdded++;
				// numWords already incremented in addWord
			}
			// System.out.println("# of words added is: " + wordsAdded);
			return wordsAdded;
		}
		catch (Exception e) { // FileNotFoundException e
			// e.printStackTrace();
			return 0;
		}
	}

	// helper method for constainsWord and containsPrefix
	// see if str can be found in the trie's path
	public LexiconNode find(String str) {
		LexiconNode current = startNode;
		// for every letter in str, see if it can be traced down in the next node's
		// children vecgor
		for (int i=0; i<str.length(); i++) {
			char l = str.charAt(i);
			int j = searchChildren(current, l);
			//searched through current's children ArrayList and cannot find a match for l
			// meaning there is not a path for str
			if (j==current.children().size()) return null;
			else current = current.children().get(j);
		}

		return current; //the node with last letter in str
	}

	// Only difference between the two contains method:
	// containsWord performs one additional test
	// before returning to see if the isWord flag is set to be true
	public boolean containsWord(String word){
		LexiconNode wordFound = find(word);
		if (wordFound!=null && wordFound.isWord==true) return true;
		else return false;
	}
	public boolean containsPrefix(String prefix){
		LexiconNode prefixFound = find(prefix);
		if (prefixFound!=null) return true;
		else return false;
	}

	public int numWords() {return numWords;}

	// trace out the path of word, char by char
	// if encounter mismatch before finishing, return false
	// else turn off isWord flag + decrement numWords + return true
    public boolean removeWord(String word) {
		LexiconNode current = startNode;

		// for optional:
		LexiconNode lastWordNode = null; // the most recent node before current with isWord==true
		LexiconNode deleteFrom = null; // a child of lastWordNode

		// for every letter in str, see if it can be traced down in the next node's
		// children ArrayList
		for (int i=0; i<word.length(); i++) {
			char l = word.charAt(i);
			int j = searchChildren(current, l);
			// searched through current's children ArrayList and cannot find a match for l
			// meaning there is not a path for str, end here and return false
			if (j==current.children().size()) return false;
			// otherwise there is a match, so update current node
			else {
				LexiconNode nextNode = current.children().get(j); // for optional
				if (current.isWord) { // for optional
					lastWordNode = current;
					deleteFrom = nextNode;
				}
				current = nextNode;
			}
		}
		current.isWord = false;
		numWords--;

		// remove dead ends:
		// If the word being removed was the only valid word along this path,
		// the nodes along that path must be deleted from the trie along with the word
		if (noWordLeft(current)) {
			lastWordNode.children().remove(deleteFrom);
		}

		return true;
	}

	// recursive helper method for removing dead ends
	// return true if any path down from ln do not have a word /
	// all nodes stemmed from ln has isWord==false
	public boolean noWordLeft(LexiconNode ln) {
		if (ln.children().isEmpty()) return true;
		if (ln.isWord) return false;
		else {
			Iterator<LexiconNode> iter = ln.iterator();
			do {
				return noWordLeft(iter.next());
			} while (iter.hasNext());
		}
	}

	// a helper method that recursively builds a ArrayList of words
	// note: LexiconNodes already maintain a list of their children in sorted order
	public void iteratorHelper(ArrayList<String> allWords, LexiconNode ln, String str) {
		// when isWord flag is true, add word to the ArrayList
		if (ln.isWord==true) allWords.add(str);
		// if reaches leaf/no children, end
		if (ln.children().isEmpty()) return;
		else { // keep going down the path to build word
			Iterator<LexiconNode> iter = ln.iterator(); // an iterator of ln's children, in alphaberical order
			while (iter.hasNext()) {
				ln = iter.next();
				iteratorHelper(allWords, ln, str+ln.letter());
			}
		}
	}

    public Iterator<String> iterator() {
		ArrayList<String> words = new ArrayList<String>();
		iteratorHelper(words, startNode, "");
		return words.iterator();
	}

	/* incomplete for now
	- naive: search all words in trie against target
	- better: Recursive traversal through the trie gathering those “neighbors”
	that are close to the target path
	*/
    public Set<String> suggestCorrections(LexiconTrie trie, String target, int maxDistance) {
		Set<String> suggestions = new HashSet<String>();
		for (String word : trie.iterator()) {
			if (distance(target, word) <= maxDistance) { suggestions.add(word); }
		}
		return suggestions;
	}
	/*
	*/
	public int distance(String word1, String word2) {
		assert word1.length()==word2.length() : "consider only 2 words with same lengths";
		int count = 0;
		for (int i=0; i<word1.length(); i++) {
			if (word1.charAt(i)==word2.charAt(i)) { count++; }
		}
		return count;
	}

	// incomplete for now
	// For non-wildcard characters, it proceeds just as for traversing ordinary words.
	// On wildcard characters, “fan out” the search to include all possibilities for that wildcard
    public Set<String> matchRegex(String pattern){
		if (pattern.contains("*") ) { //wildcard characters *
			return null;
		}
		else if (pattern.contains("?")) { //wildcard characters ?
			//The ‘?’ wildcard character matches either zero or one character.
			return null;
		}
		else { //non-wildcard
			if (containsWord(pattern)) {
				Set<String> word = new HashSet<String>();
				word.add(pattern);
				return word;
			}
			else return null;
		}
	}
}

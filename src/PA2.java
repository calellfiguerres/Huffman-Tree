import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PA2{

	//PA #2 TODO: finds the smallest tree in a given forest, allowing for a single skip
	//Finds the smallest tree (by weight) in the supplied forest.  
	//Note that this function accepts a second optional parameter of an index to skip.  
	//Use this index to allow your function to also find the 2nd smallest tree in the 
	//forest.
	//DO NOT change the first findSmallestTree function. Only work in the second one!
	public static int findSmallestTree(List<HuffmanTree<Character>> forest)
	{
		return findSmallestTree(forest, -1); //find the real smallest 
	}
	public static int findSmallestTree(List<HuffmanTree<Character>> forest, int index_to_ignore) 
	{
		// Get a temporary copy of the forest to play around with
		ArrayList<HuffmanTree<Character>> temp = new ArrayList<>(forest);

		// Get the tree in the forest that we want to ignore, if one was given
		HuffmanTree<Character> treeToIgnore = null;
		if (index_to_ignore >= 0) {
			treeToIgnore = forest.get(index_to_ignore);
		}
		// A forest that is sorted
		ArrayList<HuffmanTree<Character>> sortedBySmallest = new ArrayList<>();

		// A rudimentary sorting algorithm:
		// Find the smallest tree (by weight) in temp
		// Add that tree to sortedBySmallest
		// Remove that tree from temp
		// Repeat until temp is empty
		while (temp.size() > 0) {
			int currSmallestTreeIndex = 0;
			HuffmanTree<Character> currSmallestTree = temp.get(0);
			for (int i = 1; i < temp.size(); i++) {
				HuffmanTree<Character> compareForest = temp.get(i);
				if (compareForest.getWeight() < currSmallestTree.getWeight()) {
					currSmallestTreeIndex = i;
					currSmallestTree = compareForest;
				}
			}
			sortedBySmallest.add(currSmallestTree);
			temp.remove(currSmallestTreeIndex);
		}

		// Remove the treeToIgnore from our sorted forest, if one was given
		if (treeToIgnore != null) {
			sortedBySmallest.remove(treeToIgnore);
		}

		// Get the tree just after the index to ignore (i.e., ignoring the 3rd-smallest tree gets the 4th smallest tree)
		HuffmanTree<Character> treeToFind = sortedBySmallest.get(0);

		// Run through the original forest and find the tree that matches our treeToFind
		for (int i = 0; i < forest.size(); i++) {
			HuffmanTree<Character> currTree = forest.get(i);
			if (currTree == treeToFind) {
				return i;
			}
		}

		// return -1 if we can't find a smallest forest...for some reason
		return -1; //find the smallest except the index to ignore.
	}

	//PA #2 TODO: Generates a Huffman character tree from the supplied text
	//Builds a Huffman Tree from the supplied list of strings.
	//This function implement's Huffman's Algorithm as specified in page 
	//435 of the book.	
	public static HuffmanTree<Character> huffmanTreeFromText(List<String> data) {
		//In order for your tree to be the same as mine, you must take care 
		//to do the following:
		//1.	When merging the two smallest subtrees, make sure to place the 
		//      smallest tree on the left side!
		//2.	Have the newly created tree take the spot of the smallest 
		//		tree in the forest(e.g. list.set(smallest_index, merged_tree) ).
		//3.	Use list.remove(second_smallest_index) to remove 
		//      the other tree from the forest.	
		//The lines below are just an example. They are NOT part of the code.
//		HuffmanTree<Character> some_tree = new HuffmanTree<Character>('a', 5);
//		HuffmanNode<Character> root = some_tree.getRoot();
		
		//note that root is a HuffmanNode instance. This type cast would only work 
		//if you are sure that root is not a leaf node.
		//Vice versa, for this assignment, you might need to force type cast a HuffmanNode
		//to a HuffmanLeafNode when you are sure that what you are getting is a HuffmanLeafNode.
		//The line below is just an example on how to do forced casting. It is NOT part of the code.
//		HuffmanInternalNode<Character> i_root = (HuffmanInternalNode<Character>)root;

		// Character Frequency: a hash map to store the frequency of each character in the List<String> data
		// K - Character: the character
		// V - Integer  : the frequency of the given character
		// (k, v) = (character, frequency of character)
		HashMap<Character, Integer> characterFrequency = new HashMap<>();

		// Create a simple Key-Value pairs storing the frequency of each character
		for (String str : data) {
			for (int i = 0; i < str.length(); i++) {
				char currChar = str.charAt(i);
				if (!characterFrequency.containsKey(currChar)) {
					characterFrequency.put(currChar, 1);
				} else {
					int currCharFreq = characterFrequency.get(currChar);
					characterFrequency.put(currChar, currCharFreq + 1);
				}
			}
		}

		// Convert each Key-Value pair in characterFrequency into a forest of HuffmanTrees
		List<HuffmanTree<Character>> forest = new ArrayList<>();

		characterFrequency.forEach((Character character, Integer frequency) -> {
			HuffmanTree<Character> leaf = new HuffmanTree<>(character, frequency);
			forest.add(leaf);
		});

		// Build out the internal nodes of the Huffman Tree
		// Get the two smallest-frequency characters and join them

		// While there is MORE than one tree in our forest (since by the end our remaining tree is the culmination of
		// all other trees
		while (forest.size() > 1) {
			// Get the 1st- and 2nd-smallest frequency character
			int firstSmallestIndex = findSmallestTree(forest);
			HuffmanTree<Character> firstSmallest = forest.get(firstSmallestIndex);
			int secondSmallestIndex = findSmallestTree(forest, firstSmallestIndex);
			HuffmanTree<Character> secondSmallest = forest.get(secondSmallestIndex);

			// Join the two trees, with the smaller frequency as the left
			// child and the slightly higher frequency as the right child.
			HuffmanTree<Character> joinedTree = new HuffmanTree<>(firstSmallest, secondSmallest);

			// Put the joined tree in place of the 1st-smallest tree
			forest.set(firstSmallestIndex, joinedTree);

			// Remove the second-smallest tree
			forest.remove(secondSmallestIndex);
		}

		// Return the final Huffman tree
		return forest.get(0);
	}

	//PA #2 TODO: Generates a Huffman character tree from the supplied encoding map
	//NOTE: I used a recursive helper function to solve this!
	public static HuffmanTree<Character> huffmanTreeFromMap(Map<Character, String> huffmanMap) {
		//Generates a Huffman Tree based on the supplied Huffman Map.Recall that a 
		//Huffman Map contains a series of codes(e.g. 'a' = > 001).Each digit(0, 1) 
		//in a given code corresponds to a left branch for 0 and right branch for 1.

		HuffmanInternalNode<Character> nodeStructure = new HuffmanInternalNode<>(null, null);
		huffmanMap.forEach((Character character, String code) -> {
			HuffmanInternalNode<Character> currNode = nodeStructure;
			// Build out the internal nodes; if the code is "001", it builds out the left-left nodes and stops in
			// preparation for the final right left node
			for (int i = 0; i < code.length() - 1; i++) {
				char currChar = code.charAt(i);

				// Left at node
				if (currChar == '0') {
					// If there isn't already a left child internal node of the current node
					if (currNode.getLeftChild() == null) {
						HuffmanInternalNode<Character> newNode = new HuffmanInternalNode<>(null, null);
						currNode.setLeftChild(newNode);
					}

					// Set the current node to be the current node's left child
					currNode = (HuffmanInternalNode<Character>) currNode.getLeftChild();

				// Right at node
				} else {
					// If there isn't already a right child internal node of the current node
					if (currNode.getRightChild() == null) {
						HuffmanInternalNode<Character> newNode = new HuffmanInternalNode<>(null, null);
						currNode.setRightChild(newNode);
					}

					// Set the current node to be the current node's right child
					currNode = (HuffmanInternalNode<Character>) currNode.getRightChild();
				}
			}

			// Gets the last "turn" the traversal would have to make
			char finalTurn = code.charAt(code.length() - 1);

			// Left at node
			if (finalTurn == '0') {
				if (currNode.getLeftChild() != null) {
					throw new RuntimeException("A node already exists when trying to put {" + character + ": " + code + "} into HuffmanTree");
				}
				// Create a new HuffmanLeafNode; the frequency doesn't matter, so just set it to -1 to satisfy the constructor
				HuffmanLeafNode<Character> newLeaf = new HuffmanLeafNode<>(character, -1);

				// Add the new left node to our tree
				currNode.setLeftChild(newLeaf);

			// Right at node
			} else {
				if (currNode.getRightChild() != null) {
					throw new RuntimeException("A node already exists when trying to put {" + character + ": " + code + "} into HuffmanTree");
				}
				// Create a new HuffmanLeafNode; the frequency doesn't matter, so just set it to -1 to satisfy the constructor
				HuffmanLeafNode<Character> newLeaf = new HuffmanLeafNode<>(character, -1);

				// Add the new right node to our tree
				currNode.setRightChild(newLeaf);
			}
		});

		return new HuffmanTree<>(nodeStructure);
	}

	//PA #2 TODO: Generates a Huffman encoding map from the supplied Huffman tree
	//NOTE: I used a recursive helper function to solve this!
	public static Map<Character, String> huffmanEncodingMapFromTree(HuffmanTree<Character> tree) {
		//Generates a Huffman Map based on the supplied Huffman Tree.  Again, recall 
		//that a Huffman Map contains a series of codes(e.g. 'a' = > 001).Each digit(0, 1) 
		//in a given code corresponds to a left branch for 0 and right branch for 1.  
		//As such, a given code represents a pre-order traversal of that bit of the 
		//tree.  I used recursion to solve this problem.
		
		Map<Character, String> result = new HashMap<>();

		mapFromTreeHelper((HuffmanInternalNode<Character>) tree.getRoot(), result, "");

		return result;
	}

	private static void mapFromTreeHelper(HuffmanInternalNode<Character> tree, Map<Character, String> map, String path) {
		// If the tree's left child is a leaf node
		if (tree.getLeftChild().isLeaf()) {
			// Get the left child and cast it to a leaf node
			HuffmanLeafNode<Character> leftChild = (HuffmanLeafNode<Character>) tree.getLeftChild();

			// Put the path to the node in the map, appending a "left" turn to the current path
			map.put(leftChild.getValue(), path + "0");

		// Otherwise, it's another internal node
		} else {
			// Do it all again, starting from the tree's left child; append on a "left" turn to the current path
			mapFromTreeHelper((HuffmanInternalNode<Character>) tree.getLeftChild(), map, path + "0");
		}

		// If the tree's right child is a leaf node
		if (tree.getRightChild().isLeaf()) {
			// Get the right child and cast it to a leaf node
			HuffmanLeafNode<Character> rightChild = (HuffmanLeafNode<Character>) tree.getRightChild();

			// Put the path to the node in the map, appending a "right" turn to the current path
			map.put(rightChild.getValue(), path + "1");
		} else {
			// Do it all again, starting from the tree's right child; append a "right" turn to the current path
			mapFromTreeHelper((HuffmanInternalNode<Character>) tree.getRightChild(), map, path + "1");
		}
	}

	//PA #2 TODO: Writes an encoding map to file.  Needed for decompression.
	public static void writeEncodingMapToFile(Map<Character, String> huffmanMap, String file_name) {
		//Writes the supplied encoding map to a file.  My map file has one 
		//association per line (e.g. 'a' and 001).  Each association is separated by 
		//a sentinel value.  In my case, I went with a double pipe (||).

		// Create a new StringBuilder
		StringBuilder sb = new StringBuilder();

		// For each character-characterCode pair in the map, create the String "<character>||<code>\n" then append it
		// to the StringBuilder. e.g., the mapping {'a' = "001"} becomes "a||001\n"
		huffmanMap.forEach((Character character, String code) -> {
			sb.append(character).append("||").append(code).append("\n");
		});

		// Finalize the StringBuilder
		String strToWrite = sb.toString();

		// Write the file
		File file = new File(file_name);
		try {
			// Attempt to create a new file
			if (file.createNewFile()) {
				// Make a new writer based on our file
				FileWriter writer = new FileWriter(file);

				// Write the file and close it
				writer.write(strToWrite);
				writer.close();

				System.out.println("Successfully created and wrote file '" + file_name + "'.");

			// Skip if the file already exists, to avoid overwriting anything potentially important
			} else {
				System.out.println("File '" + file_name + "' already exists. Skipping...");
			}

		// If writing the file goes wrong somehow
		} catch (IOException e) {
			System.out.println("Unable to create file '" + file_name + "'. Skipping...");
		}
	}

	//PA #2 TODO: Reads an encoding map from a file.  Needed for decompression.
	public static Map<Character, String> readEncodingMapFromFile(String file_name) {
		//Creates a Huffman Map from the supplied file.Essentially, this is the 
		//inverse of writeEncodingMapToFile. Use String.split() function - note that
		//the split() function takes a Regular Expression as an input, not a "string" itself. 
		//To separate based on "||", the argument for the function should be: split("\\|\\|")

		// Create a map to hold the results
		Map<Character, String> result = new HashMap<>();

		File file = new File(file_name);

		// Attempt to read the file
		try {
			// if the file exists
			if (file.exists()) {
				// Attempt to read the file line by line
				Scanner scanner = new Scanner(file);

				// Until we reach the end of the file...
				while (scanner.hasNextLine()) {
					// Read the next line
					String data = scanner.nextLine();

					// Split the line by the separator
					String[] dataArray = data.split("\\|\\|");

					// Get the character we need; it is assumed that the first character in the first String
					// is what the code corresponds to
					char character = dataArray[0].charAt(0);

					// Get the code for the character
					String code = dataArray[1];

					// Put it in the map
					result.put(character, code);
				}

				System.out.println("Successfully read file '" + file_name + "'.");

			// If the file doesn't exist
			} else {
				System.out.println("File '" + file_name + "' does not exist. Skipping...");
			}

		// If reading the file goes wrong somehow
		} catch (IOException e) {
			System.out.println("Unable to read file '" + file_name + "'. Skipping...");
		}

		return result;
	}

	//PA #2 TODO: Converts a list of bits (bool) back into readable text using the supplied Huffman map
	public static String decodeBits(List<Boolean> bits, Map<Character, String> huffmanMap) {
		//Uses the supplied Huffman Map to convert the list of bools (bits) back into text.
		//To solve this problem, I converted the Huffman Map into a Huffman Tree and used 
		//tree traversals to convert the bits back into text.
		
		//Use a StringBuilder to append results. 
		StringBuilder result = new StringBuilder();

		// Convert the list of bits into a String of 1's and 0's
		StringBuilder sb = new StringBuilder();
		for (Boolean bit : bits) {
			if (bit) {
				sb.append("1");
			} else {
				sb.append("0");
			}
		}
		String bitsTranslated = sb.toString();

		// Convert the huffmanMap to a HuffmanTree
		HuffmanTree<Character> huffmanTree = huffmanTreeFromMap(huffmanMap);
		HuffmanInternalNode<Character> huffmanTreeRoot = (HuffmanInternalNode<Character>) huffmanTree.getRoot();

		// Traverse the tree according to the translated bits
		HuffmanInternalNode<Character> currNode = huffmanTreeRoot;
		for (int i = 0; i < bitsTranslated.length(); i++) {
			char currTurn = bitsTranslated.charAt(i);

			// Left turn at node
			if (currTurn == '0') {
				// The left child is a leaf node, therefore it has a letter
				if (currNode.getLeftChild().isLeaf()) {
					// Get the letter it holds and append it to the StringBuilder
					char letter = ((HuffmanLeafNode<Character>) currNode.getLeftChild()).getValue();
					result.append(letter);

					// Return to root
					currNode = huffmanTreeRoot;

				// The next node is not a leaf node, so take the left turn
				} else {
					currNode = (HuffmanInternalNode<Character>) currNode.getLeftChild();
				}

			// Right turn at node
			} else {
				// The right child is a leaf node, therefore it has a letter
				if (currNode.getRightChild().isLeaf()) {
					// Get the letter it holds and append it to the StringBuilder
					char letter = ((HuffmanLeafNode<Character>) currNode.getRightChild()).getValue();
					result.append(letter);

					// Return to root
					currNode = huffmanTreeRoot;

				// The next node is not a leaf node, so take the right turn
				} else {
					currNode = (HuffmanInternalNode<Character>) currNode.getRightChild();
				}
			}
		}

		return result.toString();
	}

	//PA #2 TODO: Using the supplied Huffman map compression, converts the supplied text into a series of bits (boolean values)
	public static List<Boolean> toBinary(List<String> text, Map<Character, String> huffmanMap) {
		List<Boolean> result = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		// For each String in text
		for (String str : text) {
			// For each character in str
			for (int i = 0; i < str.length(); i++) {
				// Find the character's mapping in huffmanMap and append that mapping onto sb
				String charMapping = huffmanMap.get(str.charAt(i));
				sb.append(charMapping);
			}
		}

		// Finalize textTranslated
		String textTranslated = sb.toString();

		// Convert each 1 or 0 in textTranslated into a boolean, then add that boolean to result
		for (int i = 0; i < textTranslated.length(); i++) {
			if (textTranslated.charAt(i) == '0') {
				result.add(false);
			} else {
				result.add(true);
			}
		}

		return result;
	}

}

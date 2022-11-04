import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

class Node implements Comparable<Node> {

	/*
		Declares instance variables that will be initialized in 
		one of the constructors below
	*/
	int frequency;
    	Node leftNode;
    	Node rightNode;
	Character character;

	/*
		The below constructor helps initialized the left and right
		nodes of a non-leaf node. This also stores the frequency of
		the node. e.g. a node with no character values is connected to
		two leaf nodes with each a character frequency of 3. So the parent
		node's frequency is 6.
	*/
    	public Node(Node leftNode, Node rightNode) {
		this.leftNode = leftNode;
		this.rightNode = rightNode;
		this.frequency = leftNode.frequency + rightNode.frequency;
    	}

	/*
		The below constructor creates the leaf nodes with its character and
		frequency values initialized
	*/
	public Node(Character character, int frequency) {
		this.frequency = frequency;
		this.character = character;
	}
    
	/*
		The compareTo method is used to help the PriorityQueue
		know where to place each element in its appropriate priority
		placement
	*/
    @Override
    public int compareTo(Node node) {
        return Integer.compare(frequency, node.frequency);
    }
}

//The class below is used for encoding and decoding of the files

public class HuffmanSubmit implements Huffman {
	Node root; //creates a node, which will be set as the top/root node of the huffman tree
	Node currNode; //this node helps iterate throughout the tree, especially helpful in decoding
	BinaryIn charRead; //declares an object of BinaryIn, which would then help read the binary of the file to be encoded
	BinaryOut cOut; //this would help in creating the encoded and decoded files
	HashMap<Character, Integer> chMap = new HashMap<Character, Integer>(); //maps each character in the input file to its frequency
	HashMap<Character, String> huffmanCode = new HashMap<Character, String>(); //maps each character in the input file to its huffman code string (e.g. possibly 00 for 'a')
 
	public void encode(String inputFile, String outputFile, String freqFile){
		charRead = new BinaryIn(inputFile); //sets the input file the program will be reading from as the argument supplied in the method call
		
		/*
			Iterates through each character in the input file, and inserts the character
			as a key of the hashmap. The program will then set the value of the character occurrence
			as 1. If the character already occurred (it's contained in the hashmap), then the frequency
			value associated with that key/character increments by one
		*/
		while(!charRead.isEmpty()) { //keeps iterating through all the characters until there's no characters to iterate
			Character currCh = charRead.readChar(); //sets the current character being added as the character received from the input file
			if(!chMap.containsKey(currCh)) {  //adds the character if it's not already contained in the hashmap
				chMap.put(currCh, 1); //adding the character w/ 1 as freq
			}
			else {
				int val = chMap.get(currCh); 
				chMap.put(currCh, val + 1); //increments the character's frequency
			}
		}	
		
		/*
			The try block mainly iterates through the hashmap and prints the 8-bit binary
			code for the character stored as a key in the hashmap, separated by a ":", and then
			printing the character's frequency, which is stored in the hashmap's key values.
		*/
		try {			
			FileWriter writeFile = new FileWriter(freqFile); //creates an instance of FileWriter, which will then be used to write onto the freqFile
			int i = 0;
			for(Character c : chMap.keySet()) { //iterates through the hashmap, sets the Character c to the key value being iterated in the hashmap
				String binary = Integer.toBinaryString(c); //turns the character into a binary string
				String freqLine = String.format("%8s:%d%s", binary, chMap.get(c), (i < chMap.keySet().size()-1) ? "\n" : ""); //formats the lines of the code as described in the instructions. Also, this doesn't print an empty new line as the last line of the frequency text file
				writeFile.write(freqLine.replaceAll(" ", "0")); //writes onto the frequency text file and replaces every instance of a space, as created by "%8s," into a 0. This produces an 8-bit binary string
				i++; //increments i, this is used to keep track of the current line and help not print an empty last line, which can prevent possible issues in the huffman tree/coding
			}
			writeFile.close(); 
		}
		catch(IOException exc){ //prints an error statement if an exception happens in the try block
			System.out.println("Error: frequency file"); 
			exc.printStackTrace();
		}
		
		PriorityQueue<Node> queue = new PriorityQueue<>(); //creates a PriorityQueue with the properties described in the Node class
		chMap.forEach((currCh, freqChar) ->
			queue.add(new Node(currCh, freqChar)) //creates a bunch of leaf nodes encapsulating the character being iterated in the hashmap and its frequency value
		);	
		
		while (queue.size() > 1) { //makes the queue into one element where all the nodes inside it (from lowest to highest priority) are being connected to a new node
			queue.add(new Node(queue.poll(), queue.poll()));
		}
		
		root = queue.poll(); //makes the root the node at the top of the tree encapsulating all the rest of the nodes connected to it
		
		chMap.forEach((crChar, frChar) -> { //iterates through the char/freq hashmap
			huffmanCode.put(crChar, generateHuffmanCodes(root, crChar)); //adds the character and its huffmanCode (e.g. possibly 00 for 'a') as a string
		});
		
		charRead = new BinaryIn(inputFile); //sets the file it will be reading from to the input file, the programming will start from the beginning reading over the file
		cOut = new BinaryOut(outputFile); //this will help print the binary onto the .enc file
		
		while(!charRead.isEmpty()) { //this will keep going until there is no characters to read from the input file
			Character currCh = charRead.readChar(); //sets currCh to the current character being read from the input file (which will then move to the next character in the next iteration)
			String code = huffmanCode.get(currCh); //sets code to the huffman code of the corresponding character
			
			for(int k = 0; k < code.length(); k++) { //iterates through code
				if(code.charAt(k) == '1') { //if the character at k in code is 1, the program will print the boolean true onto the encode file
					cOut.write(true);
				}
				else { //if the character is not 1, i.e., 0, then it will print the boolean false onto the encode file 
					cOut.write(false);
				}
			}
		}
		
		cOut.flush(); //flushes the code out into the encode file
		cOut.close();
	}
	
	Stack <Integer> huffStack = new Stack<Integer>(); //creates a stack named huffStack as a global variable
	
	/*
		generateHuffmanCodes works recursively by starting at the root and trying to find the 
		huffmanCode for the character being traced. The String huffmanCode will be returned when
		the method finishes executing
	*/
    String generateHuffmanCodes(Node node, Character chT) {
    	String huffmancoding = ""; 
    	if((node.leftNode != null) && (node.rightNode != null)) { //checks if the node has any children nodes; if yes, this executes
    		huffStack.push(0); //pushes a 0 to the stack
    		String left = generateHuffmanCodes(node.leftNode, chT); //the result of leftNode being supplied in recursion is given to a string
    		if(left.equals("")) { //if the return value is an empty string, meaning it's the not the character we want, this executes
    			huffStack.pop(); //removes the zero
    			huffStack.push(1); //adds a 1
    			String right = generateHuffmanCodes(node.rightNode, chT); //sets right to the outcome of the right traversal path
    			if(right.equals("")) { //if that outcome is an empty string
    				huffStack.pop(); //the one added will be removed
    				return ""; //returns nothing (character hasn't been found)
    			} else {
    				return right; //one of the best cases
    			}
    		} else {
    			return left; //another one of the best cases
    		}
    	}
    	else if(node.character == chT) { //this happens when the program has found the character we want
    		for(Object x: huffStack.toArray()) { //this iterates throughout the stack of 0s and 1s, and generates the huffman code
    			huffmancoding = huffmancoding + x;
    		}
    		huffStack.clear(); //clears the stack so this method can be reused for multiple characters
    		return huffmancoding;
    	}

		return ""; //returns nothing
    }
   
	public void decode(String inputFile, String outputFile, String freqFile){
		chMap.clear(); //Clearing the hashmap so they can be reused
		charRead = new BinaryIn(inputFile); //sets the file to be reading from to the encoded file
		cOut = new BinaryOut(outputFile); //sets the file to be writing to as the output file
		
		/*
			The try block below will set the file it will be reading from be the frequency file.
			This will iterate through each line one by one, allowing for processing of each line to happen
			before moving to the next. This will split the line by the ":" and store it into an array.
			The first index will be the character (8-bit binary to char) and the second will be the 
			character frequency. Both, the character and its frequency, will be added to the chMap hashmap.
		*/
        try {            
        	File fileName = new File(freqFile);
        	Scanner s = new Scanner(fileName);     
            while (s.hasNextLine()) {
            	String[] arr = s.nextLine().split(":"); //split the line being looked at into an array
            	int parseInt = Integer.parseInt(arr[0], 2); //this parses the 8-bit binary to ASCII
            	char c = (char)parseInt; //converts ASCII to char
                chMap.put(c, Integer.valueOf(arr[1])); //adds the char and its frequency as an integer      
            }         
            s.close();
        }
        catch (IOException e) {
        	e.printStackTrace();     
        }
		
		PriorityQueue<Node> queue2 = new PriorityQueue<>(); //creates another priority queue
		chMap.forEach((curCh, freCh) -> //iterates through the hashmap
			queue2.add(new Node(curCh, freCh)) //creates a bunch of leaf nodes storing the hashmap's character and frequency
		);		
		
		while (queue2.size() > 1) { //makes the queue into one element where all the nodes under it are connected
			queue2.add(new Node(queue2.poll(), queue2.poll()));
		}
		
		root = queue2.poll(); //finds what the root is (which is the last element of the queue, the top of the tree)
		currNode = root; //this will traverse the tree. it's set to the root
		int totalCountZ = 0; //this will store the total number of characters that should be in the output file

		for(Character ca : chMap.keySet()){ //this will count the total number of characters that should be in the output file. i.e., the frequencies of each character is being added up
			totalCountZ = totalCountZ + chMap.get(ca);
		}

		int values = 0;	//this will store the number of characters printed onto the output file
		while(!charRead.isEmpty()){ //this will continue until there are no more boolean values to be read from in the encoded file
			Boolean val = charRead.readBoolean(); //sets the Boolean val to the next boolean value charRead is reading from the binary input stream
			if(values == totalCountZ) {break;} //if the total number of characters that should be printed and the number printed are the same, this will break the loop
			Character cchar = printingCharacter(val); //sets the current character received from the printingCharacter method, which is supplied with the boolean val 

			if(cchar != null){ //if the character received from printingCharacter is not null, this will execute 
				cOut.write(cchar); //prints the character onto the output file
				values++; //increments the number of characters printed 
				printingCharacter(null); //calls printingCharacter with a null value, which would then reset the currNode to the root and return null
			}
		}

		cOut.flush(); //flushes the characters out
		cOut.close();
   }
	
	Character printingCharacter(Boolean val) {

		if(val == null) { //sets currNode to the root
            currNode = root;
        }
        else if(val) { //sets currNode to the rightNode if boolean is true
            currNode = currNode.rightNode;
        } 
		else { //sets currNode to the leftNode if boolean is not true
            currNode = currNode.leftNode;
        }
        
		return currNode.character; //returns the character from whichever node (left, right, or root)
	}

   public static void main(String[] args) {

	   Huffman  huffman = new HuffmanSubmit();
	   huffman.encode("csc172_14_heapsort_lower_dark.pdf", "ur.enc", "freq.txt");
	   huffman.decode("ur.enc", "ur_dec.pdf", "freq.txt");

   }
}

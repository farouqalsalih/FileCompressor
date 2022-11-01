Farouq Alsalih
CSC 172
Project 2

How to run the program in the terminal:

javac HuffmanSubmit.java
java HuffmanSubmit

How the code works:

The objective of this program is to receive an input file, whether .pdf, .jpg, .txt, or anything, and encode that file 
using Huffman coding. Once an encoded version of the input file and a frequency txt file has been created, those two 
elements can then be used to decode the encoded file. The frequency txt file is especially important in storing information 
about each character's frequency, which will then be used in a Priority Queue.

As mentioned above, the program will take the name of the input file (which should be in the same folder as the code)
and then iterate throughout each character in the file and create a character/frequency hashmap. Once the hashmap is
created in storing each character and its frequency, it will then use that hashmap in printing out to a freq.txt file 
(which stores the characters and their frequencies) the character, separated by a ":", and its frequency. In the 
process of reading from the hashmap and printing out to the frequency txt file, the character being printed
will be converted to 8-bit binary.

After the frequency file has been generated, the program will then create a priority queue. The program will
then iterate through the hashmap created, and add new nodes into the queue, which each store a character
and its frequency. The node class created for this project has instance variables of frequency, character, leftNode,
and rightNode. A leaf node being created (storing the character and frequency) will encapsulate information just using
the frequency and character instance variables. Once all of the lead nodes are added to the priority queue, the
program will make new nodes that take in two nodes as arguments corresponding to their priority. So when the program
iterates through the queue, it adds two nodes as the left and right node of the parent node. This queue is very 
important in building the tree. Once there is only one element in the queue, the loop will end and the program will 
set that element as the root, which is connected to the rest of the nodes.

Once the tree is created, it will then be used to generate the huffman codes. The program will iterate through all 
the elements of the char/freq hashmap and add in the character and huffman code corresponding to that character in the
char/huffman code hashmap by calling a method called generateHuffmanCodes. The method works by traversing the tree 
starting from the root and working recursively trying to find the desired character in one of the leaf nodes of the tree. 
As the recursion works, integers of 1s and 0s are added into a stack, a global variable, which keeps tracks of the 
left and right traversals of the tree. If the character isn't found in a particular traversal path, it pops some of the 
values stored in the stack. Once the character is found, the elements of the stack are then converted into a string such 
that the bottom most element of the stack is the starting element of the huffman code for that character.

Once the huffman codes are generated and each character and huffman code are mapped, the program will then start reading
from the input file and start outputting to the output file / encode. The program will loop through each character in the 
input file and a string called code will be set to the value of the huffman code for that character. The program will then 
enter a for loop that iterates through "code," and if the character at i (i = 0 as starting value) is equal to '1', the program 
will print true on the encode file. If the character is a '0', the program will print false on the encode file. Once there is no 
more characters to read from in the input file, the while loop will terminate and the binary will get flushed out. 

The decode method works by first clearing the char/frequency hashmap so that it can be reconstructed given the frequency file
provided as an argument to the method. Next, the program will set the input file as the encoded file and the output file
as the provided file name for outputting the decoded text. Next, the program will iterate through the frequency file and 
create an array of each line such that there are two elements where the first is the 8-bit character converted to a character 
and the second as the integer value of the frequency of that character. Next, those two elements of the array for each 
line will be inputted into the char/frequency hashmap, respectively. The program will then employ the same priority queue 
strategy for constructing the tree given the character and frequency values in the hashmap. The queue will add leaf 
nodes of each character and its frequency. Next, the queue will combine its elements together such that a node, with 
no characters, will be created using the left and right nodes provided to it. Next, the last element of the queue will 
be set as the root. The class instance variable will be set to the root. Next, the program will iterate through the hashmap 
adding each character's frequency value and setting a variable to that number. That variable will be used to make sure 
the total of all the characters in the original input file matches the number printed in the decoded file.

In the next part of the program, this is where the decoding part happens. The program will read the binary values one by 
one from the input file (the encoded one) and keeps going until there are no binary values to read from. In each iteration 
of the loop, the program will set the variable cchar to the output received from the printingCharacter method (I received 
some help writing this method from Sammy Potter). The provided method will either return a null or the character value. 
When the method is called with the binary value given, if that binary is a '1', it will set currNode to its right node. 
If that binary is 0, it will set currNode to the left node. If the binary provided is null, it will set currNode to the root. 
At the end of the method, the program will return the character value at that node. So if there is no character value (i.e., 
null), the decoding part will not print it. However, if the character is not null, then the program will print that 
character. If a character is printed, the program will increment a variable called values, which would represent the 
total number of characters printed on the output file. If the number of characters printed and the number that should be 
printed are the same, the program will break the while loop. Now, if a character does get printed, the program will 
then call the printCharacter method with an argument as null, which would then set currNode as the root and return null. 
Once all the characters are written on the output file, they are flushed out.

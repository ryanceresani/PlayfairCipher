import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlayfairCipher {

	//J is excluded from alphabet and combined with I.
	public static final String ALPHABET[] = {"A","B","C","D","E","F","G","H","I","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

	public static void main(String[] args) throws IOException {
		Scanner input = new Scanner(System.in);
		HashMap<String, Letter> alphabet = genLetters();

		
		//MAKE INTO METHOD
		System.out.println("Please select:");
		System.out.print("(E)ncryption or (D)crpytion:  ");
		String choice = input.next();

		String text = "";
		boolean validChoice = false;

		//Check if Encrypt or Decrypt and verify valid input
		do
		{
			if(choice.toUpperCase().startsWith("E")){
				text = readFile("plaintext.txt");
				validChoice = true;
				choice = "E";
			}
			else if(choice.toUpperCase().startsWith("D")){
				text = readFile("ciphertext.txt");
				validChoice = true;
				choice = "D";
			}
			else{
				System.out.println("Invalid Answer, please choose (E) for Encrpyt or (D) for Decrypt: ");
				choice = input.next();
			}
		} while(!validChoice);
		// END MAKE INTO METHOD
		
		//TESTING FOR PRINTING CIPHER/PLAINTEXT
		System.out.println(text);

		//Get Key and verify it is letters.
		System.out.print("Enter Key: ");
		String userIn = input.next();

		//Create the Playfair Matrix
		String matrix[][] = generateMatrix(userIn, alphabet);
		
		//Map the points (x,y) of each letter to their location in the Matrix for later use.
		mapIndexes(matrix, alphabet);

		printMatrix(matrix);

		//Determines if it should encrypt or decrypt
		String output = "";
		if(choice.equals("E")){
			output = encrypt(matrix, text, alphabet);
			System.out.println("Encrypted Cipher Text:  " + output);
		}
		else {
			output = decrypt(matrix, text, alphabet);
			System.out.println("Decrypted Plain Text:  " + output);
		}
	}

	private static void mapIndexes(String[][] matrix, HashMap<String, Letter> alphabet) {
		for (int i = 0; i < matrix[0].length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				Point point = new Point(i, j);
				alphabet.get(matrix[i][j]).setPoint(point);
			}
		}
	}
	
	private static String encrypt(String[][] matrix, String text, HashMap<String, Letter> letters) {
		StringBuilder cipherText = new StringBuilder();
		
		//Iterate through each pair of letters of the plaintext
		for (int i = 0; i < text.length(); i+=2) {
			String first = text.substring(i, i+1);
			String second = text.substring(i+1, i+2);
			
			//Identify the X and Y coordinates for each of those letters
			int firstX = (int) ((letters.get(first).getPoint().getX()));
			int secondX = (int) ((letters.get(second).getPoint().getX()));
			int firstY = (int) ((letters.get(first).getPoint().getY())) ;
			int secondY = (int) ((letters.get(second).getPoint().getY()));


			//If letters are on the same ROW shift one right with wrapping
			if(firstX == secondX) {
				firstY = (firstY + 1) % 5;
				secondY = (secondY + 1) % 5;	
				cipherText.append(matrix[firstX][firstY]);
				cipherText.append(matrix[secondX][secondY]);
			}
			
			//If letters are on the same COLUMN shift one down with wrapping 
			else if(firstY == secondY) {
				firstX = (firstX + 1) % 5;
				secondX = (secondX + 1) % 5;	
				cipherText.append(matrix[firstX][firstY]);
				cipherText.append(matrix[secondX][secondY]);
			}
			
			//Otherwise, find the rectangle letters (aka swap the Y values of the two letters and return the new letter)
			else {
				cipherText.append(matrix[firstX][secondY]);
				cipherText.append(matrix[secondX][firstY]);
			}

		}

		return cipherText.toString();
	}

	private static String decrypt(String[][] matrix, String text, HashMap<String, Letter> letters) {
		StringBuilder plainText = new StringBuilder();
		
		//Iterate through each pair of letters of the plaintext or cipher text
		for (int i = 0; i < text.length(); i+=2) {
			String first = text.substring(i, i+1);
			String second = text.substring(i+1, i+2);

			//Identify the X and Y coordinates for each of those letters
			int firstX =(int) ((letters.get(first).getPoint().getX()));
			int secondX = (int) ((letters.get(second).getPoint().getX()));
			int firstY = (int) ((letters.get(first).getPoint().getY())) ;
			int secondY = (int) ((letters.get(second).getPoint().getY()));
			
			//If letters are on the same ROW shift one back with wrapping
			if(firstX == secondX) {
				firstY = (firstY + 4) % 5;
				secondY = (secondY + 4) % 5;	
				plainText.append(matrix[firstX][firstY]);
				plainText.append(matrix[secondX][secondY]);
			}

			//If letters are on the same COLUMN shift one up with wrapping
			else if(firstY == secondY) {
				firstX = (firstX + 4) % 5;
				secondX = (secondX + 4) % 5;	
				plainText.append(matrix[firstX][firstY]);
				plainText.append(matrix[secondX][secondY]);
			}

			//Otherwise, find the rectangle letters (aka swap the Y values of the two letters and return the new letter)
			else {
				plainText.append(matrix[firstX][secondY]);
				plainText.append(matrix[secondX][firstY]);
			}

		}

		return plainText.toString();

	}


	//Populate an array full of Letter objects
	public static HashMap<String, Letter> genLetters(){
		HashMap<String, Letter> letters = new HashMap<String, Letter>();
		for (int i = 0; i < ALPHABET.length; i++) {
			letters.put(ALPHABET[i], new Letter(ALPHABET[i]));
		}
		return letters;
	}

	public static String[][] generateMatrix(String keyword, HashMap<String, Letter> letters){

		//Change the keyword to uppercase, remove anything that is not a letter, and replace J with I
		keyword = keyword.toUpperCase();
		keyword = keyword.replaceAll("[^A-Z]", "");
		keyword = keyword.replace("J", "I");

		//Create ArrayList to remove duplicates for key
		ArrayList<String> tempKey = new ArrayList<String>();

		//Iterate over each letter of keyword
		for (int i=0; i<keyword.length(); i++)
		{
			//Check each Letter object for if it has been used already in the key
			//If not, add to key and set as used
			for (int j = 0; j < letters.size(); j++) {
				String currentLetter = keyword.substring(i, i+1);
				if (!letters.get(currentLetter).getUsed()) {
					tempKey.add(currentLetter);
					letters.get(currentLetter).setUsed(true);
				}
			}
		}


		//Add remaining letters of the alphabet to the key.
		for (int i = 0; i < letters.size(); i++) {
			if(!letters.get(ALPHABET[i]).getUsed())
				tempKey.add(letters.get(ALPHABET[i]).getLetter());

		}


		//Add to a Playfair matrix
		String matrix[][] = new String[5][5];

		int count = 0;
		for (int j = 0; j < 5; j++) {
			for (int j2 = 0; j2 < 5; j2++) {
				matrix[j][j2] = tempKey.get(count);
				count++;
			}
		}

		return matrix;
	}

	//Prints 5x5 cube of key string
	public static void printMatrix(String matrix[][]){
		System.out.println("- MATRIX -");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}

	//Reads in the plaintext or ciphertext
	public static String readFile(String filename) throws IOException {
		String text = new String(Files.readAllBytes(Paths.get(filename)));
		text = text.toUpperCase();
		text = text.replace("J", "I");
		text = text.replaceAll("[^A-Z]", "");

		int stringLength = text.length();

		//Adds the Xs if the letters in the pairs are the same
		if(filename.equals("plaintext.txt"))
		{
			StringBuilder adjust = new StringBuilder(text);
			for (int i = 0; i < stringLength - 1; i+=2) {
				if(adjust.charAt(i) == adjust.charAt(i+1)) {
					adjust.insert(i+1, "X");
					stringLength++;
				}
			}
			//Also adds an X at the end if it does not end in an even pair
			if (adjust.length() % 2 != 0)
				adjust.append("X");
			text = adjust.toString();
		}
		return text;
	}
}

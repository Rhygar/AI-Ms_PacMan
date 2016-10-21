package pacman.entries.pacman;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * This class works as an agent Pacman. The class creates a decision tree, based on the data which is 
 * stored in a textfile. The data is collected from playing and recording from Pacman game, played by a human.
 * Then when the Pacman agent plays, the game will call the function getMove() and depending on it's game state, 
 * the method will return a direction for the Pacman, using the decision tree.  
 * 
 * Readme file available in this package
 * 
 * @author David Tran & John Tengvall
 * @date 19-10-2016
 *
 */
public class MyPacMan extends Controller<MOVE> {

	private ArrayList<DataTuple> trainingData = new ArrayList<DataTuple>();
	private ArrayList<DataTuple> testData = new ArrayList<DataTuple>();
	public static HashMap<String, ArrayList<String>> attributes = new HashMap<String, ArrayList<String>>();
	private Node root;

	public MyPacMan() {
		//prints all outputs into a file output.txt. Contains the decision tree
		try {
			System.setOut(new PrintStream(new FileOutputStream("src/pacman/entries/pacman/David&Johns_DecisionTree.txt")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//create traning and test data.
		generateTrainingAndTestData();
		
/* **** create the ArrayLists (values for each attribute) *************/
		ArrayList<String> yesOrNo = new ArrayList<String>();
		yesOrNo.add("YES");
		yesOrNo.add("NO");

		ArrayList<String> discreteDistance = new ArrayList<String>();
//		discreteDistance.add("LOW");
//		discreteDistance.add("MEDIUM");
//		discreteDistance.add("HIGH");
		discreteDistance.add("VERY_LOW");
		discreteDistance.add("LOW");
		discreteDistance.add("MEDIUM");
		discreteDistance.add("HIGH");
		discreteDistance.add("VERY_HIGH");
		discreteDistance.add("NONE");
		
		ArrayList<String> directions = new ArrayList<String>();
		directions.add("RIGHT");
		directions.add("LEFT");
		directions.add("UP");
		directions.add("DOWN");
		directions.add("NEUTRAL");
		
/* ******add to hashmap which hold all the attributes and its value ***************/
		attributes.put("isBlinkyEdible", yesOrNo);
		attributes.put("isInkyEdible", yesOrNo);
		attributes.put("isPinkyEdible", yesOrNo);
		attributes.put("isSueEdible", yesOrNo);
		attributes.put("blinkyDist", discreteDistance);
		attributes.put("inkyDist", discreteDistance);
		attributes.put("pinkyDist", discreteDistance);
		attributes.put("sueDist", discreteDistance);
		attributes.put("blinkyDir", directions);
		attributes.put("inkyDir", directions);
		attributes.put("pinkyDir", directions);
		attributes.put("sueDir", directions);
//		attributes.put("blinkySameDir", yesOrNo);
//		attributes.put("inkySameDir", yesOrNo);
//		attributes.put("pinkySameDir", yesOrNo);
//		attributes.put("sueSameDir", yesOrNo);
//		attributes.put("isJunction", yesOrNo);
	}

	/**
	 * This method loads the data stored in AI-Ms_PacMan/Ms_Pacman/myData/traningData.txt into DataTuple array,
	 * and split it into one set of traningdata and one set of testdata. It randomly picks data instead of taking
	 * for example the 1st 1/3 of the data.  
	 */
	public void generateTrainingAndTestData() {
		DataTuple[] data = DataSaverLoader.LoadPacManData();		
		Random rand = new Random();
		ArrayList<DataTuple> allData = new ArrayList<DataTuple>(Arrays.asList(data));
		double testDataSize = 0, totalSize = allData.size();
		double proportion = testDataSize/totalSize;
		
		while((proportion) < 0.33) {
			int index = rand.nextInt(allData.size());
			testData.add(allData.get(index));
			allData.remove(index);
			testDataSize++;
			proportion = testDataSize/totalSize;
		}
		for(int i = 0; i < allData.size(); i++) {
			trainingData.add(allData.get(i));
		}
	}

	/**
	 * This method build the decision tree using the generateTree() method
	 */
	public void buildTree() {
		ArrayList<String> attrList = new ArrayList<String>(attributes.keySet());
		root = generateTree(trainingData, attrList);
		//uncommenting next line will print the tree to the file David&Johns_DecisionTree.txt
		root.print(); 
		validateTraning();
	}

	/**
	 * This method follows the algorithm for building a decision tree, described in the book Data Mining Concept Technics
	 * figure 8.3 in chapter 8
	 * @param dataTuples the data from playing as a human
	 * @param attributeList the attributes which we find necessary for the tree
	 * @return
	 */
	public Node generateTree(ArrayList<DataTuple> dataTuples,ArrayList<String> attributeList) {

		// 1. Create Node N
		Node N = new Node();
		// 2. If every tuple in D has the same class C, return N as a
		if (allSameClass(dataTuples)) {
			// Get the string of the direction
			String label = dataTuples.get(0).DirectionChosen.toString();
			// label the node with that direction
			N.setLabel(label);
			return N;
		}

		// 3. Otherwise, if the attribute list is empty, return N as a leaf node
		// labeled with the majority class in D
		if (attributeList.isEmpty()) {
			// N.myMove = majorityClass(dataTuples);
			N.setLabel(majorityClass(dataTuples).toString());
			return N;
		}

		// 4. Otherwise:

		// 4.1 Call the attribute selection method on D and the attribute list,
		// in order to choose the current attribute A:
		// A = S(D, attribute list)
		String A = returnAttribute(attributes, dataTuples, attributeList);
//		String A = getAttributeTest(dataTuples, attributeList);
		// 4.2 Label N as A and remove A from the attribute list
		N.setLabel(A);
		attributeList.remove(A);
		// 4.3 For each value in aj in attribute A:
		ArrayList<String> valuesInA = attributes.get(A);
		for (String aj : valuesInA) {
			//make a copy of the attributelist, otherwise it will be changed in recursive calls
			ArrayList<String> copyArrayList = (ArrayList<String>) attributeList.clone();
			// 4.3a Seperate all tuples in D so that attribute A takes the value
			// aj, creating the subset Dj
			ArrayList<DataTuple> dj = createSubset(dataTuples, A, aj);
			// 4.3b If Dj is empty, add a child node to N labeled with the
			// majority class in D
			if (dj.isEmpty()) {
				N.addChild(aj, new Node(majorityClass(dataTuples).toString()));
			}
			// 4.3c Otherwise, add the resulting node from calling
			// generateTree(Dj,attribute) as a child node to N
			else {
				N.addChild(aj, generateTree(dj, copyArrayList));
//				N.addChild(aj, generateTree(dj, attributeList));
			}
			// 4.4 return N
		}
		return N;
	}

	/**
	 * This method creates a subset of a dataset. 
	 * @param dataTuples all data 
	 * @param attribute the current attribute 
	 * @param aj the value of the attribute we want to create subset for
	 * @return the subset created
	 */
	public ArrayList<DataTuple> createSubset(ArrayList<DataTuple> dataTuples, String attribute, String aj) {
		ArrayList<DataTuple> newDataTuple = new ArrayList<DataTuple>();
		for (DataTuple d : dataTuples) {
			if (d.getAttributeValue(attribute).equals(aj)) {
				newDataTuple.add(d);
			}
		}
		return newDataTuple;
	}

	/**
	 * This method checks which MOVE is the majority of the dataset
	 * @param the dataset to check
	 * @return the MOVE which is the most common in the dataset
	 */
	public MOVE majorityClass(ArrayList<DataTuple> D) {

		MOVE move = null;
		HashMap<MOVE, Integer> moves = new HashMap<MOVE, Integer>();
		moves.put(MOVE.UP, 0);
		moves.put(MOVE.DOWN, 0);
		moves.put(MOVE.RIGHT, 0);
		moves.put(MOVE.LEFT, 0);
		moves.put(MOVE.NEUTRAL, 0);

		for (int i = 0; i < D.size(); i++) {
			MOVE key = D.get(i).DirectionChosen;
			moves.put(key, (moves.get(key) + 1));
		}
		int maxValueInMoves = (Collections.max(moves.values()));
		for (Map.Entry<MOVE, Integer> entry : moves.entrySet()) {
			if (entry.getValue() == maxValueInMoves) {
				move = entry.getKey();
			}
		}
		return move;
	}

	/**
	 * This method checks if all datatuples in the dataset have the same class
	 * @param dataTuples the data to check
	 * @return boolean value. True if all datatuples have the same class, otherwise false
	 */
	public boolean allSameClass(ArrayList<DataTuple> dataTuples) {
		MOVE move = dataTuples.get(0).DirectionChosen;
		for (int i = 1; i < dataTuples.size(); i++) {
			if (dataTuples.get(i).DirectionChosen != move) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method uses the ID3 algorithm to find which attribute gives the most gain, and returns the attribute. 
	 * It uses a mathemtical approach to calculate and validate the values for every attribute from a list.  
	 * @param allAttributes all the attributes we are using
	 * @param data all data stored 
	 * @param attributeList the attributes we want to evaluate 
	 * @return the best attribute to use
	 */
	public String returnAttribute(HashMap<String, ArrayList<String>> allAttributes,ArrayList<DataTuple> data, ArrayList<String> attributeList) {
		String returnAttribute = "";
		double bestInfoAD = 10000000;
		// check every attribute
		for (int i = 0; i < attributeList.size(); i++) {
			double infoAD = 0;
			ArrayList<String> valueInCurrentAttribute = allAttributes.get(attributeList.get(i));
			int[] nbrOfEachValue = new int[valueInCurrentAttribute.size()];
			// for every value in this attribute
			for (int j = 0; j < valueInCurrentAttribute.size(); j++) { // YES NO
				ArrayList<DataTuple> subSet = new ArrayList<DataTuple>();
				// create subset for this value in subset
				for (DataTuple D : data) {
					if (D.getAttributeValue(attributeList.get(i)).equals(
							valueInCurrentAttribute.get(j))) {
						nbrOfEachValue[j]++;
						subSet.add(D);
					}
				}
				double up = 0, down = 0, right = 0, left = 0, neutral = 0;
				for (DataTuple D : subSet) {
					if (D.DirectionChosen == MOVE.UP) {
						up++;
					} else if (D.DirectionChosen == MOVE.DOWN) {
						down++;
					} else if (D.DirectionChosen == MOVE.RIGHT) {
						right++;
					} else if (D.DirectionChosen == MOVE.LEFT) {
						left++;
					} else {
						neutral++;						
					}
				}
				double T = nbrOfEachValue[j];
				if (T != 0.0) {
					infoAD += (T / data.size()) * (
							- ((up / T)      * (log2(up/T)))
							- ((down / T)    * (log2(down/T)))
							- ((right / T)   * (log2(right/T))) 
							- ((left / T)    * (log2(left/T)))
							- ((neutral / T) * (log2(neutral/T)))
							);
				}
			}
			if (infoAD < bestInfoAD) {
				bestInfoAD = infoAD;
				returnAttribute = attributeList.get(i);
			}
		}
		return returnAttribute;
	}
	
	static double log2(double x) {
		if(x == 0) return 0;
		float res = (float)(Math.log(x) / Math.log(2));
		return res;
	}
	
	/**
	 * This method is used to validate how good the agent acts. 
	 * @return a double value between 0 and 1
	 */
	public double validateTraning() {
		MOVE shouldBeMove, generatedMove;
		double nbrOfCorrectMoves = 0, accuracy;
		
		for(int i = 0; i < testData.size(); i++) {
			shouldBeMove = testData.get(i).DirectionChosen;
			generatedMove = getMoveRecursively(root, testData.get(i));
			if(shouldBeMove.toString().equals(generatedMove.toString())) {
				nbrOfCorrectMoves++;
			}
		}
		accuracy = nbrOfCorrectMoves / testData.size();
		System.out.println("Accuracy: " + accuracy);
		return accuracy;
	}

	/**
	 * This method is used to traverse a tree to find a path down to a leaf node, to know which move to make
	 * @param node the current node 
	 * @param data the game state information stored in a datatuple
	 * @return the move to make
	 */
	public MOVE getMoveRecursively(Node node, DataTuple data) {
		MOVE move = null;
		//if leaf node is reached, return the move
		if (node.isLeafNode()) {
			 move = MOVE.valueOf(node.getLabel());
		} else {
			//get the label of the attribute
			String valueNode = data.getAttributeValue(node.getName());
			//get the childnodes 
			HashMap hash = node.getChildren();
			//go down to a certain childnode, depending on the value of the attribute
			Node goToNode = (Node) hash.get(valueNode);
			//recusively traverse
			move = getMoveRecursively(goToNode, data);
		}
		return move;
	}
	
	public MOVE getGoing(Game game) {
		DataTuple temp = new DataTuple(game, null);
		return getMoveRecursively(root, temp);
	}

	/**
	 * This method is called from the game, whenever it wants to have the agents next move
	 */
	public MOVE getMove(Game game, long timeDue) {
		return getGoing(game);
	}

	public static void main(String[] args) {
		MyPacMan pac = new MyPacMan();
		pac.buildTree();
		pac.root.print();
		pac.validateTraning();
	}

}
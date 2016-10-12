package pacman.entries.pacman;

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

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE> {

	private ArrayList<DataTuple> data;
	public static HashMap<String, ArrayList<String>> attributes = new HashMap<String, ArrayList<String>>();
	private Node root;

	public MyPacMan() {
		// Load the file into an arraylist
		DataTuple[] dataTuples = DataSaverLoader.LoadPacManData();
		this.data = new ArrayList<DataTuple>(Arrays.asList(dataTuples));

		// create the ArrayLists (values for each attribute)
		ArrayList<String> yesOrNo = new ArrayList<String>();
		yesOrNo.add("YES");
		yesOrNo.add("NO");

		ArrayList<String> discreteDistance = new ArrayList<String>();
		discreteDistance.add("LOW");
		discreteDistance.add("MEDIUM");
		discreteDistance.add("HIGH");

		// add to hashmap
		attributes.put("isBlinkyEdible", yesOrNo);
		attributes.put("isInkyEdible", yesOrNo);
		attributes.put("isPinkyEdible", yesOrNo);
		attributes.put("isSueEdible", yesOrNo);
		attributes.put("blinkyDist", discreteDistance);
		attributes.put("inkyDist", discreteDistance);
		attributes.put("pinkyDist", discreteDistance);
		attributes.put("sueDist", discreteDistance);
	}

	@SuppressWarnings("unchecked")
	public void buildTree() {
		ArrayList<String> attrList = new ArrayList<String>(attributes.keySet());
		for(int i = 0; i < attrList.size(); i++) {
			System.out.println(attrList.get(i));
		}
		root = generateTree(data, attrList);
	}

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
		if (attributeList.size() == 0) {
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
//		attributeList.trimToSize();
		// 4.3 For each value in aj in attribute A:
		ArrayList<String> valuesInA = attributes.get(A);
//		System.out.println(valuesInA.size());
//		for(int k = 0; k < valuesInA.size(); k++) {
//			System.out.println(valuesInA.get(k));
//		}
		for (String aj : valuesInA) {
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
			}
			// 4.4 return N
		}
		return N;
	}

	public String getAttributeTest(ArrayList<DataTuple> data,
			ArrayList<String> attributes) {

		Random rand = new Random();
		return attributes.get(rand.nextInt(attributes.size()));
	}

	public ArrayList<DataTuple> createSubset(ArrayList<DataTuple> dataTuples,
			String attribute, String aj) {
		ArrayList<DataTuple> newDataTuple = new ArrayList<DataTuple>();
		for (DataTuple d : dataTuples) {
			if (d.getAttributeValue(attribute).equals(aj)) {
				newDataTuple.add(d);
			}
		}
		return newDataTuple;
	}

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

	public boolean allSameClass(ArrayList<DataTuple> dataTuples) {
		MOVE move = dataTuples.get(0).DirectionChosen;
		for (int i = 1; i < dataTuples.size(); i++) {
			if (dataTuples.get(i).DirectionChosen != move) {
				return false;
			}
		}
		return true;
	}

	public String returnAttribute(HashMap<String, ArrayList<String>> allAttributes,ArrayList<DataTuple> data, ArrayList<String> attributeList) {
		String returnAttribute = "DOG";
		double bestInfoAD = 10000000;
		// check every attribute
		for (int i = 0; i < attributeList.size(); i++) {
//			System.out.println("Now cheking attribute: " + attributeList.get(i));
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
//				double T = up+down+right+left;
//				System.out
//						.println("CurrentAttri: " + attributeList.get(i)
//								+ "\n Current Value in attri: "
//								+ valueInCurrentAttribute.get(j)
//								+ "Values UP: " + up + "Values DOWN: " + down
//								+ "Values RIGHT: " + right + "Values LEFT: "
//								+ left + "Values NEUTRAL: " + neutral);
				if (T != 0.0) {
					
					//PROBLEMET ÄR ATT LOG(0) = -OÄNDLIGHETEN, så att när någon av up,down,left,right, är 0 så ballar det ur
					
					infoAD += (T / data.size()) * (
							- ((up / T)      * (log2(up/T)))
							- ((down / T)    * (log2(down/T)))
							- ((right / T)   * (log2(right/T))) 
							- ((left / T)    * (log2(left/T)))
							- ((neutral / T) * (log2(neutral/T)))
							);
				} else {
					// System.out.println("T = 0 for " + attributeList.get(i));
				}

			}
			 System.out.println("InfoAD for " + attributeList.get(i) + " :" +
			 infoAD);
			if (infoAD < bestInfoAD) {
				bestInfoAD = infoAD;
				returnAttribute = attributeList.get(i);
			}
		}
		System.out.println("Attribute returned: " + returnAttribute);
		System.out.println();
		return returnAttribute;
	}
	
	static double log2(double x) {
		if(x == 0) return 0;
		float res = (float)(Math.log(x) / Math.log(2));
		return res;
	}

	public static double logb(double a, double b) {
		return Math.log(a) / Math.log(b);
	}

	public MOVE getMoveRecursively(Node node, DataTuple data) {
		MOVE move = null;

		if (node.isLeafNode()) {
			 move = MOVE.valueOf(node.getLabel());
//			// game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
//			// game.getGhostCurrentNodeIndex(GHOST.BLINKY), DM.PATH);
//			int pacManPosition = Integer.parseInt(data
//					.getAttributeValue("pacManPosition"));
		} else {
			// hämta värdet på attributet, ex age skulle gett youth, middleAge,
			// old
			String valueNode = data.getAttributeValue(node.getName());
			// hämta nodens barn
			HashMap hash = node.getChildren();
			// gå ner till barnet med värdet på attributet
			Node goToNode = (Node) hash.get(valueNode);
			// rekursiv metod
			move = getMoveRecursively(goToNode, data);
		}
		return move;
	}

	public MOVE getGoing(Game game) {
		DataTuple temp = new DataTuple(game, null);
		return getMoveRecursively(root, temp);
	}

	public MOVE getMove(Game game, long timeDue) {
		// Place your game logic here to play the game as Ms Pac-Man
		return getGoing(game);
	}

	public static void main(String[] args) {
		MyPacMan pac = new MyPacMan();
		pac.buildTree();
//		System.out.println(pac.root.getLabel());
		// System.out.println(pac.root.getChild("YES").getLabel());
		// System.out.println(pac.root.getChild("YES").getChild("HIGH").getLabel());
		// System.out.println(pac.root.getgetLabel());
	}

}
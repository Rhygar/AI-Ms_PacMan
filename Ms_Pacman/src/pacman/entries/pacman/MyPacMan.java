package pacman.entries.pacman;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	
	private MOVE myMove=MOVE.NEUTRAL;
	private static DataTuple[] dataTuples;
	
	public MyPacMan() {
		dataTuples = DataSaverLoader.LoadPacManData();
	}
	
	public Node generateTree(DataTuple[] dataTuples, String[] attributeList) {
		//1. Create Node N
		Node N = new Node();
		//2. If every tuple in D has the same class C, return N as a 
		boolean allSameClass = false;
		MOVE move = dataTuples[0].DirectionChosen;
		for(int i = 1; i < dataTuples.length; i++) {
			if(dataTuples[i].DirectionChosen != move) {
				N.isLeafNode = true;
				N.myMove = move;
				return N;
			}
		}
		
		//3. Otherwise, if the attribute list is empty, return N as a leaf node labeled with the majority class in D
		if(attributeList.length == 0) {
			N.isLeafNode = true;
			N.myMove = majorityClass(dataTuples);
			return N;
		}
		
		//4. Otherwise:
		
		//4.1 Call the attribute selection method on D and the attribute list, in order to choose the current attribute A:
				//A = S(D, attribute list)

		//4.2 Label N as A and remove A from the attribute list
		
		//4.3 For each value in aj in attribute A:
		
		//4.3a Seperate all tuples in D so that attribute A takes the value aj, creating the subset Dj
		
		//4.3b If Dj is empty, add a child node to N labeled with the majority class in D
		
		//4.3c Otherwise, add the resulting node from calling generateTree(Dj,attribute) as a child node to N
		
		//4.4 return N
		
		return N;
	}
	
	public MOVE majorityClass(DataTuple D[]) {
		
		MOVE move = null;
		
		HashMap<MOVE,Integer> moves = new HashMap<MOVE, Integer>();
		
		for(int i = 0; i < D.length; i++) {
			MOVE key = D[i].DirectionChosen;
			moves.put(key, (moves.get(key) + 1));
		}
		int maxValueInMoves = (Collections.max(moves.values()));
		for(Map.Entry<MOVE, Integer> entry : moves.entrySet()) {
			if(entry.getValue() == maxValueInMoves) {
				move = entry.getKey();
			}
		}
//		moves.
		return move;
	}
	
	public MOVE getMove(Game game, long timeDue) 
	{
		//Place your game logic here to play the game as Ms Pac-Man
		return myMove;
	}
	public static Integer[] convert(int[] ints) {
        Integer[] copy = new Integer[ints.length];
        for(int i = 0; i < copy.length; i++) {
            copy[i] = ints[i];
        }
        return copy;
    }
	public static void main(String[] args) {
		MyPacMan pac = new MyPacMan();
		System.out.println(dataTuples[0].numberOfNodesInLevel);
		System.out.println(dataTuples[0].DirectionChosen);
		System.out.println(MOVE.UP);
		System.out.println(MOVE.DOWN);
		System.out.println(MOVE.RIGHT);
		System.out.println(MOVE.LEFT);
		System.out.println(MOVE.NEUTRAL);
		int[] a = {2,5,7,4,2,5,7,8};
        Integer[] b = convert(a);
        System.out.println(Collections.max(Arrays.asList(b)));
	}
	
}
package pacman.entries.pacman;

import java.util.HashMap;
import java.util.Map;

import dataRecording.DataTuple;
import pacman.game.Constants.MOVE;

public class GetAttribute {
	
	public  void iterativDichotomiser(DataTuple[] D,String[] attr ){
		int totalDataTuples = D.length;
		double infoD = 0;
		//attributes
		int PacManPos = D[0].pacmanPosition;
		boolean isBlinkyEdible = D[0].isBlinkyEdible;
		boolean isPinkyEdible = D[0].isPinkyEdible;
		boolean isInkyEdible = D[0].isInkyEdible;
		boolean isSueEdible = D[0].isSueEdible;
		MOVE blinkyDir = D[0].blinkyDir;
		MOVE inkyDir = D[0].inkyDir;
		MOVE pinkyDir = D[0].pinkyDir;
		MOVE sueDir = D[0].sueDir; 
		
		//to get the move up,down,left,right
		HashMap<MOVE,Integer> moves = new HashMap<MOVE, Integer>();
		
		for(int i = 0; i < D.length; i++) {
			MOVE key = D[i].DirectionChosen;
			moves.put(key, (moves.get(key) + 1));
		}
		for(Map.Entry<MOVE, Integer> entry : moves.entrySet()) {
			infoD += -(entry.getValue()/totalDataTuples) * (Math.log10(entry.getValue()/totalDataTuples)/Math.log10(2));
		}
		
		
	}
}
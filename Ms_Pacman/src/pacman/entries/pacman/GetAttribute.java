package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiDevice.Info;

import dataRecording.DataTuple;
import pacman.game.Constants.MOVE;

public class GetAttribute {	
	
	public double iterativDichotomiser(ArrayList<DataTuple> D,ArrayList<String> attr ){
		int totalDataTuples = D.size();
		double info = 0;
		String retAttr;
		//to get the move up,down,left,right
		HashMap<MOVE,Integer> moves = new HashMap<MOVE, Integer>();
		
		for(int i = 0; i < D.size(); i++) {
			MOVE key = D.get(i).DirectionChosen;
			moves.put(key, (moves.get(key) + 1));
		}
		for(Map.Entry<MOVE, Integer> entry : moves.entrySet()) {
			info += -(entry.getValue()/totalDataTuples) * (Math.log10(entry.getValue()/totalDataTuples)/Math.log10(2));
		}
		return info;
	}
	
	public double returnAttribute(HashMap <String, ArrayList<String>> allAttributes, ArrayList<DataTuple> data, ArrayList<String> attributeList){
		double bestValue = 0.0;
		double gain = 0.0;
		for(int i = 0; i < attributeList.size(); i++){
			double infoTotal = 0;
			ArrayList<String> currentAttr = allAttributes.get(attributeList.get(i));
			int[] nbrOfEachValue = new int[currentAttr.size()];
			for(int j = 0; j < currentAttr.size(); j++){  //YES NO
				ArrayList<DataTuple> tempList = new ArrayList<DataTuple>();
				for(DataTuple D : data){
					if(D.getAttributeValue(attributeList.get(i)).equals(currentAttr.get(j))){
						nbrOfEachValue[j]++;
						tempList.add(D);
					}
				}
				int up = 0,down = 0,right = 0, left = 0, neutral = 0;
				for(DataTuple D : tempList){
					if (D.DirectionChosen == MOVE.UP){
						up++;
					}
					else if(D.DirectionChosen == MOVE.DOWN){
						down++;
					}
					else if(D.DirectionChosen == MOVE.RIGHT){
						right++;
					}
					else if(D.DirectionChosen == MOVE.LEFT){
						left++;
					}
					else 
						neutral++;
				}
				int totalDir = up + down + right + left + neutral;
				int T = nbrOfEachValue[j];
				double infoX = ((T/data.size()) * ((-(up/T)*(Math.log10(up/T)/Math.log10(2))-(down/T)*(Math.log10(down/T)/Math.log10(2))-(right/T)*(Math.log10(right/T)/Math.log10(2))-(left/T)*(Math.log10(left/T)/Math.log10(2))-(neutral/T)*(Math.log10(neutral/T)/Math.log10(2)))));
				if(infoX > bestValue){
					bestValue += infoX;
				}
			}	
			
		}
		return bestValue;
	}
}
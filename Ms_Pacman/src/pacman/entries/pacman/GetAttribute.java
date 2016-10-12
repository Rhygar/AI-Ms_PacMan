package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiDevice.Info;

import dataRecording.DataTuple;
import pacman.game.Constants.MOVE;

public class GetAttribute {

	public double iterativDichotomiser(ArrayList<DataTuple> D,
			ArrayList<String> attr) {
		int totalDataTuples = D.size();
		double info = 0;
		String retAttr;
		// to get the move up,down,left,right
		HashMap<MOVE, Integer> moves = new HashMap<MOVE, Integer>();

		for (int i = 0; i < D.size(); i++) {
			MOVE key = D.get(i).DirectionChosen;
			moves.put(key, (moves.get(key) + 1));
		}
		for (Map.Entry<MOVE, Integer> entry : moves.entrySet()) {
			info += -(entry.getValue() / totalDataTuples)
					* (Math.log10(entry.getValue() / totalDataTuples) / Math
							.log10(2));
		}
		return info;
	}

	public String returnAttribute(
			HashMap<String, ArrayList<String>> allAttributes,
			ArrayList<DataTuple> data, ArrayList<String> attributeList) {
		String returnAttribute = "";
		double bestInfoAD = 1000000;
		//check every attribute
		for (int i = 0; i < attributeList.size(); i++) {
			double infoAD = 0;
			ArrayList<String> valueInCurrentAttribute = allAttributes.get(attributeList.get(i));
			int[] nbrOfEachValue = new int[valueInCurrentAttribute.size()];
			//for every value in this attribute
			for (int j = 0; j < valueInCurrentAttribute.size(); j++) { // YES NO
				ArrayList<DataTuple> subSet = new ArrayList<DataTuple>();
				//create subset for this value in subset
				for (DataTuple D : data) {
					if (D.getAttributeValue(attributeList.get(i)).equals(valueInCurrentAttribute.get(j))) {
						nbrOfEachValue[j]++;
						subSet.add(D);
					}
				}
				int up = 0, down = 0, right = 0, left = 0, neutral = 0;
				for (DataTuple D : subSet) {
					if (D.DirectionChosen == MOVE.UP) {
						up++;
					} else if (D.DirectionChosen == MOVE.DOWN) {
						down++;
					} else if (D.DirectionChosen == MOVE.RIGHT) {
						right++;
					} else if (D.DirectionChosen == MOVE.LEFT) {
						left++;
					} else
						neutral++;
				}
				int T = nbrOfEachValue[j];
				infoAD += (T / data.size()) * (
						- ((up / T)	     * (Math.log10(up / T)      / Math.log10(2))) 
						- ((down / T)    * (Math.log10(down / T)    / Math.log10(2)))
						- ((right / T)   * (Math.log10(right / T)   / Math.log10(2)))
						- ((left / T)    * (Math.log10(left / T)    / Math.log10(2))) 
						- ((neutral / T) * (Math.log10(neutral / T) / Math.log10(2)))
						);
			}
			if(infoAD < bestInfoAD) {
				bestInfoAD = infoAD;
				returnAttribute = attributeList.get(i);
			}
		}
		return returnAttribute;
	}
}
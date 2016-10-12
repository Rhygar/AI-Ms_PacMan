package pacman.entries.pacman;

import java.util.HashMap;

import pacman.game.Constants.MOVE;

public class Node {
	boolean isLeafNode = true;
	String label = "";
	HashMap<String, Node> childrenNodes = new HashMap<String, Node>();
	
	public Node() {
		
	}
	
	public Node(String label) {
		this.label = label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}

	public boolean isLeafNode() {
		return isLeafNode;
	}
	
	
	public void addChild(String value, Node childNode) {
		childrenNodes.put(value, childNode);
		this.isLeafNode = false;
	}
	
	public Node getChild(String key) {
		return childrenNodes.get(key);
	}
	
	public HashMap<String, Node> getChildren() {
		return childrenNodes;
	}
	
	public String getName() {
		return this.label;
	}
	
 	public static void main(String[] args) {
 		Node N = new Node();
 		N.label = "Steve";
 		System.out.println(N.getName());
 		
 	}
}

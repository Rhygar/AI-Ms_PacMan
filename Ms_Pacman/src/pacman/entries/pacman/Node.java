package pacman.entries.pacman;

import java.util.HashMap;
import java.util.Map;

import pacman.game.Constants.MOVE;
/**
 * This class represents a node in a decision tree. Nodes can either be a leafnode or a parent to childrenNodes,
 * which are stored in a hashmap.
 * @author David Tran & John Tengvall
 * @date 19-10-2016
 */
public class Node {
	boolean isLeafNode = true;
	String label = "";
	HashMap<String, Node> childrenNodes = new HashMap<String, Node>();
	
	public Node() {}
	
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
	
	public void print(){
		print("");
	}
	
	/*
	 * This method prints the tree
	 */
	private void print(String indent) {

        if (this.isLeafNode) {
            System.out.print(indent);
            System.out.println("  └─ Return " + getLabel());
        }
        Map.Entry<String, Node>[] nodes = childrenNodes.entrySet().toArray(new Map.Entry[0]);
        for (int i = 0; i < nodes.length; i++) {
        	System.out.print(indent);
            if (i == nodes.length - 1) {
                System.out.println("└─ \"" + label + "\" = " + nodes[i].getKey() + ":");
                nodes[i].getValue().print(indent + "    ");
            } else {
                System.out.println("├─ \"" + label + "\" = " + nodes[i].getKey() + ":");
                nodes[i].getValue().print(indent + (char)0x007C +"   ");
            }
        }
    }
}

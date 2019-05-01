import java.util.ArrayList;

/**
 * Class representing a node of a graph
 */

public class Node {
	
	/**
	 * Constructs a new Node with given value
	 * @param val Value to be stored in node
	 */
	
	public Node(String val) {
		this.val=val;
		edges = new ArrayList<Edge>();
		inEdgeCount=0;
		outEdgeCount=0;
	}
	
	/**
	 * @return (k-1)-mer represented by node
	 */
	
	public String getVal() {
		return val;
	}
	
	/**
	 * Add given edge to this node's list of outgoing edges
	 * @param edge Edge to be added 
	 */
	
	public void addEdge(Edge edge) {
		edges.add(edge);
	}
	
	/**
	 * Increase incoming edge count by 1
	 */
	
	public void increaseInEdgeCount() {
		inEdgeCount++;
	}
	
	/**
	 * Returns this node's incoming edge count
	 * @return Incoming edge count
	 */
	
	public int getInEdgeCount() {
		return inEdgeCount;
	}
	
	/**
	 * Increase outgoing edge count by 1
	 */
	
	public void increaseOutEdgeCount() {
		outEdgeCount++;
	}
	
	/**
	 * Returns this node's outgoing edge count
	 * @return Outgoing edge count
	 */
	
	public int getOutEdgeCount() {
		return outEdgeCount;
	}
	
	/**
	 * Returns array of this node's outgoing edges
	 * @return array of this node's outgoing edges
	 */
	
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	
	/**
	 * Returns value stored in node
	 * @return value stored in node
	 */
	
	@Override
	public String toString() {
		return getVal();
	}
	
	private String val;
	private ArrayList<Edge> edges; //Only stores outgoing edges
	private int outEdgeCount;
	private int inEdgeCount;

}

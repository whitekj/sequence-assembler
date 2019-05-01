import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Class representing a de Bruijn graph
 */

public class Graph {
	
	/**
	 * Constructs a new Graph object
	 * @param kmers ArrayList of k-mers to be represented as edges in the graph
	 */
	
	public Graph(ArrayList<String> kmers) {
		adjList = new HashMap<Node, LinkedList<Node>>();
		nodeList = new HashMap<Integer,Node>();
		usedNodes = new HashSet<Node>();
		numNodes = 0;
		numEdges = 0;
		disconnectedEdgeCount = 0;
		wasBalanced = false;
		makeGraph(kmers);
	}
	
	/**
	 * Creates the de Bruijn graph, represented as adjacency list
	 */
	
	private void makeGraph(ArrayList<String> kmers) {
		for (String kmer : kmers) {
			//Get prefix of kmer
			String prefix = kmer.substring(0, kmer.length()-1);
			Node prefNode;
			//Find node with value of prefix
			int nodeIndex = findInNodeList(prefix);
			//If prefix node was not found in nodeList
			if (nodeIndex==-1) {
				//Assign as new node
				prefNode = new Node(prefix);
				nodeList.put(numNodes,prefNode);
				numNodes++;
			} else {
				//Else, set to node at retrieved index
				prefNode = nodeList.get(nodeIndex);
			}
			String suffix = kmer.substring(1, kmer.length());
			Node suffNode;
			nodeIndex = findInNodeList(suffix);
			if (nodeIndex==-1) {
				suffNode = new Node(suffix);
				nodeList.put(numNodes,suffNode);
				numNodes++;
			} else {
				suffNode = nodeList.get(nodeIndex);
			}
			//If prefix is in adjList
			if (adjList.containsKey(prefNode)) {
				//Add suffix node to prefNode's linkedlist
				adjList.get(prefNode).add(suffNode);
			}
			else {
				//If not in adjList, create new prefix and linkedlist w/ suffix
				LinkedList<Node> ll = new LinkedList<Node>();
				ll.add(suffNode);
				adjList.put(prefNode, ll);
			}
			//Add the new edge to the prefix node's list of edges
			Edge newEdge = new Edge(prefNode,suffNode);
			prefNode.addEdge(newEdge);
			numEdges++;
			//Increase edge count of prefix and suffix nodes
			prefNode.increaseOutEdgeCount();
			suffNode.increaseInEdgeCount();
		}
	}
	
	/**
	 * Finds Eulerian path in graph
	 *  @return String representation of eulerian path
	 */
	
	public ArrayList<String> findPath() {
		ArrayList<String> contigList = new ArrayList<String>();
		return findPath(null, contigList);
	}
	
	private ArrayList<String> findPath(ArrayList<String> contigList) {
		return findPath(null, contigList);
	}
	
	private ArrayList<String> findPath(ArrayList<Edge> usedEdges, ArrayList<String> contigList) {
		if(adjList == null || adjList.isEmpty()) {
			return contigList;
		}
		HashSet<Node> nodesInPath = new HashSet<Node>();
		Node startNode = null;
		//usedEdges should be null for first time method is run or if disconnect was found
		if(usedEdges == null) {
			//Balance the graph
			startNode = balanceGraph(usedNodes);
			if (startNode == null) {
				//Balancing error, return 
				return contigList;
			}
			//Initialize usedEdges
			usedEdges = new ArrayList<Edge>();
		}
		while((usedEdges.size()+disconnectedEdgeCount) < numEdges) {
			//Find node to start with
			Node currentNode = null;
			Node nextNode = null;
			if (usedEdges.size()==0) {
				//if no used edges yet, begin with startNode
				currentNode = startNode;
				LinkedList<Node> ll = adjList.get(startNode);
				if(ll == null || ll.isEmpty()) {
					return contigList;
				}
				nextNode = ll.getFirst();
				ll.removeFirst();
			} else {
				//Find node in path w/ unused edges
				for(Node node : nodesInPath) {
					LinkedList<Node> ll = adjList.get(node);
					if (!ll.isEmpty()) {
						currentNode = node;
						nextNode = ll.getFirst();
						//Remove nextNode from the linkedlist of currentNode
						ll.removeFirst();
						break;
					}
				}
				if (currentNode == null) {
					//Means there are unused edges that are disconnected from the main part of the graph
					//Update adjacency list
					updateAdjList();
					contigList.add(pathToString(usedEdges));
					//Restart method with empty usedEdges and changed adjList
					disconnectedEdgeCount = usedEdges.size();
					return findPath(contigList);
				}
			}
			ArrayList<Edge> usedEdgesCurrentLoop = new ArrayList<Edge>();
			Node firstInPath = currentNode;
			if(!nodesInPath.contains(currentNode)) {
				nodesInPath.add(currentNode);
				usedNodes.add(currentNode);
			}
			Edge nextEdge = new Edge(currentNode, nextNode);
			usedEdgesCurrentLoop.add(nextEdge);
			if(!nodesInPath.contains(nextNode)) {
				nodesInPath.add(nextNode);
				usedNodes.add(nextNode);
			}
			currentNode = nextNode;
			//Loop until returning to original node
			while(firstInPath != currentNode) {
				//Find next node
				LinkedList<Node> ll = adjList.get(currentNode);
				if(ll == null || ll.isEmpty()) {
					//Dead end
					updateAdjList();
					if(usedEdges.isEmpty()) {
						usedEdges = usedEdgesCurrentLoop;
					}
					//Update used edges
					updateUsedEdges(usedEdges, usedEdgesCurrentLoop, firstInPath);
					contigList.add(pathToString(usedEdges));
					//Restart method
					return findPath(contigList);
				}
				nextNode = ll.getFirst();
				ll.removeFirst();
				nextEdge = new Edge(currentNode, nextNode);
				usedEdgesCurrentLoop.add(nextEdge);
				if(!nodesInPath.contains(nextNode)) {
					nodesInPath.add(nextNode);
					usedNodes.add(nextNode);
				}
				currentNode = nextNode;
			}
			updateUsedEdges(usedEdges, usedEdgesCurrentLoop, firstInPath);
		}
		contigList.add(pathToString(usedEdges));
		return contigList;
	}
	
	/**
	 * Merges usedEdgesCurrentLoop into usedEdges
	 */
	
	private ArrayList<Edge> updateUsedEdges(ArrayList<Edge> usedEdges, ArrayList<Edge> usedEdgesCurrentLoop, Node firstInPath) {
		//Default index = last
		int index = usedEdges.size();
		//Find a used edge from firstInPath
		edgeSearch:
		for(Edge edge : firstInPath.getEdges()) {
			for(int i=0;i<usedEdges.size();i++) {
				if(usedEdges.get(i).getVal().equals(edge.getVal())) {
					index = i;
					break edgeSearch;
				}
			}
		}
		usedEdges.addAll(index,usedEdgesCurrentLoop);
		return usedEdges;
	}
	
	/**
	 * Adds extra temporary edge to graph
	 * Returns Node that is the child of temporary edge
	 */
	
	private Node balanceGraph(HashSet<Node> usedNodes) {
		Node parentNode = null; //Extra incoming edges
		Node childNode = null; //Extra outgoing edges
		//Find nodes with odd edge count
		for(Node node : nodeList.values()) {
			//If node was already used, skip it
			if(!usedNodes.contains(node)) {
				//Check if 1 extra incoming edge
				if(node.getInEdgeCount() > node.getOutEdgeCount()) {
					if(parentNode==null) {
						parentNode=node;
					}
				}
				//Check if 1 extra outgoing edge
				if(node.getInEdgeCount() < node.getOutEdgeCount()) {
					if(childNode==null) {
						childNode=node;
					}
				}
			}	
		}
		//Add new temporary edge
		if(childNode != null && parentNode != null) {
			//Add to adjList
			if (adjList.get(parentNode) == null) {
				//If parentNode not in adjList, add it
				adjList.put(parentNode,new LinkedList<Node>());
			}
			adjList.get(parentNode).add(childNode);
			Edge tempEdge = new Edge(parentNode,childNode,true);
			numEdges++;
			//Add to parent node's edge list
			parentNode.addEdge(tempEdge);
			wasBalanced = true;
			return childNode;
		}
		//If no balancing necessary, return first node in list
		return nodeList.get(0);
	}
	
	/**
	 * Removes nodes from adjacency list with empty node lists
	 */
	
	private void updateAdjList() {
		ArrayList<Node> toRemove = new ArrayList<Node>();
		for (Node key : adjList.keySet()) {
			if (adjList.get(key).isEmpty()) {
				toRemove.add(key);
			}
		}
		for(Node key : toRemove) {
			adjList.remove(key);
		}
	}
	
	/**
	 * Finds node with given value in nodeList, return -1 if none
	 */
	
	private int findInNodeList(String str) {
		for (int i=0;i<nodeList.size();i++) {
			if (nodeList.get(i).getVal().equals(str)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Converts list of edges to string
	 */
	
	private String pathToString(ArrayList<Edge> usedEdges) {
		if(usedEdges.isEmpty()) {
			return "";
		}
		String output = usedEdges.get(0).getVal();
		//If graph was balanced, skip last edge in list
		int lastEdge;
		if (wasBalanced) {
			lastEdge = usedEdges.size()-1;
		}
		else {
			lastEdge = usedEdges.size();
		}
		for(int i=1;i<lastEdge;i++) {
			Edge currentEdge = usedEdges.get(i);
			String val = currentEdge.getVal();
			output+=val.charAt(val.length()-1);
		}
		return output;
	}
	
	
	
	/**
	 * Prints adjacency list
	 */
	
	public void printAdjList() {
		System.out.println("Adjacency list:");
		for(Node key : adjList.keySet()) {
			System.out.print(key+" ");
			for (Node node : adjList.get(key)) {
				System.out.print(node+" ");
			}
			System.out.println("");
		}
	}
	
	/**
	 * Prints node list
	 */
	
	public void printNodeList() {
		System.out.println("Node list:");
		for(Node node : nodeList.values()) {
			System.out.println(""+node);
		}
		System.out.println("");
	}
	
	private HashMap<Node, LinkedList<Node>> adjList;
	private HashMap<Integer,Node> nodeList;
	private HashSet<Node> usedNodes;
	private int numNodes;
	private int numEdges;
	private int disconnectedEdgeCount;
	private boolean wasBalanced;
}
package dataStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import gui.GraphListener;

public class DGraph implements graph,Serializable{

	private static final long serialVersionUID = 1L;
	
	//DGraph Parameters:
	public HashMap<Integer, node_data> nodesMap = new HashMap<Integer, node_data>();
	public HashMap<Integer, HashMap<Integer,edge_data>> edgesMap = new HashMap<Integer, HashMap<Integer,edge_data>>();
	private int edgesCounter=0;
	private int MC=0;
	private GraphListener listener;

	//Constructor:
	public DGraph() {
		this.nodesMap = new HashMap<Integer, node_data>();
		this.edgesMap = new HashMap<Integer, HashMap<Integer,edge_data>>();
		this.edgesCounter=0;
		this.MC=0;
	}
	
	public void addListener(GraphListener listener){
		this.listener = listener;
	}
	
	public void updateListener(){
		if(listener != null)
			listener.graphUpdater();
	}

	//deep copy.
	public DGraph(DGraph G) {
		this.nodesMap.putAll(G.nodesMap);
		this.edgesMap.putAll(G.edgesMap);
		this.MC=G.MC;
		this.edgesCounter=G.edgesCounter;
	}

	//Methods:
	@Override
	public node_data getNode(int key) { 
		if (this.nodesMap.get(key)==null) { return null; }
		return this.nodesMap.get(key); 
	}

	@Override
	public edge_data getEdge(int src, int dest) {
		if (this.edgesMap.get(src)!=null) {
			if (this.edgesMap.get(src).get(dest) != null) {
				return (edge_data)(this.edgesMap.get(src).get(dest)); 
			}
		}
		return null;
	}

	@Override
	public void addNode(node_data n) {
		int key=n.getKey();
		this.nodesMap.put(key, (node)n);
		this.MC++;
		updateListener();
	}

	@Override
	public void connect(int src, int dest, double w) {
		if (this.nodesMap.get(src)==null || this.nodesMap.get(dest)== null) {
			System.out.println("Can't connect nodes");
		}
		else {
			edge temp = new edge(src,dest,w);
			if (this.edgesMap.get(src) == null) {
				this.edgesMap.put(src, new HashMap<Integer,edge_data>());
				this.edgesMap.get(src).put(dest, temp);
				edgesCounter++;
				this.MC++;
				updateListener();
			}
			else {
				this.edgesMap.get(src).put(dest, temp);
				edgesCounter++;
				this.MC++;
				updateListener();
			}
		}
	}

	@Override
	public Collection<node_data> getV() {
		if (this.nodesMap.isEmpty()) { return null; }
		return this.nodesMap.values(); 
	}

	@Override
	public Collection<edge_data> getE(int node_id) {
		if (this.edgesMap.isEmpty()) { return null; }
		if (this.edgesMap.get(node_id)==null) { return null; }
		return this.edgesMap.get(node_id).values(); 
	}

	@Override
	public node_data removeNode(int key) {

		if (this.nodesMap.get(key)==null) { return null; }

		node_data ans = new node((node)nodesMap.get(key));//for data-return
		ArrayList<Integer> toD = new ArrayList<Integer>();// to-Delete all empty HashMaps.

		//remove all edges going into key-node.
		this.edgesMap.forEach((k, v) -> {
			if (v.get(key)!=null) {
				v.remove(key);
				edgesCounter--;
				this.MC++;
				if (v.isEmpty()) {
					toD.add(k);
				}
			}
		});
		for (int i : toD) {
			this.edgesMap.remove(i);
		}

		//remove all edges coming out of key-node.
		edgesCounter -= this.edgesMap.get(key).size();
		this.edgesMap.remove(key);
		//remove the key-node.
		this.nodesMap.remove(key);
		this.MC++;
		updateListener();
		return ans;
	}

	@Override
	public edge_data removeEdge(int src, int dest) {
		if (this.edgesMap.get(src).get(dest)==null) { return null; }
		edge_data e = new edge((edge)this.edgesMap.get(src).get(dest));

		this.edgesMap.get(src).remove(dest);
		edgesCounter--;
		this.MC++;
		updateListener();
		return e;
	}

	@Override
	public int nodeSize() { return this.nodesMap.size(); }

	@Override
	public int edgeSize() { return edgesCounter; }

	@Override
	public int getMC() { return MC; }

	public boolean containsN(int k) {
		if (this.nodesMap.containsKey(k)) { return true; }
		return false;
	}

	public boolean containsE(int s, int d) {
		if (this.edgesMap.containsKey(s)) {
			if (this.edgesMap.get(s).containsKey(d)) { return true; }
		}
		return false;
	}
}
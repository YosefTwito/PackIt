package dataStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Point3D;

public class DGraph extends Observable implements graph,Serializable{

	private static final long serialVersionUID = 1L;
	
	//DGraph Parameters:
	public HashMap<Integer, node_data> nodesMap = new HashMap<Integer, node_data>();
	public HashMap<Integer, HashMap<Integer,edge_data>> edgesMap = new HashMap<Integer, HashMap<Integer,edge_data>>();
	private int edgesCounter=0;
	private int MC=0;

	//Constructor:
	public DGraph() {
		this.nodesMap = new HashMap<Integer, node_data>();
		this.edgesMap = new HashMap<Integer, HashMap<Integer,edge_data>>();
		this.edgesCounter=0;
		this.MC=0;
	}
	
	//deep copy.
	public DGraph(DGraph G) {
		this.nodesMap.putAll(G.nodesMap);
		this.edgesMap.putAll(G.edgesMap);
		this.MC=G.MC;
		this.edgesCounter=G.edgesCounter;
	}
	
	/**
	 * Initialize graph from json
	 * @param g - String with the graph data
	 */
	public void init(String g) {
		
		try {
			JSONObject jobj = new JSONObject(g);
			JSONArray Jedges = jobj.getJSONArray("Edges");
			JSONArray Jnodes = jobj.getJSONArray("Nodes");	

			for (int i = 0; i < Jnodes.length(); i++) {
				JSONObject nody= (JSONObject) Jnodes.get(i);
				String location = (String) nody.getString("pos");
				String[] points = location.split(",");
				double x = Double.parseDouble(points[0]);
				double y = Double.parseDouble(points[1]);	
				double z = Double.parseDouble(points[2]);
				int id = nody.getInt("id");
				Point3D p = new Point3D(x,y,z);
				node_data n = new node(id, p);
				this.addNode(n);
			}
			for (int i = 0; i < Jedges.length(); i++) {
				JSONObject edgeE= (JSONObject) Jedges.get(i);
				int src = edgeE.getInt("src");
				int dest = edgeE.getInt("dest");
				double weight = edgeE.getDouble("w");
				this.connect(src, dest, weight);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
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
		notifyObservers(n);
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
				notifyObservers();
			}
			else {
				this.edgesMap.get(src).put(dest, temp);
				edgesCounter++;
				this.MC++;
				notifyObservers();
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
				notifyObservers();
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
		notifyObservers();
		return ans;
	}

	@Override
	public edge_data removeEdge(int src, int dest) {
		if (this.edgesMap.get(src).get(dest)==null) { return null; }
		edge_data e = new edge((edge)this.edgesMap.get(src).get(dest));

		this.edgesMap.get(src).remove(dest);
		edgesCounter--;
		this.MC++;
		notifyObservers();
		return e;
	}

	@Override
	public int nodeSize() { return this.nodesMap.size(); }

	@Override
	public int edgeSize() { return edgesCounter; }

	@Override
	public int getMC() { return MC; }

	/**
	 * @param key - of the node you want to find
	 * @return True if the graph contains the node with this key
	 */
	public boolean containsN(int key) {
		if (this.nodesMap.containsKey(key)) { return true; }
		return false;
	}

	/**
	 * @param src - of the edge you want to find
	 * @param dest - of the edge you want to find
	 * @return True if the graph contains the edge with those src and dest
	 */
	public boolean containsE(int src, int dest) {
		if (this.edgesMap.containsKey(src)) {
			if (this.edgesMap.get(src).containsKey(dest)) { return true; }
		}
		return false;
	}
}
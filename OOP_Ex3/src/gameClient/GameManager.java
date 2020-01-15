package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;

/**
 * this class represents an automatic way to play.
 * should guarantee best times and score.
 * @author Eldar and Yossi
 *
 */



public class GameManager {
	game_service Scenario;
	DGraph graph;
	ArrayList<Robot> robo_list = new ArrayList<Robot>(); // list of robots we have
	ArrayList<Fruit> fru_list = new ArrayList<Fruit>(); // list of fruits we have

	//Constructor: not finished.
	public GameManager(int s,DGraph g,ArrayList<Robot> robo_list,ArrayList<Fruit> fru_list) {
		this.Scenario=Game_Server.getServer(s);
		this.graph=g;
		this.robo_list=robo_list;
		this.fru_list=fru_list;
	}
	
	public GameManager() {
		
	}
	//returns the key of the node that is the best place to start
	//ALGO: start from the node that has the most number of edges going out of it.
	//NOT THE BEST, JUST FOR NOW.
	public int whereToStart() {
		int max =0;
		node_data ans=null;
		//Graph_Algo temp = new Graph_Algo(graph); TO BE USED LATER WITH ALGOS
		for(node_data nd : graph.getV()) {
			Collection<edge_data> ed=graph.getE(nd.getKey());
			if(ed==null) break;
			if(ed.size()>max) {
				max=ed.size();
				ans=nd;
			}
		}
		return ans.getKey();
	}
	//returns the key of the node that is the best place to go
	//ALGO: JUST PICK ONE OF THE NODES
	//TO BE CONTINUED
	public int whereNext() {
		node_data ans=null;
		//Graph_Algo temp = new Graph_Algo(graph); TO BE USED LATER WITH ALGOS
		for(node_data nd : graph.getV()) {
			Collection<edge_data> ed=graph.getE(nd.getKey());
			if(ed==null) break;
			ans=nd;
		}
		return ans.getKey();
		
	}

}

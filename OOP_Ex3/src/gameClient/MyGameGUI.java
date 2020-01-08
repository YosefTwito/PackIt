package gameClient;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import dataStructure.graph;
import dataStructure.node_data;
import dataStructure.DGraph;
import dataStructure.edge_data;
import gui.*;
import utils.Point3D;

/**
 * this class represents the gui's game.
 * we need to fetch the amount of robots and fruits and put them on our gui.
 * than we need to implement the game as expected.
 * one implementation is an auto-play with directed scenario according to the files.
 * another is manual-play where we decide where to point the robot.
 * @author Eldar and Yossi
 *
 */
public class MyGameGUI {
	static graph graph;
	ArrayList<Robot> robo_list = new ArrayList<Robot>(); // list of robots we have
	ArrayList<Fruit> fru_list = new ArrayList<Fruit>(); // list of fruits we have



	public static void main(String[] args) {
		game_service game = Game_Server.getServer(2); // this is where we get the user input too know what game to play [0,23];
		String g = game.getGraph(); // graph as string.
		DGraph gg = new DGraph();
		gg.init(g); // TODO init from json string to DGraph in DGraph!!!
		//we have the graph. now we need to get the robots and fruits.
		//after getting the fruits and robots, we need to update our graph with the location of fruits and robots.
		//after that, we need to update our GUI with new parameters and present it.
		
		
	
		
		
		
	}
	public MyGameGUI(graph g) {
		graph=g;
	}
	public MyGameGUI() {
		graph=null;
	}
	//takes the json of a robot and make an object out of it.
	public void fetchRobots(game_service g) {
		
		List<String> log = g.getRobots();
		if(log!=null) {
			String robot_json = log.get(0);
			JSONObject line;
			
			try {
				//need to do loop
				line = new JSONObject(robot_json);
				JSONObject ttt = line.getJSONObject("Robot");
				int rid = ttt.getInt("id");
				int src = ttt.getInt("src");
				int dest = ttt.getInt("dest");
				int x = ttt.getInt("pos"); // TODO parse the json string and get all robots.
				int y = ttt.getInt("pos");
				int z = ttt.getInt("pos");
				Point3D p = new Point3D(x,y,z);
				double val = ttt.getDouble("value");
				Robot r = new Robot(rid,src,dest,p,val);
				robo_list.add(r);
				if(dest==-1) {	
					dest = nextNode(graph, src);
					g.chooseNextEdge(rid, dest);
					System.out.println("Turn to node: "+dest);
					System.out.println(ttt);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
		
	
}
	void fetchFruits(game_service g) {
		List<String> log = g.getFruits();
		if(log!=null) {
			String fru_json = log.get(0);
			JSONObject line;
			
			try {
				line = new JSONObject(fru_json);
				JSONObject t = line.getJSONObject("Fruit");
				double value = t.getDouble("value");
				int type = t.getInt("type");
				int x = t.getInt("pos"); // TODO iterate thro the json array and fetch the x,y,z respectively.
				int y = t.getInt("pos"); // prase through json string and get all fruits.
				int z = t.getInt("pos");
				Point3D p = new Point3D(x,y,z);
				Fruit f = new Fruit(value,type,p);
				fru_list.add(f);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	private static int nextNode(graph g, int src) {
		int ans = -1;
		Collection<edge_data> ee = g.getE(src);
		Iterator<edge_data> itr = ee.iterator();
		int s = ee.size();
		int r = (int)(Math.random()*s);
		int i=0;
		while(i<r) {itr.next();i++;}
		ans = itr.next().getDest();
		return ans;
	}
}
	
	



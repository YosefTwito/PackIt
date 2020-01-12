package gameClient;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Server.*;
import dataStructure.*;
import gui.*;
import utils.Point3D;

/**
 * this class represents the game.
 * we need to fetch the amount of robots and fruits.
 * than we need to implement the game as expected.
 * one implementation is an auto-play with directed scenario according to the files.
 * another is manual-play where we decide where to point the robot.
 * the class also has algorithms to play the auto-player.
 * @author Eldar and Yossi
 *
 */
public class MyGame {
	game_service game;
	static graph graph;
	ArrayList<Robot> robo_list = new ArrayList<Robot>(); // list of robots we have
	ArrayList<Fruit> fru_list = new ArrayList<Fruit>(); // list of fruits we have



	public static void main(String[] args) {
		game_service game = Game_Server.getServer(4); // this is where we get the user input too know what game to play [0,23];
		String g = game.getGraph(); // graph as string.
		DGraph gg = new DGraph();
		game.addRobot(1);
		gg.init(g); // TODO init from json string to DGraph in DGraph!!!
		//we have the graph. now we need to get the robots and fruits.
		//after getting the fruits and robots, we need to update our graph with the location of fruits and robots.
		//after that, we need to update our GUI with new parameters and present it.
		
		//GarphGui gui = new GraphGui(myg); // YOSSI TA'ASE INIT LEZE <3
		
		
		
		
		
	
		
		
		
	}
	public MyGame(graph g,game_service game) {
		graph=g;
		this.game=game;
		fetchRobots(game);
		fetchFruits(game);
	}
	public MyGame() {
		graph=null;
		this.game=null;
	}
	
	private void fetchRobots(game_service g) {
		
		List<String> log = g.getRobots();
		if(log!=null) {
			String robot_json = log.toString();

			try {
				JSONArray line= new JSONArray(robot_json);
				
				for(int i=0; i< line.length();i++) {
					
					JSONObject j= line.getJSONObject(i);
					JSONObject jrobots = j.getJSONObject("Robot");
					String loc = jrobots.getString("pos");
					String[] xyz = loc.split(",");
					double x = Double.parseDouble(xyz[0]);
					double y = Double.parseDouble(xyz[1]);	
					double z = Double.parseDouble(xyz[2]);
					Point3D p = new Point3D(x,y,z);
					int rid = jrobots.getInt("id");
					int src = jrobots.getInt("src");
					int dest = jrobots.getInt("dest");
					double val = jrobots.getDouble("value");
					Robot r = new Robot(rid,src,dest,p,val);
					robo_list.add(r);
					
					
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
		
	
}
	private void fetchFruits(game_service g) {
		List<String> log = g.getFruits();
		if(log!=null) {
			String fru_json = log.toString();

			try {
				JSONArray line= new JSONArray(fru_json);
				
				
				for(int i =0; i<line.length();i++) {
					JSONObject j = line.getJSONObject(i);
					JSONObject fru = j.getJSONObject("Fruit");
					String loc = fru.getString("pos");
					String[] xyz = loc.split(",");
					double x = Double.parseDouble(xyz[0]);
					double y = Double.parseDouble(xyz[1]);
					double z = Double.parseDouble(xyz[2]);
					Point3D p = new Point3D(x,y,z);
					double value = fru.getDouble("value");
					int type = fru.getInt("type");
					Fruit f = new Fruit(value,type,p);
					fru_list.add(f);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	/* TO BE USED LATER
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
	*/
	
	private static int nextNodeManual(graph g, int src,int dest) {
		if(graph.getNode(dest)==null) return -1;
		int ans = -1;
		Collection<edge_data> ee = g.getE(src);
		for(edge_data e:ee) {
			if(e==null) return -1;
			if(e.getDest()==graph.getNode(dest).getKey()) return 1;
		}
		return ans;
	
	}
	
	private long timeToEnd() {
		return this.game.timeToEnd()/1000;
	}
	
	private long startGame() {
		return this.game.startGame();
	}
	
	private long stopGame() {
		return this.game.stopGame();
	}
	
	private boolean isRunning() {
		return game.isRunning();
	}
	
	private String Score(ArrayList<Robot> al){
		String ans ="";
		for(int i=0;i<al.size();i++) {
			ans+="Robot #:"+i+" Scored:"+al.get(i).value;
			ans+="\n";
		}
		return ans;
	}
	/**
	 * Calculates the edge that contains the fruit based on their location.
	 * based on the Triangle Equivalence
	 * @param f - fruit that we want to determine on witch edge it sits.
	 * @param g - graph
	 * @return the edge_data that hold the fruit.
	 */
	private edge_data fruitToEdge(Fruit f,graph g) {
		edge_data ans = null;
		Point3D f_p = f.pos;
		Collection<node_data> nd = g.getV();
		for(node_data n:nd) {
			Point3D ns_p = n.getLocation();
			Collection<edge_data> ed = g.getE(n.getKey());
			if(ed==null) break;
			for(edge_data e : ed) {
				Point3D nd_p = g.getNode(e.getDest()).getLocation();
				if(ns_p.distance3D(f_p)+f_p.distance3D(nd_p)==ns_p.distance3D(nd_p)) {
					return e;
				}
			}
		}
		return ans;
	}
	
	
}
	
	



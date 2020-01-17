package gameClient;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.plaf.synth.SynthDesktopIconUI;

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
	public game_service game;
	public graph graph;
	public ArrayList<Robot> robo_list = new ArrayList<Robot>(); // list of robots we have
	public ArrayList<Fruit> fru_list = new ArrayList<Fruit>(); // list of fruits we have
	public double score=0;



	public static void main(String[] args) {
		game_service game = Game_Server.getServer(12); // this is where we get the user input too know what game to play [0,23];
		String g = game.getGraph(); // graph as string.

		DGraph gg = new DGraph();
		game.addRobot(0);
		game.addRobot(0);
		game.addRobot(0);
	
		gg.init(g);
		//we have the graph. now we need to get the robots and fruits.
		//after getting the fruits and robots, we need to update our graph with the location of fruits and robots.
		//after that, we need to update our GUI with new parameters and present it.
		
		//GarphGui gui = new GraphGui(myg); // YOSSI TA'ASE INIT LEZE <3
		
		
		System.out.println(game.getFruits());
		MyGame mg = new MyGame(gg,game);
		
	
		
		
		
	}
	public MyGame(graph g,game_service game) {
		graph=g;
		this.game=game;
		fetchRobots();
		fetchFruits();
	}
	public MyGame() {
		graph=null;
		this.game=null;
	}
	
	public void updategame(game_service game) {
		this.game=game;
	}
	
	private void fetchRobots() {
		
		List<String> log = game.getRobots();
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
					int speed = jrobots.getInt("speed");
					Robot r = new Robot(rid,src,dest,p,val,speed);
					robo_list.add(r);
					
					
				}
			

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
		
	
}
	private void fetchFruits() {
		List<String> log = game.getFruits();
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
	private int startHere(graph g,int src) {
		int ans=0;
		double temp=0;
		if(g.edgeSize()<30) {
			for(Fruit f:fru_list) {
				if(f.value>temp) {
					ans=f.from;
					temp=f.value;
				}
				
			}
			return ans;
		}
		else  {
			for(node_data nd:g.getV()) {
				int s= g.getE(nd.getKey()).size();
				if(s>temp) {
					ans=nd.getKey();
					temp=s;
				}
				
			}
		}
		return ans;
		
	}
	
	
	private static int nextNodeManual(graph g, int src,int dest) {
		if(g.getNode(dest)==null) return -1;
		int ans = -1;
		Collection<edge_data> ee = g.getE(src);
		for(edge_data e:ee) {
			if(e==null) return -1;
			if(e.getDest()==g.getNode(dest).getKey()) return 1;
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
	
	public String Score(ArrayList<Robot> al){
		String ans ="";
		int total=0;
		for(int i=0;i<al.size();i++) {
			total+=al.get(i).value;
			ans+="Robot #:"+i+" Scored:"+al.get(i).value;
			ans+="\n";
			this.score=total;
		}
		
		System.out.println("Final Score is"+score);
		return ans;
	}

	/**
	 * Calculates the edge that contains the fruit based on their location.
	 * based on the Triangle Equivalence
	 * @param f - fruit that we want to determine on witch edge it sits.
	 * @param g - graph
	 * @return the edge_data that hold the fruit.
	 */
	public edge_data fruitToEdge(Fruit f,graph g) {
		edge_data ans = null;
		Point3D f_p = f.pos;
		Collection<node_data> nd = g.getV();
		for(node_data n:nd) {
			Point3D ns_p = n.getLocation();
			Collection<edge_data> ed = g.getE(n.getKey());
			if(ed==null) continue;
			for(edge_data e : ed) {
				Point3D nd_p = g.getNode(e.getDest()).getLocation();
				if((ns_p.distance3D(f_p)+f_p.distance3D(nd_p))-ns_p.distance3D(nd_p)<0.000001) {
					return e;
				}
			}
		}
		
		return ans;
	}
	public boolean close(Robot r) {
		for(Fruit f:fru_list) {
			if(r.src==f.from && r.dest==f.to) return true;
		}
		return false;
	}
	

	
	public void upDate() {
		
		
		robo_list.clear();
		
		List<String> log = game.move();	
		
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
					int speed = jrobots.getInt("speed");
					Robot r = new Robot(rid,src,dest,p,val,speed);
					robo_list.add(r);
					
					
					
					
					
					
				}
				
				for(Robot r:robo_list) {
					
					
					if(r.dest==-1) {
						update();
						System.out.println(Score(robo_list));
						r.setDest(nextNode(graph, r.src));
						game.chooseNextEdge(r.id, r.dest);
						
					}
					
					
				}
				

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}

	}
	public void update() {
		fru_list.clear();
		List<String> log = game.getFruits();
		System.out.println(log);
		
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
	public int whereToStart() {
		int ans=0;
		double temp=0;
		for(node_data nd : graph.getV()) {
			for(edge_data ed: graph.getE(nd.getKey())) {
				for(Fruit f:fru_list) {
					if(f.from==ed.getSrc() && f.to==ed.getDest()) {
						if(f.value>temp) {
							ans=f.from;
							temp=f.value;
						}
					}
				}
			}
		}
		return ans;
	}
		
		
	
	
	
	
	
}
	
	



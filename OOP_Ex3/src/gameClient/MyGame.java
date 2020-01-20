package gameClient;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.plaf.synth.SynthDesktopIconUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Server.*;
import algorithms.Graph_Algo;
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
 */
public class MyGame {
	public game_service game;
	public graph graph;
	public ArrayList<Robot> robo_list = new ArrayList<Robot>(); // list of robots we have
	public ArrayList<Fruit> fru_list = new ArrayList<Fruit>(); // list of fruits we have
	public double score=0;
	private static KML_Logger kml=new KML_Logger();
	public int topfruitTo;
	public int topfruitFrom;




	public static void main(String[] args) {
		game_service game = Game_Server.getServer(7); // this is where we get the user input too know what game to play [0,23];
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
		
		
		
		MyGame mg = new MyGame(gg,game);
		mg.SetTop();
		mg.game.startGame();
		

		for(Fruit f:mg.fru_list) System.out.println("|"+f.from);
		Fruit a = mg.topFruit();
		while(mg.isRunning()) {
		mg.robo_list=mg.upDate(a.from,a.to);
		Robot r = mg.robo_list.get(0);
		System.out.println(r.pos);
		for(Fruit f:mg.fru_list) System.out.println("|"+f.from);

		}

	}
	public void SetTop() {
		double temp =0;
		Fruit tfru = new Fruit();
		for(Fruit f:fru_list) {
			if(f.value>temp) {
				temp = f.value;
					
			}
			tfru=f;
			
		}
		this.topfruitFrom=tfru.from;
		this.topfruitTo=tfru.to;
		
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
	/**
	 * function that parse the json and retracts robotos to array list of robots.
	 */
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
	/**
	 * function that parse the json and retracts the fruits to array list of fruits
	 */
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
					f=setFnT(f,graph);
					
					fru_list.add(f);
					
					
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	/**
	 * a random walk on the graph
	 * @param g graph
	 * @param src src of the robot
	 * @return the node the robot will head to
	 */
	
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
	
	public List<node_data> go(graph g,int src,int from,int to) {
		//Fruit f = topFruit();
		
		Graph_Algo ga = new Graph_Algo(g);
		//f= setFnT(f, g);

		List<node_data>arr = ga.shortestPath(src, from);
		
		arr.add(g.getNode(to));
		arr.add(g.getNode(from));
		
		return arr;
		
	}
	/**
	 * more clever approach to decide where to drive the robot.
	 * will drive the robot to the closet and most valuable fruit.
	 * @param r robot
	 */
	public void goNext(Robot r,graph g) {
		Fruit f = this.topFruit();
		Graph_Algo ga = new Graph_Algo(g);
		
		
		List<node_data>arr = ga.shortestPath(r.src, f.from);
		arr.add(g.getNode(f.to));
		for(node_data temp:arr) {
			game.chooseNextEdge(r.id, temp.getKey());
		}
	
	}
	/**
	 * calculates where is the best point to start the game from.
	 * @return the node its best to start in
	 */
	
	public int startHere() {

		int k = graph.nodeSize();
		return (int)(Math.random()*k+1);
	}
	
	public Fruit topFruit() {
		Fruit fru = new Fruit();
		double temp=0;
		for(Fruit f:fru_list) {
			if(f.value>temp) {
				temp = f.value;
				fru = f;
			}
			
		}
		return fru;
	}
	/**
	 * checks if the robot can go to the selected node
	 * @param r robot
	 * @param src node of the robot
	 * @param dest node of the robot
	 * @return
	 */
	
	public boolean nextNodeManual(Robot r,int src,int dest) {
		if(graph.getNode(dest)==null) return false;
		boolean ans = false;
		Collection<edge_data> ee = graph.getE(src);
		for(edge_data e:ee) {
			if(e==null) return false;
			if(e.getDest()==graph.getNode(dest).getKey()) {
				//r.setDest(nextNode(graph, r.src));
				game.chooseNextEdge(r.id, dest);
				return true;
			}
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
	/**
	 * calcualtes the score of each robot 
	 * also prints the combined score
	 * @param al array list of the robots
	 * @return string that shows each robot and its score
	 */
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
	public static edge_data fruitToEdge(Fruit f,graph g) {
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
					f.setFrom(e.getSrc());
					f.setTo(e.getDest());
					return e;
				}
			}
		}
		
		return ans;
	}
	public Fruit setFnT(Fruit f,graph g) {
		Fruit ans = new Fruit();
		ans.type=f.type;
		ans.pos=f.pos;
		Point3D f_p = f.pos;
		Collection<node_data> nd = g.getV();
		for(node_data n:nd) {
			Point3D ns_p = n.getLocation();
			Collection<edge_data> ed = g.getE(n.getKey());
			if(ed==null) continue;
			for(edge_data e : ed) {
				Point3D nd_p = g.getNode(e.getDest()).getLocation();
				if((ns_p.distance3D(f_p)+f_p.distance3D(nd_p))-ns_p.distance3D(nd_p)<0.000001) {
					ans.setFrom(e.getSrc());
					ans.setTo(e.getDest());
					return ans;
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
	
	/**
	 * updates the robot list constantly so the robot location and score would update
	 * while the game is running.
	 * fetches the data from the server and updates the robo list.
	 */	
	public ArrayList<Robot> upDate(int from,int to) {
		try {
			kml.make_kml(this,0);
		} catch (ParseException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> log = game.move();	
		if(log!=null) {
			robo_list.clear();
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
		
		for(Robot r:robo_list) {
			
			if(r.dest==-1) {
				update();
			
				List<node_data> temp2 = go(graph,r.src,from,to);
				
				for(node_data nd:temp2) {
					
					r.setDest(nd.getKey());
					
					game.chooseNextEdge(r.id,r.dest);
					
				
				
//				r.dest = nextNode(graph, r.src);
//				game.chooseNextEdge(r.id,r.dest);
			//	System.out.println(r.id+"|"+r.dest+"|"+r.pos);
				
				
			}	
			}
		}
		return robo_list;
	}
	/**
	 * updates the fruit list constantly while the game is running.
	 * fetches the data from the server
	 */
	public void update() {
		fru_list.clear();
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
					f=setFnT(f,graph);
					fru_list.add(f);
					
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}

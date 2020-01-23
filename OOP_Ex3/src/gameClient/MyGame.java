package gameClient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Server.*;
import algorithms.Graph_Algo;
import dataStructure.*;
import elements.Fruit;
import elements.Robot;
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
	public String score;
	int level;
	private static KML_Maker kml=new KML_Maker();
	private int moves=0;

	public String getScore() { return score; }

	public int get_level() { return this.level; }

	public int getMoves() {return this.moves;}
	
	public MyGame() {;}
	
	public MyGame(graph g,game_service game, int level) {
		this.graph=g;
		this.game=game;
		this.level=level;
		fetchRobots();
		fetchFruits();
	}
	
	

	
	/**
	 * main driver of the game.
	 * gets the decision of the user regard the level and mode of the game
	 * then starts the game.
	 * @param args
	 */


	public static void main(String[] args) {
		int level = getLevel();
		int mode = getMode();

		game_service game = Game_Server.getServer(level); 
		String g = game.getGraph(); 

		DGraph gg = new DGraph();
		game.addRobot(8);
		game.addRobot(32);
		game.addRobot(40);

		gg.init(g);


		MyGame mg = new MyGame(gg,game,level);
		mg.goGo(mode);

	}
	/**
	 * asks the user about the mode he wants to play.
	 * Automate or Manual
	 * @return
	 */
	private static int getMode() {
		ImageIcon robo = new ImageIcon("robotB.png");
		String[] Mode = {"Automate", "Manual"};
		int ModeNum = JOptionPane.showOptionDialog(null, "Choose the Mode you would like to display", "Click a button",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, Mode, Mode[0]);
		if (ModeNum<0) ModeNum=0;//in case user don't pick and press x
		return ModeNum;
	}
	/**
	 * asks the user about the level he wants to play.
	 * [0,23]
	 * @return
	 */
	private static int getLevel() {
		// Logo for options-dialog
		ImageIcon robo = new ImageIcon("robotB.png");

		//Set the game Level - [0,23]
		String[] options = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
		int gameNum = JOptionPane.showOptionDialog(null, "Choose the Level you would like to display", "Click a button",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, options, options[0]);
		if (gameNum<0) gameNum=0;//in case user don't pick and press x
		return gameNum;

	}
	

	/**
	 * logs to the server with the users ID
	 * than initialize the game with users decisions
	 * when game is over, generates a KML and shows score
	 * @param mode
	 */
	public void goGo(int mode) {

		MyGameGUI r = new MyGameGUI((DGraph)this.graph,game,7,this);
		r.setVisible(true);


		//Game_Server.login(314732637);

		game.startGame();

		while(game.isRunning()) {
			this.robo_updater(mode);
			r.run();
			fru_updater();
			try {
				kml.makeKML(this,0);
			} catch (ParseException | InterruptedException e){ e.printStackTrace(); }


		}

		String res = game.toString();
		game.sendKML("kmlFile.kml");
		System.out.println(res);

		JOptionPane.showMessageDialog(null, ("           Your Score is: "+Score(this.robo_list)));


	}

	/**
	 * function that parse the json and builds an arraylist of robots.
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
			} catch (JSONException e) { e.printStackTrace(); }	
		}
	}
	/**
	 * function that parse the json and builds an arraylist of fruits
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
					

					fru_list.add(f);
				}
			} catch (JSONException e) { e.printStackTrace(); }	
		}
	}
	/**
	 * a random walk on the graph
	 * @param g graph
	 * @param src src of the robot
	 * @return the node the robot will head to
	 */

	public static int nextNode(graph g, int src) {
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

	/**
	 * checks if the robot can go to the selected node
	 * used only in Manual mode
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
				game.chooseNextEdge(r.getID(), dest);
				return true;
			}
		}
		return ans;

	}
	/**
	 * checks if game is running at the moment
	 * @return true if it is
	 */
	public boolean isRunning() {
		return game.isRunning();
	}

	/**
	 * Calculates the score of each robot 
	 * also prints the combined score
	 * @param al array list of the robots
	 * @return string that shows each robot and its score
	 */
	public int Score(ArrayList<Robot> al){
		int ans = 0;
		int total=0;
		for(int i=0;i<al.size();i++) {
			total+=al.get(i).getV();
			ans+=al.get(i).getV();

			this.score="\n";
		}
		this.score=""+total;
		this.score+=total;
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
		Point3D f_p = f.getPos();
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



	/**
	 * updates the game data from the server while game is running
	 * the data that is being updated is Robot pos,value, src and dest
	 * @param mode Manual - 1 || Automate - 0
	 * @return arraylist of robots that has been updated
	 */
	public ArrayList<Robot> robo_updater(int mode) {
		List<String> log = game.move();
		moves++;
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
					r.setLast(src);
				}
			} catch (JSONException e) { e.printStackTrace(); }
		}
		for(Robot r:robo_list) {
			if(r.getDest()==-1) {
				if(mode==0) {
					
					int nodetoGO = setNext(r, graph, fru_list);
					game.chooseNextEdge(r.getID(), nodetoGO);
					//					for(node_data nd:temp2) {				
					//						r.setDest(nd.getKey());
					//						
					//					game.chooseNextEdge(r.getID(),r.getDest());			
									
				}
				else {
					ImageIcon robo = new ImageIcon("robotB.png");
					int size = this.graph.getE(r.getSrc()).size();
					int [] tem = new int[size];
					String[] options = new String[size];
					ArrayList<edge_data> temp = new ArrayList<edge_data>();
					temp.addAll(graph.getE(r.getSrc()));
					for(int i=0;i<size;i++) {
						tem[i]=temp.get(i).getDest();
						options[i]=""+temp.get(i).getDest();

					}
					int ryyy = JOptionPane.showOptionDialog(null, "Enter node to go - Robot id:"+r.getID(), "Click", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, options, options[0]);
					int dest= tem[ryyy];
					nextNodeManual(r, r.getSrc(), dest);
				}
			}
	}
		
		try {
			Thread.sleep(15);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		return robo_list;

	}

	/**
	 * updates the game data from the server while game is running
	 * the data that is being updated is Fruit pos,value,src and dest
	 * 
	 * 
	 */
	public void fru_updater() {

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
					fru_list.add(f);

				}
			} catch (JSONException e) { e.printStackTrace(); }
		}
	}
	

	
	/**
	 * calculates the next node that the robot should go to 
	 * @param r robot 
	 * @param g graph
	 * @param fru_list arraylist of fruits
	 * @return the key of the node
	 */

	private int setNext(Robot r , graph g, List<Fruit> fru_list ) {
		Graph_Algo ga = new Graph_Algo(g);
		edge_data temp_edge = null;
		
		int next=-1;
		int decision =-1;
		
		double min = 9999;
		double distFromMe = 0;
	
		for (Fruit fruit: fru_list) {
			if (fruit.getTag() == 0) {
				temp_edge = fruitToEdge(fruit,g); // return the edge that the fruit is sitting on
				if (fruit.getType() == -1) { // 
					if (temp_edge.getDest() > temp_edge.getSrc()) {
						distFromMe = ga.shortestPathDist(r.getSrc(), temp_edge.getDest()); //return the shortest path between robot and fruit
						next = temp_edge.getSrc();
					} 
					else if (temp_edge.getSrc() > temp_edge.getDest()) {
						distFromMe = ga.shortestPathDist(r.getSrc(), temp_edge.getSrc()); //return the shortest path between robot the fruit;
						next = temp_edge.getDest();
					}
					if(r.getSrc()==temp_edge.getSrc()) {
						fruit.setTag(1); // fruit has been visited
						return temp_edge.getDest();
					}
					if(r.getSrc()==temp_edge.getDest()) {
						fruit.setTag(1); //fruit has been visited
						return temp_edge.getSrc();
					}
					if (distFromMe < min) {
						min = distFromMe;
						decision = next; //sets where to go
					}

				} 
				else {
					
					if (temp_edge.getDest() < temp_edge.getSrc()) {
						distFromMe = ga.shortestPathDist(r.getSrc(), temp_edge.getDest()); //return shortest path between robot and fruit
						next = temp_edge.getDest();
						
					} else if (temp_edge.getSrc() < temp_edge.getDest()) {
						distFromMe = ga.shortestPathDist(r.getSrc(), temp_edge.getSrc()); //return shortest path between robot and fruit
						next = temp_edge.getSrc();
					}
					if(r.getSrc()==temp_edge.getSrc()) {
						fruit.setTag(1); //fruit has been visited
						return temp_edge.getDest();
					}
					if(r.getSrc()==temp_edge.getDest()) {
						fruit.setTag(1); // fruit has been visited
						return temp_edge.getSrc();
					}
					if (distFromMe < min) {
						min = distFromMe;
						decision = next; // sets where to go
					}
				}
			}
		}
		ArrayList<node_data> array = new ArrayList<node_data>(); 
		array.addAll(ga.shortestPath(r.getSrc(), decision));
		for (Fruit fruit: fru_list) {
			temp_edge = fruitToEdge(fruit,g); // returns the edge that the fruit is sitting on

			if(temp_edge.getDest()==decision || temp_edge.getSrc()==decision){
				fruit.setTag(1); // fruit has been visited.
				break;
			}
		}

		return array.get(1).getKey();

		

	    }

	}



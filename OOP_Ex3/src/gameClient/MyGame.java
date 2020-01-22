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
	private static KML_Logger kml=new KML_Logger();
	private int moves=0;

	public String getScore() {
		return score;
	}

	public int get_level() { return this.level; }

	public int getMoves() {return this.moves;}


	public static void main(String[] args) {
		int level = getLevel();
		int mode = getMode();

		game_service game = Game_Server.getServer(level); // this is where we get the user input too know what game to play [0,23];
		String g = game.getGraph(); // graph as string.

		DGraph gg = new DGraph();
		game.addRobot(10);
		game.addRobot(0);
		game.addRobot(0);

		gg.init(g);
		//we have the graph. now we need to get the robots and fruits.
		//after getting the fruits and robots, we need to update our graph with the location of fruits and robots.
		//after that, we need to update our GUI with new parameters and present it.

		MyGame mg = new MyGame(gg,game,level);
		mg.goGo(mode);

	

	}
	private static int getMode() {
		ImageIcon robo = new ImageIcon("robotB.png");
		String[] Mode = {"Automate", "Manual"};
		int ModeNum = JOptionPane.showOptionDialog(null, "Choose the Mode you would like to display", "Click a button",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, Mode, Mode[0]);
		if (ModeNum<0) ModeNum=0;//in case user don't pick and press x
		return ModeNum;
	}
	private static int getLevel() {
		// Logo for options-dialog
		ImageIcon robo = new ImageIcon("robotB.png");

		// Set the game Level - [0,23]
		String[] options = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
		int gameNum = JOptionPane.showOptionDialog(null, "Choose the Level you would like to display", "Click a button",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, options, options[0]);
		if (gameNum<0) gameNum=0;//in case user don't pick and press x
		return gameNum;

	}

	public MyGame(graph g,game_service game, int level) {
		this.graph=g;
		this.game=game;
		this.level=level;
		fetchRobots();
		fetchFruits();
	}
	public MyGame() {

	}


	public void goGo(int mode) {
		
		MyGameGUI r = new MyGameGUI((DGraph)this.graph,game,7,this);
		r.setVisible(true);

		game.startGame();

		while(game.isRunning()) {
			this.upDate(mode);
			r.run();
			update();
			try {
				kml.makeKML(this,0);
			} catch (ParseException | InterruptedException e){ e.printStackTrace(); }
			

		}



		JOptionPane.showMessageDialog(null, ("           Your Score is: "+Score(this.robo_list)));


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
			} catch (JSONException e) { e.printStackTrace(); }	
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
			} catch (JSONException e) { e.printStackTrace(); }	
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

	private node_data decide(graph g, int src, Robot r) {
		Collection<edge_data> ed = g.getE(r.getSrc()); 
		ArrayList<edge_data> edal = new ArrayList<edge_data>(); //arraylist of edges coming out of robot src
		edal.addAll(ed);
		Fruit f = topFruit(this.fru_list); //f is best fruit
		
		Graph_Algo ga = new Graph_Algo(g);
		ArrayList<node_data> nd = new ArrayList<node_data>(); 
		nd.addAll(ga.shortestPath(src, f.from)); // shortest path to the fruit.
		nd.add(g.getNode(f.to));
		for(edge_data t:edal) {
		node_data x = g.getNode(t.getDest());
		if(nd.contains(x)) return x;
		}
		return null;
		
	}

	public List<node_data> go(graph g,int src,Robot r) {
		Graph_Algo ga = new Graph_Algo(g);
		if(this.fru_list.size()==1) {
			Fruit f = this.fru_list.get(0);
			List<node_data>arr = ga.shortestPath(src, f.from);
			arr.add(g.getNode(0));
			arr.addAll(ga.shortestPath(0, f.to));
			return arr;
			
		}
		Fruit f = closeFru(r);
		Fruit f2=null;
		ArrayList<Fruit> tempfru = this.fru_list;
//		if(tempfru.size()>1) {
//			tempfru.remove(f);
//			f2 = closeFru(r);
//		}
		
		f= setFnT(f, g);

		List<node_data>arr = ga.shortestPath(src, f.from);
		arr.add(g.getNode(f.to));
//		if(f2!=null) {
//			arr.addAll(ga.shortestPath(f.to, f2.to));
//		}


		return arr;

	}

	/**
	 * calculates where is the best point to start the game from.
	 * @return the node its best to start in
	 */

	public int startHere() {

		int k = graph.nodeSize();
		return (int)(Math.random()*k+1);
	}

	public Fruit topFruit(ArrayList<Fruit> fru_list) {
		Fruit fru = new Fruit();
		double temp=0;
		for(Fruit f:fru_list) {
			if(f.getValue()>temp) {
				temp = f.getValue();

			}
			fru = f;

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
				game.chooseNextEdge(r.getID(), dest);
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
	public Fruit setFnT(Fruit f,graph g) {
		Fruit ans = new Fruit();
		ans.setType(f.getType());
		ans.setPos(f.getPos());
		Point3D f_p = f.getPos();
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
			if(r.getSrc()==f.from && r.getDest()==f.to) return true;
		}
		return false;
	}

	/**
	 * updates the robot list constantly so the robot location and score would update
	 * while the game is running.
	 * fetches the data from the server and updates the robo list.
	 */	
	public ArrayList<Robot> upDate(int mode) {

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
					int nodetoGO = getNextNode(r, graph, fru_list);
					List<node_data> temp2 = go(graph,r.getSrc(),r);
					node_data temp3 = decide(graph,r.getSrc(),r);
					game.chooseNextEdge(r.getID(), nodetoGO);
//					for(node_data nd:temp2) {				
//						r.setDest(nd.getKey());
//						
//						game.chooseNextEdge(r.getID(),r.getDest());			
//					}
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
			Thread.sleep(sleepTime(graph, fru_list, robo_list));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			} catch (JSONException e) { e.printStackTrace(); }
		}
	}
	
	public Fruit closeFru(Robot r) {
		double distance=0;
		Fruit f= null;
		for(Fruit temp:this.fru_list) {
			if(temp.getPos().distance3D(r.getPos())<distance)
				distance = temp.getPos().distance3D(r.getPos());
				f=temp;
		}
		return f;
	}
	 public int getNextNode(Robot r , graph g, List<Fruit> arr ) {
	        Graph_Algo p = new Graph_Algo(g);
	        edge_data temp = null;
	        double min = Integer.MAX_VALUE;
	        double disFromRob = 0;
	        int whereTo=-1;
	        int finalWhereTo =-1;
	        for (Fruit fruit: arr) {
	            if (fruit.getTag() == 0) {
	                temp = fruitToEdge(fruit,g);
	            	//temp = fruit.getFruitEdge(g, fruit);
	                if (fruit.getType() == -1) {
	                    if (temp.getDest() > temp.getSrc()) {
	                        disFromRob = p.shortestPathDist(r.getSrc(), temp.getDest());
	                        whereTo = temp.getSrc();
	                    } else if (temp.getSrc() > temp.getDest()) {
	                        disFromRob = p.shortestPathDist(r.getSrc(), temp.getSrc());
	                        whereTo = temp.getDest();
	                    }
	                    if(r.getSrc()==temp.getSrc()) {
	                        fruit.setTag(1);
	                        return temp.getDest();
	                    }
	                    if(r.getSrc()==temp.getDest()) {
	                        fruit.setTag(1);
	                        return temp.getSrc();
	                    }
	                    if (disFromRob < min) {
	                        min = disFromRob;
	                        finalWhereTo = whereTo;
	                    }

	                } else if (fruit.getType() == 1) {
	                    if (temp.getDest() < temp.getSrc()) {
	                        disFromRob = p.shortestPathDist(r.getSrc(), temp.getDest());
	                        whereTo = temp.getDest();
	                    } else if (temp.getSrc() < temp.getDest()) {
	                        disFromRob = p.shortestPathDist(r.getSrc(), temp.getSrc());
	                        whereTo = temp.getSrc();
	                    }
	                    if(r.getSrc()==temp.getSrc()) {
	                        fruit.setTag(1);
	                        return temp.getDest();
	                    }
	                    if(r.getSrc()==temp.getDest()) {
	                        fruit.setTag(1);
	                        return temp.getSrc();
	                    }
	                    if (disFromRob < min) {
	                        min = disFromRob;
	                        finalWhereTo = whereTo;
	                    }

	                }

	            }

	        }
	        System.out.println("im here");
	        List<node_data> ans = p.shortestPath(r.getSrc(), finalWhereTo);
	        for (Fruit fruit: arr) {
	        	temp = fruitToEdge(fruit,g);
	            //temp = fruit.getFruitEdge(g,fruit);
	            if(temp.getDest()==finalWhereTo || temp.getSrc()==finalWhereTo){
	                fruit.setTag(1);
	                break;
	            }
	        }
	        if (ans.size() == 1) {
	            List<node_data> ans2 = p.shortestPath(r.getSrc(), (finalWhereTo + 15) % 11);
	            System.out.println("im here 2");
	            return ans2.get(1).getKey();
	        }
	        return ans.get(1).getKey();


	    }
	  private int sleepTime(graph g,ArrayList<Fruit> arrF,ArrayList<Robot> arrR){
	        int ans =100;
	        for (Robot rob: arrR) {
	            for (Fruit fruit: arrF) {
	                edge_data temp = fruitToEdge(fruit,g);
	                if(temp.getSrc()==rob.getSrc() || temp.getDest()==rob.getSrc()){
	                    System.out.println("im at sleep");
	                    return 50;
	                }
	            }
	        }
	        return ans;
	    }
  
}

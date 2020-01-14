package gameClient;

import Server.Game_Server;
import Server.game_service;
import dataStructure.*;
import gui.*;
import utils.Point3D;
/**
 * this is the GUI class, based on the gui yossi made.
 * yossi do your magic.
 * @author Eldar
 *
 */
public class MyGameGUI {
	
	/**
	 * 
	 * @param data denote some data to be scaled
	 * @param r_min the minimum of the range of your data
	 * @param r_max the maximum of the range of your data
	 * @param t_min the minimum of the range of your desired target scaling
	 * @param t_max the maximum of the range of your desired target scaling
	 * @return
	 */
	private double scale(double data, double r_min, double r_max, double t_min, double t_max)
	{
		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}

	public static void main(String[] args) {
		
		/*game_service game = Game_Server.getServer(4); // this is where we get the user input too know what game to play [0,23];
		String g = game.getGraph(); // graph as string.
		DGraph gg = new DGraph();
		game.addRobot(1);
		gg.init(g);*/
		DGraph gg = new DGraph();
		gg.addNode(new node(new Point3D(300,300)));
		
		GraphGui a = new GraphGui(gg);
		a.setVisible(true);
		
		//MyGame

	}

}

package gui;

import java.util.HashMap;

import Server.Game_Server;
import Server.game_service;
import dataStructure.*;
import utils.Point3D;

public class Gui_Main {
	
	/**
	 * @param data - denote some data to be scaled
	 * @param r_min the minimum of the range of your data
	 * @param r_max the maximum of the range of your data
	 * @param t_min the minimum of the range of your desired target scaling
	 * @param t_max the maximum of the range of your desired target scaling
	 * @return
	 */
	private static double scale(double data, double r_min, double r_max, double t_min, double t_max)
	{
		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}
	
	private static double[] scaleHelper(HashMap<Integer, node_data> n) {
		double [] ans = {Double.MAX_VALUE, Double.MIN_VALUE ,Double.MAX_VALUE ,Double.MIN_VALUE};
		n.forEach((k, v) -> {
			if (v.getLocation().x()<ans[0]) ans[0] = v.getLocation().x();
			if (v.getLocation().x()>ans[1]) ans[1] = v.getLocation().x();
			if (v.getLocation().y()<ans[2]) ans[2] = v.getLocation().y();
			if (v.getLocation().y()>ans[3]) ans[3] = v.getLocation().y();
		});
		
		return ans;
	}

	public static void main(String[] args) {
		
		game_service game = Game_Server.getServer(4); // this is where we get the user input too know what game to play [0,23];
		String str = game.getGraph(); // graph as string.
		DGraph g = new DGraph();
		game.addRobot(1);
		g.init(str);
		
		double [] size = scaleHelper(g.nodesMap);
		
		g.nodesMap.forEach((k, v) -> {
			Point3D loc = v.getLocation();
			Point3D newL = new Point3D((int)scale(loc.x(),size[0],size[1],50,1230), (int)scale(loc.y(),size[2],size[3],80,670));
			v.setLocation(newL);
		});

		GraphGui a = new GraphGui(g);
		a.setVisible(true);

	}
}


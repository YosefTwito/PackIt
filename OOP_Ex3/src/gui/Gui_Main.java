package gui;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Server.Game_Server;
import Server.game_service;
import dataStructure.*;
import gameClient.Fruit;
import gameClient.MyGame;
import gameClient.Robot;
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

		JFrame chooseGameNum = new JFrame();
		boolean WeCanStart = true;
		int gameNum = -1;
		try { // this is where we get the user input too know what game to play [0,23]
			while (gameNum < 0 || gameNum >23) {
				String howMany = JOptionPane.showInputDialog(chooseGameNum,"Hello, please choose Map out of our 24 Options \n                  Enter a number between 0-23 :");
				gameNum = Integer.parseInt(howMany);
				if (gameNum <= 23 && gameNum >=0) break;
				JOptionPane.showMessageDialog(chooseGameNum, "You've entered illegal Map number");
			}

		} catch (Exception e) {
			WeCanStart=false;
			JOptionPane.showMessageDialog(chooseGameNum, "Error - You did not enter a number");
		}
		
		game_service game = Game_Server.getServer(gameNum);
		game.addRobot(0); game.addRobot(1); game.addRobot(2); game.addRobot(3); game.addRobot(4);
		String str = game.getGraph(); // graph as string.
		DGraph g = new DGraph();
		
		//init the graph from json for the game.
		g.init(str);

		//add objects parameters to GraphGui:
		MyGame mg = new MyGame(g, game);
		//get fruits.
		ArrayList<Fruit> fr = mg.fru_list;
		if (fr != null) {
			for (int i=0; i<fr.size(); i++) {
				fr.get(i).from = mg.fruitToEdge(fr.get(i), g).getSrc();
				fr.get(i).to = mg.fruitToEdge(fr.get(i), g).getDest();
			}
		}
		//get robots.
		ArrayList<Robot> rob = mg.robo_list;
		
		//relocate nodes to valid coordination.
		double [] size = scaleHelper(g.nodesMap);
		g.nodesMap.forEach((k, v) -> {
			Point3D loc = v.getLocation();
			Point3D newL = new Point3D((int)scale(loc.x(),size[0],size[1],50,1230), (int)scale(loc.y(),size[2],size[3],80,670));
			v.setLocation(newL);
		});

		//Init gui
		GraphGui a = new GraphGui(g, fr, size ,mg);
		//Let the Show Begin !
		a.setVisible(true);
		
		while(mg.game.isRunning()) {
			try {
				Thread.sleep(100);
				a.mg.upDate();
				System.out.println(mg.Score(rob));
				a.repaint();			
			} catch (InterruptedException e) {e.printStackTrace();}
			//System.out.println(mg.game.timeToEnd());
		}
		JFrame showScore = new JFrame();
		JOptionPane.showMessageDialog(showScore, "Your score is: ***gotta add****");
	}
}


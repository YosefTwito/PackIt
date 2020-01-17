package gameClient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Server.Game_Server;
import Server.game_service;
import dataStructure.*;
import gameClient.Fruit;
import gameClient.MyGame;
import gameClient.Robot;
import gui.GraphGui;
import utils.Point3D;

public class MyGameGUI extends JFrame{
	
	MyGame mg;
	double [] size;

	public MyGameGUI(int Mode, int Level, DGraph g, MyGame mg) {
		this.mg=mg;
		this.size=scaleHelper(g.nodesMap);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(false);
		this.setSize(1280, 720);
		this.setResizable(true);
		this.setTitle("Hello and welcome to PackIt !");
		
		ImageIcon img = new ImageIcon("Rocket.png");
		this.setIconImage(img.getImage());
		
		MenuBar menuBar = new MenuBar();
		this.setMenuBar(menuBar);
		
		Menu file = new Menu("File ");
		menuBar.add(file);
		
		this.setVisible(true);
		
		this.createBufferStrategy(2);

		//relocate nodes to valid coordination.
		
		g.nodesMap.forEach((k, v) -> {
			Point3D loc = v.getLocation();
			Point3D newL = new Point3D((int)scale(loc.x(),size[0],size[1],50,1230), (int)scale(loc.y(),size[2],size[3],80,670));
			v.setLocation(newL);
		});

		gameLoop(Mode, Level, g); }

	private void gameLoop(int Mode, int Level, DGraph g) { 
		
		// Your game logic goes here.

		paint(Level, g); 
		}

	private void paint(int Level, DGraph g) { // Code for the drawing goes here. }
		BufferStrategy bf = this.getBufferStrategy();
		Graphics d = null;

		try { 
			d = bf.getDrawGraphics();
			if (g != null && g.nodeSize()>=1) {
				//get nodes
				Collection<node_data> nodes = g.getV();

				for (node_data n : nodes) {
					//draw nodes
					Point3D p = n.getLocation();
					d.setColor(Color.BLACK);
					d.fillOval(p.ix(), p.iy(), 11, 11);

					//draw nodes-key's
					d.setColor(Color.BLUE);
					d.drawString(""+n.getKey(), p.ix()-4, p.iy()-5);

					//check if there are edges
					if (g.edgeSize()==0) { continue; }
					if ((g.getE(n.getKey())!=null)) {
						//get edges
						Collection<edge_data> edges = g.getE(n.getKey());
						for (edge_data e : edges) {
							//draw edges
							d.setColor(Color.GREEN);
							((Graphics2D) d).setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
							Point3D p2 = g.getNode(e.getDest()).getLocation();
							d.drawLine(p.ix()+5, p.iy()+5, p2.ix()+5, p2.iy()+5);
						}	
					}
				}
			}
			//draw robots
			if (mg.robo_list !=null) {
				//get icon
				ImageIcon robocop = new ImageIcon("robot.png");
				if (mg.robo_list.size()>0) {
					for (int i=0; i< mg.robo_list.size(); i++) {
						//reposition to robots
						Point3D pos = new Point3D((int)scale(mg.robo_list.get(i).getPos().x(),this.size[0],this.size[1],50,1230), (int)scale(mg.robo_list.get(i).getPos().y(),this.size[2],this.size[3],80,670));
						//draw
						d.drawImage(robocop.getImage(), pos.ix()-10, pos.iy()-13, pos.ix()+10, pos.iy()+13, 0, 0, 500, 500, null);

					}
				}
			}	
			bf.show();
			Graphics e = null;
			
			paintComponent(e);
		}
		finally {d.dispose(); }
	}


	void paintComponent(Graphics d){
		
	}
	
	
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

		ImageIcon robo = new ImageIcon("robotB.png");
		// Set the game Level - [0,23]
		String[] options = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
		int gameNum = JOptionPane.showOptionDialog(null, "Choose the Level you would like to display", "Click a button",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, options, options[0]);
		if (gameNum<0) gameNum=0;
		System.out.println(gameNum);
		// Set the game mode - Manual/Automate
		String[] Mode = {"Automate", "Manual"};
		int ModeNum = JOptionPane.showOptionDialog(null, "Choose the Mode you would like to display", "Click a button",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, Mode, Mode[0]);
		if (ModeNum<0) ModeNum=0;
		System.out.println(ModeNum);

		game_service game = Game_Server.getServer(gameNum);
		game.addRobot(0);game.addRobot(1);game.addRobot(2);game.addRobot(3);game.addRobot(4);
		String str = game.getGraph(); // graph as string.
		DGraph g = new DGraph();
		//initialize the graph from json for the game.
		g.init(str);

		MyGame mg = new MyGame(g, game);
			
		MyGameGUI t = new MyGameGUI(ModeNum, gameNum, g, mg);
		
		mg.game.startGame();

	}

}




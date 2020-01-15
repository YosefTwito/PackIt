package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import dataStructure.*;
import gameClient.*;
import utils.*;


/**
 * This class makes a gui window to represent a graph and
 * use the Algorithms from class Graph_Algo on live.
 * (use the methods and represent it on the gui window while it is still up).
 * @author YosefTwito and EldarTakach
 */
public class GraphGui extends JFrame implements ActionListener, GraphListener{

	private static final long serialVersionUID = 1L;
	MyGame mg;
	graph gr;
	graph original;
	ArrayList<Robot> robots;
	ArrayList<Fruit> fruits;
	double [] exPos;

	public GraphGui(DGraph g){
		g.addListener(this);
		this.gr=g;
		this.original=g;
		initGUI(g);
	}

	/**
	 * @param g - the graph we work on
	 * @param src - the source node of the robot
	 * @return the next node the robot should ahead to
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

	/**
	 * Constructor
	 * @param g - graph to paint
	 * @param fruits
	 * @param robots
	 * @param size
	 * @param game
	 */
	public GraphGui(DGraph g, ArrayList<Fruit> fruits, ArrayList<Robot> robots, double [] size,MyGame game){
		g.addListener(this);
		this.gr=g;
		this.original=g;
		this.fruits=fruits;
		this.robots=robots;
		this.exPos = size;
		this.mg=game;
		initGUI(g);
		mg.game.startGame();
	}

	/**
	 * this method scales the position from Google-Earth coordinates to a valid position on our Jframe
	 * @param data - the position you want to scale
	 * @param r_min the minimum of the range of all your data
	 * @param r_max the maximum of the range of all your data
	 * @param t_min the minimum of the range of your desired target scaling
	 * @param t_max the maximum of the range of your desired target scaling
	 * @return valid location 
	 */
	private static double scale(double data, double r_min, double r_max, double t_min, double t_max)
	{
		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}
	
	public void paint(Graphics d) {
		super.paint(d);
		
		if (gr != null && gr.nodeSize()>=1) {
			//get nodes
			Collection<node_data> nodes = gr.getV();

			for (node_data n : nodes) {
				//draw nodes
				Point3D p = n.getLocation();
				d.setColor(Color.BLACK);
				d.fillOval(p.ix(), p.iy(), 11, 11);

				//draw nodes-key's
				d.setColor(Color.BLUE);
				d.drawString(""+n.getKey(), p.ix()-4, p.iy()-5);

				//check if there are edges
				if (gr.edgeSize()==0) { continue; }
				if ((gr.getE(n.getKey())!=null)) {
					//get edges
					Collection<edge_data> edges = gr.getE(n.getKey());
					for (edge_data e : edges) {
						//draw edges
						d.setColor(Color.GREEN);
						((Graphics2D) d).setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
						Point3D p2 = gr.getNode(e.getDest()).getLocation();
						d.drawLine(p.ix()+5, p.iy()+5, p2.ix()+5, p2.iy()+5);

					}	

					//draw fruits
					if (this.fruits != null) {
						if (this.fruits.size()>0) {
							//get icons
							ImageIcon apple = new ImageIcon("ap2.jpg");
							ImageIcon banana = new ImageIcon("pin2.jpg");
							//draw
							int srcF, destF;
							Point3D tempS, tempD;
							for (int i=0; i<this.fruits.size(); i++) {
								srcF = this.fruits.get(i).from;
								destF = this.fruits.get(i).to;
								//draw apple
								if (this.fruits.get(i).getType()==2) {
									tempS = this.gr.getNode(srcF).getLocation();
									tempD = this.gr.getNode(destF).getLocation();
									if (srcF < destF) {
										d.drawImage(apple.getImage(), (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))-5, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))-10, (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))+15, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))+10, 0, 0, 413, 472, null);
									}
									else {
										d.drawImage(apple.getImage(), (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))-5, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))-10, (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))+15, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))+10, 0, 0, 413, 472, null);
									}
								}
								//draw banana(visually pineapple, more convenient)
								else {
									tempS = this.gr.getNode(srcF).getLocation();
									tempD = this.gr.getNode(destF).getLocation();
									if (srcF > destF) {
										d.drawImage(banana.getImage(), (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))-5, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))-10, (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))+15, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))+10, 0, 0, 413, 472, null);
									}
									else {
										d.drawImage(banana.getImage(), (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))-5, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))-10, (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))+15, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))+10, 0, 0, 413, 472, null);
									}								}
							}
						}
					}
					//draw robots
					if (this.robots !=null) {
						//get icon
						ImageIcon robocop = new ImageIcon("robo.jpg");
						if (this.robots.size()>0) {
							for (int i=0; i< robots.size(); i++) {
								//reposition to robots
								Point3D pos = new Point3D((int)scale(robots.get(i).getPos().x(),this.exPos[0],this.exPos[1],50,1230), (int)scale(robots.get(i).getPos().y(),this.exPos[2],this.exPos[3],80,670));
								//draw
								d.drawImage(robocop.getImage(), pos.ix()-10, pos.iy()-13, pos.ix()+10, pos.iy()+13, 0, 0, 345, 482, null);
							}
						}
					}
					//mg.RoboLoc(); // UPDATE ROBO LOC

					/*
						//draw direction
						d.setColor(Color.MAGENTA);
						d.fillOval((int)((p.ix()*0.7)+(0.3*p2.ix()))+2, (int)((p.iy()*0.7)+(0.3*p2.iy())), 9, 9);
						//draw weight

						String sss = ""+String.format("%.3f",e.getWeight());
						d.drawString(sss, 1+(int)((p.ix()*0.7)+(0.3*p2.ix())), (int)((p.iy()*0.7)+(0.3*p2.iy()))-2);
					 */				
				}	
			}
		}
		
	}

	private void initGUI(graph g) {
		this.gr=g;
		this.setSize(1280, 720);
		this.setTitle(" Welcome to PackIt !");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		
		ImageIcon img = new ImageIcon("Rocket.png");
		this.setIconImage(img.getImage());
		
		//add robot button
		JButton b=new JButton( new ImageIcon("roboBut.jpg"));    
		b.setBounds(0,0,35, 47);
		this.add(b);
		this.setLayout(null);
		
		MenuBar menuBar = new MenuBar();
		this.setMenuBar(menuBar);

		Menu Mode  = new Menu(" Change Mode ");
		menuBar.add(Mode);

		MenuItem item1 = new MenuItem("Manual");
		item1.addActionListener(this);
		Mode.add(item1);
		
		MenuItem item2 = new MenuItem("Automate");
		item2.addActionListener(this);
		Mode.add(item2);

	}

	/**
	 * this method listen to the menu bar and do the commands requested by the user
	 */
	@Override
	public void actionPerformed(ActionEvent Command) {
		String str = Command.getActionCommand();		

		switch(str) {
		
		//Change game mode to Manual
		case "Manual":
			
			break;
			
		//Change game mode to Automate	
		case "Automate":
			
			break;
		}
	}
	
	@Override
	public void graphUpdater() {	
		repaint();	
	}
}

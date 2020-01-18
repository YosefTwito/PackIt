package gameClient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;

public class BasicDoubleBufferSwing {

	public static class Canvas extends JPanel {

		DGraph gr;
		MyGame game;
		double[] size;
		game_service cheat;
		boolean on = true;

		private void start_game() {
			// Logo for options-dialog
			ImageIcon robo = new ImageIcon("robotB.png");

			// Set the game Level - [0,23]
			String[] options = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
			int gameNum = JOptionPane.showOptionDialog(null, "Choose the Level you would like to display", "Click a button",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, options, options[0]);
			if (gameNum<0) gameNum=0;

			// Set the game mode - Manual/Automate
			String[] Mode = {"Automate", "Manual"};
			int ModeNum = JOptionPane.showOptionDialog(null, "Choose the Mode you would like to display", "Click a button",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, Mode, Mode[0]);
			if (ModeNum<0) ModeNum=0;

			//Creating new game server
			game_service game = Game_Server.getServer(gameNum);
			this.cheat = game;
			//Adding robots.
			game.addRobot(0);game.addRobot(1);game.addRobot(2);game.addRobot(3);game.addRobot(4);
			String str = game.getGraph(); // graph as string.
			DGraph gr = new DGraph();
			//initialize the graph from json for the game.
			gr.init(str);

			//relocate nodes to valid coordination.
			double [] size = scaleHelper(gr.nodesMap);
			this.size=size;
			gr.nodesMap.forEach((k, v) -> {
				Point3D loc = v.getLocation();
				Point3D newL = new Point3D((int)scale(loc.x(),size[0],size[1],50,1230), (int)scale(loc.y(),size[2],size[3],70,660));
				v.setLocation(newL);
			});
			this.gr=gr;

			MyGame mg = new MyGame(gr, game);
			this.game=mg;
			//Let the Show begin
			mg.game.startGame();
		}

		private static final long serialVersionUID = 1L;

		private Image offScreenImage = null;
		private Graphics offScreenGraphics = null;
		private Image offScreenImageDrawed = null;
		private Graphics offScreenGraphicsDrawed = null;              
		private Timer timer = new Timer();

		public Canvas() {
			start_game();  
			timer.schedule(new AutomataTask(), 0, 40);
			this.setPreferredSize(new Dimension(1280, 720));               
		}

		/** 
		 * Use double buffering.
		 * @see java.awt.Component#update(java.awt.Graphics)
		 */
		@Override
		public void update(Graphics g) {                                
			paint(g);
		}

		/**
		 * Draw this generation.
		 * @see java.awt.Component#paint(java.awt.Graphics)
		 */
		@Override
		public void paint(final Graphics g) {

			final Dimension d = getSize();
			// Double-buffer: clear the offscreen image.  
			offScreenImageDrawed=null;
			if (offScreenImageDrawed == null) {offScreenImageDrawed = createImage(d.width, d.height);}          
			offScreenGraphicsDrawed = offScreenImageDrawed.getGraphics();                              
			/////////////////////
			// Paint Offscreen //
			/////////////////////
			renderOffScreen(offScreenImageDrawed.getGraphics());
			g.drawImage(offScreenImageDrawed, 0, 0, null);
		}

		public void renderOffScreen(final Graphics g) {

			if (gr != null && gr.nodeSize()>=1) {
				//get nodes
				Collection<node_data> nodes = gr.getV();
				for (node_data n : nodes) {
					//draw nodes
					Point3D p = n.getLocation();
					g.setColor(Color.BLACK);
					g.fillOval(p.ix(), p.iy(), 9, 9);

					//draw nodes-key's
					g.setColor(Color.BLUE);
					g.drawString(""+n.getKey(), p.ix()-4, p.iy()-5);

					//check if there are edges
					if (gr.edgeSize()==0) { continue; }
					if ((gr.getE(n.getKey())!=null)) {
						//get edges
						Collection<edge_data> edges = gr.getE(n.getKey());
						for (edge_data e : edges) {
							//draw edges
							g.setColor(Color.GREEN);
							((Graphics2D) g).setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
							Point3D p2 = gr.getNode(e.getDest()).getLocation();
							g.drawLine(p.ix()+5, p.iy()+5, p2.ix()+5, p2.iy()+5);
						}	
					}
				}
			}
			if (game.fru_list != null) {

				if (game.fru_list.size()>0) {
					//get icons
					ImageIcon apple = new ImageIcon("apple.png");
					ImageIcon banana = new ImageIcon("banana.png");
					//draw
					int srcF, destF;
					Point3D tempS, tempD;
					for (int i=0; i<game.fru_list.size(); i++) {
						srcF = game.fru_list.get(i).from;
						destF = game.fru_list.get(i).to;
						if (game.fru_list.get(i).getType()==-1) {
							tempS = this.gr.getNode(srcF).getLocation();
							tempD = this.gr.getNode(destF).getLocation();
							g.drawImage(apple.getImage(), (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))-5, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))-10, (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))+15, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))+10, 0, 0, 500, 500, null);
						}
						else {
							tempS = this.gr.getNode(srcF).getLocation();
							tempD = this.gr.getNode(destF).getLocation();
							g.drawImage(banana.getImage(), (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))-5, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))-10, (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))+15, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))+10, 0, 0, 532, 470, null);
						}
					}
				}
			}
			//draw robots
			if (game.robo_list !=null) {
				//get icon
				ImageIcon robocop = new ImageIcon("robot.png");
				if (game.robo_list.size()>0) {
					for (int i=0; i< game.robo_list.size(); i++) {
						//reposition to robots
						Point3D pos = new Point3D((int)scale(game.robo_list.get(i).getPos().x(),this.size[0],this.size[1],50,1230), (int)scale(game.robo_list.get(i).getPos().y(),this.size[2],this.size[3],80,670));
						//draw
						g.drawImage(robocop.getImage(), pos.ix()-7, pos.iy()-18, pos.ix()+13, pos.iy()+8, 0, 0, 500, 500, null);

					}
				}
			}
		}


		private class AutomataTask extends java.util.TimerTask {
			public void run() {
				// Run thread on event dispatching thread
				if (game.game.isRunning()) {
					if (!EventQueue.isDispatchThread()) {
						EventQueue.invokeLater(this);
					} else {
						if (Canvas.this != null) {
							game.upDate();               	
							Canvas.this.repaint();                        
						}
					}     
				}
				else {
					JOptionPane.showMessageDialog(null, "                  Game Over ! \n           Your Score is: "+game.score);
					this.cancel();
				}
			}
		}        
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
	/**
	 * Finds the minimum and maximum x and y positions in the nodesMap to help relocating them
	 * from google-earth coordinates to the gui window coordinates
	 * @param n
	 * @return - an array with the min and max values - [minX, minY, maxX, maxY]
	 */
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

	public static void main(final String [] args) {

		JFrame frame = new JFrame("Hello and welcome to PackIt !") {
			private static final long serialVersionUID = 1L;
			public void processWindowEvent(java.awt.event.WindowEvent e) {
				super.processWindowEvent(e);
				if (e.getID() == java.awt.event.WindowEvent.WINDOW_CLOSING) {
					System.exit(-1);
				}
			}
		};
		frame.setPreferredSize(new Dimension(1280, 720));
		frame.add(new Canvas());
		frame.pack();
		frame.setIconImage(new ImageIcon("Rocket.png").getImage());
		frame.setVisible(true); 
	}

}
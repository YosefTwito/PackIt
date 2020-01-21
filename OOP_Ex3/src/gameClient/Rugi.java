package gameClient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import dataStructure.*;

public class Rugi extends JFrame implements ActionListener, MouseListener, Runnable, Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Graphics G = null;
	private static JFrame frame;
	double pathW =-1;
	List<node_data> path;
	int [][] robotD;
	Graphics doubleB;
	int Level;
	Graphics2D g2d;

	ImageIcon robo = new ImageIcon("robotB.png");
	ImageIcon robotI = new ImageIcon("robot.png");
	ImageIcon bananI = new ImageIcon("banana.png");
	ImageIcon appleI = new ImageIcon("apple.png");
	ImageIcon Rocket = new ImageIcon("Rocket.png");

	double ex[];

	int action=0;

	Collection <node_data> nodes;
	Collection <edge_data> edges;

	graph graph;
	game_service game;
	MyGame myGame;

	public Rugi() {;}

	public Rugi(graph graph) {
		InitGui();
		this.nodes=graph.getV();
		this.graph=graph;
		((Observable)graph).addObserver(this);
	}

	public Rugi(graph graph, game_service game, int Level, MyGame mg) {
		InitGui();
		this.nodes=graph.getV();
		this.graph=graph;
		this.myGame=mg;
		((Observable)graph).addObserver(this);
		this.game=game;
		this.Level=Level;
	}

	private void InitGui() {
		this.setSize(1280, 720);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(Rocket.getImage());
		this.setTitle("Welcome to Pack-It !");

		MenuBar menu = new MenuBar();

		Menu file = new Menu("File");
		menu.add(file);
		
		MenuItem item1 = new MenuItem("Get Statistics");
		item1.addActionListener(this);
		file.add(item1);

		this.setMenuBar(menu);
		
		this.addMouseListener(this);

	}

	private BufferedImage buff;
	private Graphics2D g2;
	private BufferedImage buff3;

	JLabel background = null;

	public void paint(Graphics g) {

		if (buff == null || g2 == null || this.WIDTH != 1280 || this.HEIGHT != 720) {

			if ((this.WIDTH != 1280 || this.HEIGHT != 720) && background != null) {
				remove(background);
			}
			this.setLayout(null);
			//sets scales for gui window
			ex = scaleHelper(((DGraph)graph).nodesMap);

			buff = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
			g2 = buff.createGraphics();
			super.paint(g2);
			paintGraph(g2);
		}
		g2d = (Graphics2D)g;
		g2d.drawImage(buff, 0, 0, null);

		paintRobots();
		paintFruits();	

		if (game.timeToEnd()<1000) {
			g.drawString("Game Over !", 550, 100);
		}
	}

	public void paintGraph(Graphics g) {
		super.paintComponents(g);
		Graphics2D g2d = (Graphics2D)g;
		
		g.setColor(Color.BLACK);
		float fontMessage = 28.0f;
		g2d.setFont(g2d.getFont().deriveFont(fontMessage));
		g.drawString("Score: "+myGame.Score(myGame.robo_list), 150, 80);
		g.drawString("Time : "+game.timeToEnd()/1000, 350, 80);
		g.drawString("Level: "+this.Level, 570, 80);


		for (node_data n : nodes) {
			g2d.setStroke(new BasicStroke(2));
			float font = 14.0f;
			g2d.setFont(g2d.getFont().deriveFont(font));
			
			int x = (int)scale(n.getLocation().x(), ex[0], ex[1], 50, 1230);
			int y = (int)scale(n.getLocation().y(), ex[2], ex[3], 80, 670 );
			
			g.setColor(Color.BLACK);
			g.fillOval(x-7, y-7, 14, 14);
			g.setColor(Color.BLUE);
			g.drawString(""+n.getKey(), x-12, y-12);
			
			edges = graph.getE(n.getKey());
			if (edges == null) {continue;}

			for (edge_data e : edges) {
				
				int x2 = (int)scale(n.getLocation().x(), ex[0], ex[1], 50, 1230);
				int y2 = (int)scale(n.getLocation().y(), ex[2], ex[3], 80, 670 );
				
				int x3 = (int)scale(graph.getNode(e.getDest()).getLocation().x(), ex[0], ex[1], 50, 1230);
				int y3 = (int)scale(graph.getNode(e.getDest()).getLocation().y(), ex[2], ex[3], 80, 670 );
				
				g.setColor(Color.GREEN);
				g.drawLine(x2, y2, x3, y3);
			}
		}
	}

	public void paintFruits() {
		List<String> fruit = game.getFruits();
		
		for (int i=0; i<fruit.size(); i++) {
			try {
				JSONObject obj = new JSONObject(fruit.get(i));
				JSONObject fr = obj.getJSONObject("Fruit");
				double value = fr.getDouble("value");
				double type = fr.getDouble("type");
				String pos = fr.getString("pos");
				
				StringTokenizer st = new StringTokenizer(pos, ",");
				
				double x = Double.parseDouble(st.nextToken());
				double y = Double.parseDouble(st.nextToken());
				
				int xl = (int)scale(x, ex[0], ex[1], 50, 1230);
				int yl = (int)scale(y, ex[2], ex[3], 80, 670 );
				
				if (type == -1 ) { g2d.drawImage(appleI.getImage(), xl-12, yl-12, 25, 25, this); }
				else             { g2d.drawImage(bananI.getImage(), xl-12, yl-12, 25, 25, this); }
				
			}catch (Exception e) { e.printStackTrace(); }
		}
	}

	public void paintRobots() {
		List<String> Robot = game.getRobots();
		
		int score = 0;
		
		for (int i=0; i<Robot.size(); i++) {
			try {
				JSONObject obj = new JSONObject(Robot.get(i));
				JSONObject fr = obj.getJSONObject("Robot");
				
				int id = fr.getInt("id");
				int value = fr.getInt("value");
				String pos = fr.getString("pos");
				
				score+=value;
				
				
				StringTokenizer st = new StringTokenizer(pos, ",");
				
				double x = Double.parseDouble(st.nextToken());
				double y = Double.parseDouble(st.nextToken());
				
				int xl = (int)scale(x, ex[0], ex[1], 50, 1230);
				int yl = (int)scale(y, ex[2], ex[3], 80, 670 );
					
				g2d.drawImage(robotI.getImage(), xl-12, yl-12, 25, 25, this);
				g2d.setColor(Color.RED);
				float font = 21.0f;
				g2d.setFont(g2d.getFont().deriveFont(font));
				g2d.drawString(""+id, xl-4, yl-20);
				
			}catch (Exception e) { e.printStackTrace(); }
		}
		
		g2d.setColor(Color.MAGENTA);
		float fOver = 35.0f;
		g2d.setFont(g2d.getFont().deriveFont(fOver));
		g2d.drawString(""+score, 800, 28);
	}


	@Override
	public void run() {
		repaint();
	}
	@Override
	public void update(Observable o, Object arg) {
		repaint();
		run();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();		

		switch(event) {

		case "Get Statistics":
			break;
		}
	}



	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

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
}

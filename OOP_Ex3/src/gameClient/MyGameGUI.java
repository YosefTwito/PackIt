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
import java.awt.image.BufferedImage;
import java.text.ParseException;
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

import Server.game_service;
import dataStructure.*;

public class MyGameGUI extends JFrame implements ActionListener, Runnable, Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Get Icons for the game
	ImageIcon robo = new ImageIcon("robotB.png");
	ImageIcon robotI = new ImageIcon("robot.png");
	ImageIcon bananI = new ImageIcon("banana.png");
	ImageIcon appleI = new ImageIcon("apple.png");
	ImageIcon Rocket = new ImageIcon("Rocket.png");
	
	Graphics2D g2d;
	
	Collection <node_data> nodes;
	Collection <edge_data> edges;
	graph graph;
	game_service game;
	MyGame myGame;
	
	double GuiScales[]; // Minimum X, Maximum X, Minimum Y, Maximum Y
	int Level;
	
	// Constructors
	public MyGameGUI() {;}

	public MyGameGUI(graph graph, game_service game, int Level, MyGame mg) {
		InitGui();
		this.nodes=graph.getV();
		this.graph=graph;
		this.myGame=mg;
		((Observable)graph).addObserver(this);
		this.game=game;
		this.Level=Level;
	}

	// Initialize the Graphical Window for the Game
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
	
	}

	private BufferedImage buff;
	private Graphics2D g2;

	JLabel background = null;

	public void paint(Graphics g) {

		if (buff == null || g2 == null || this.WIDTH != 1280 || this.HEIGHT != 720) {

			if ((this.WIDTH != 1280 || this.HEIGHT != 720) && background != null) {
				remove(background);
			}
			
			this.setLayout(null);
			
			//sets scales for gui window
			GuiScales = scaleHelper(((DGraph)graph).nodesMap);

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
			float fontMessage = 48.0f;
			g.setFont(g.getFont().deriveFont(fontMessage));
			g.drawString("Game Over !", 550, 250);
		}
	}

	private void paintGraph(Graphics g) {
		super.paintComponents(g);
		Graphics2D g2d = (Graphics2D)g;
		
		g.setColor(Color.BLACK);
		float fontMessage = 28.0f;
		g2d.setFont(g2d.getFont().deriveFont(fontMessage));
		g.drawString("Score: "+myGame.Score(myGame.robo_list), 150, 80);
		g.drawString("Time : "+game.timeToEnd()/1000, 350, 80);
		g.drawString("Level: "+myGame.get_level(), 570, 80);
		g.drawString("Moves: "+myGame.getMoves(), 780, 80);

		//Get and paint nodes
		for (node_data n : nodes) {
			g2d.setStroke(new BasicStroke(2));
			float font = 14.0f;
			g2d.setFont(g2d.getFont().deriveFont(font));
			
			int scaledX = (int)scale(n.getLocation().x(), GuiScales[0], GuiScales[1], 50, 1230);
			int scaledY = (int)scale(n.getLocation().y(), GuiScales[2], GuiScales[3], 80, 670 );
			
			g.setColor(Color.BLACK);
			g.fillOval(scaledX-7, scaledY-7, 14, 14);
			g.setColor(Color.BLUE);
			g.drawString(""+n.getKey(), scaledX-12, scaledY-12);
			
			//Get and paint edges
			edges = graph.getE(n.getKey());
			if (edges == null) {continue;}

			for (edge_data e : edges) {
				
				int destX = (int)scale(graph.getNode(e.getDest()).getLocation().x(), GuiScales[0], GuiScales[1], 50, 1230);
				int destY = (int)scale(graph.getNode(e.getDest()).getLocation().y(), GuiScales[2], GuiScales[3], 80, 670 );
				
				g.setColor(Color.GREEN);
				g.drawLine(scaledX, scaledY, destX, destY);
			}
		}
	}

	private void paintFruits() {
		List<String> fruit = game.getFruits();
		
		for (int i=0; i<fruit.size(); i++) {
			//Initialize Fruit data from JSon and paint it
			try {
				JSONObject obj = new JSONObject(fruit.get(i));
				JSONObject fr = obj.getJSONObject("Fruit");
				double type = fr.getDouble("type");
				String pos = fr.getString("pos");
				
				StringTokenizer st = new StringTokenizer(pos, ",");
				
				double x = Double.parseDouble(st.nextToken());
				double y = Double.parseDouble(st.nextToken());
				
				int scaledX = (int)scale(x, GuiScales[0], GuiScales[1], 50, 1230);
				int scaledY = (int)scale(y, GuiScales[2], GuiScales[3], 80, 670 );
				
				if (type == -1 ) { g2d.drawImage(appleI.getImage(), scaledX-12, scaledY-12, 25, 25, this); }
				else             { g2d.drawImage(bananI.getImage(), scaledX-12, scaledY-12, 25, 25, this); }
				
			}catch (Exception e) { e.printStackTrace(); }
		}
	}

	private void paintRobots() {
		List<String> Robot = game.getRobots();
		
		for (int i=0; i<Robot.size(); i++) {
			//Initialize Robot data from JSon and paint it
			try {
				JSONObject obj = new JSONObject(Robot.get(i));
				JSONObject fr = obj.getJSONObject("Robot");
				int id = fr.getInt("id");
				String pos = fr.getString("pos");			
				
				StringTokenizer st = new StringTokenizer(pos, ",");
				
				double x = Double.parseDouble(st.nextToken());
				double y = Double.parseDouble(st.nextToken());
				
				int scaledX = (int)scale(x, GuiScales[0], GuiScales[1], 50, 1230);
				int scaledY = (int)scale(y, GuiScales[2], GuiScales[3], 80, 670 );
					
				g2d.drawImage(robotI.getImage(), scaledX-12, scaledY-12, 25, 25, this);
				g2d.setColor(Color.RED);
				float font = 21.0f;
				g2d.setFont(g2d.getFont().deriveFont(font));
				g2d.drawString(""+id, scaledX-4, scaledY-20);
				
			}catch (Exception e) { e.printStackTrace(); }
		}
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
	 * @return - an array with the min and max values - [minX, maxX, minY, maxY]
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

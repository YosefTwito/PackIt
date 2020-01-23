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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;


import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.json.JSONObject;

import Server.game_service;
import dataStructure.*;

public class MyGameGUI extends JFrame implements ActionListener, Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Data-Base Access
	public static final String jdbcUrl="jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser="student";
	public static final String jdbcUserPassword="OOP2020student";
	
	//My ID
	private final int myID=314732637;

	//Get Icons for the game
	ImageIcon robo = new ImageIcon("robotB.png");
	ImageIcon robotI = new ImageIcon("robot.png");
	ImageIcon bananI = new ImageIcon("banana.png");
	ImageIcon appleI = new ImageIcon("apple.png");
	ImageIcon Rocket = new ImageIcon("Rocket.png");

	//Get Icons for MenuBar
	ImageIcon baby = new ImageIcon("baby.jpg");
	ImageIcon lama = new ImageIcon("lama.jpeg");
	ImageIcon wasted = new ImageIcon("wasted.jpeg");

	Graphics2D g2d;

	Collection <node_data> nodes;
	Collection <edge_data> edges;
	graph graph;
	game_service game;
	MyGame myGame;

	double GuiScales[]; // Minimum X, Maximum X, Minimum Y, Maximum Y
	int Level;
	
	//For statistics
	int [] myPlaceInClass = new int [24];
	ArrayList<Integer> myScore = new ArrayList<Integer>();
	int currLevel=0;
	int gamesPlayed = 0;

	// Constructors
	public MyGameGUI() {;}

	public MyGameGUI(graph graph, game_service game, int Level, MyGame mg) {
		InitGui();
		this.nodes=graph.getV();
		this.graph=graph;
		this.myGame=mg;

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

		Menu file = new Menu("Data");
		menu.add(file);

		MenuItem item1 = new MenuItem("Show My Current Level");
		item1.addActionListener(this);
		file.add(item1);

		MenuItem item2 = new MenuItem("Amount of Games I Played");
		item2.addActionListener(this);
		file.add(item2);

		MenuItem item3 = new MenuItem("My Best Scores");
		item3.addActionListener(this);
		file.add(item3);

		MenuItem item4 = new MenuItem("Show My Ranks in the Class");
		item4.addActionListener(this);
		file.add(item4);

		this.setMenuBar(menu);
		
		makeStats();
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
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		String[] options = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
		int gameNum;
		
		switch(event) {

		case "Show My Current Level":

			String res = game.toString();
			System.out.println(res);

			JOptionPane.showMessageDialog(null, "My Curr Level is :"+currLevel, "From Data-Base", JOptionPane.INFORMATION_MESSAGE, baby);
			break;

		case "Amount of Games I Played":
			JOptionPane.showMessageDialog(null, "Amount of Games I Played :"+gamesPlayed, "From Data-Base", JOptionPane.INFORMATION_MESSAGE, wasted);

			break;

		case "My Best Scores":

			gameNum = JOptionPane.showOptionDialog(null, "Choose the Level to see Your best Score in it: ", "From Data-Base",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, options, options[0]);
			if (gameNum<0) gameNum=0;//in case user don't pick and press x

			JOptionPane.showMessageDialog(null, "My Best Score :\n"+myScore.get(gameNum), "From Data-Base", JOptionPane.INFORMATION_MESSAGE, baby);

			break;

		case "Show My Ranks in the Class":

			gameNum = JOptionPane.showOptionDialog(null, "Choose the Level to see Your Rank in it: ", "From Data-Base",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, robo, options, options[0]);
			if (gameNum<0) gameNum=0;//in case user don't pick and press x

			JOptionPane.showMessageDialog(null, "My Rank in the Class :\n"+myPlaceInClass[gameNum], "From Data-Base", JOptionPane.INFORMATION_MESSAGE, lama);
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
	
	/**
	 * Collecting my data and statistics from the Server
	 */
	private void makeStats() {
		//Initialize data
		myScore = new ArrayList<Integer>();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for (int j=0; j<24; j++) {
			myScore.add(j, 0);
			moves.add(j, 0);
		}
		
		int [] need_moves = {290,580,0,580,0,500,0,0,0,580,0,580,0,580,0,0,290,0,0,580,290,0,0,1140};
		int [] need_grade = {145,450,0,720,0,570,0,0,0,510,0,1050,0,310,0,0,235,0,0,250,200,0,0,1000};
		myPlaceInClass = new int [24];
		
		int moves_level=0 , score_level = 0;

		try {
			//Connecting to the Server
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();

			String allCustomersQuery = "SELECT * FROM oop.Logs where userID = "+myID+";";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);

			//Collecting data + amount of games I played in the Server
			while(resultSet.next())
			{
				gamesPlayed++;
				int id = resultSet.getInt("levelID");
				score_level = resultSet.getInt("score");
				moves_level = resultSet.getInt("moves");
				if (score_level >= need_grade[id]) {
					if (myScore.get(id) < score_level && moves_level <= need_moves[id] ) {
						myScore.remove(id);
						myScore.add(id, score_level);
						moves.remove(id);
						moves.add(id, moves_level);
					}
				}
			}
			
			//Figuring my place in the Class
			Hashtable<Integer, Integer> id_best = new Hashtable<Integer, Integer>();
			for (int i = 0; i < 24; i++) {
				String allCustomersQuery2 = "SELECT * FROM oop.Logs where levelID = "+i+" and score > "+myScore.get(i)+" and moves <= "+need_moves[i]+";";
				ResultSet resultSet2 = statement.executeQuery(allCustomersQuery2);
				while (resultSet2.next()) {
					id_best.put(resultSet2.getInt("userID"), resultSet2.getInt("score"));
				}
				myPlaceInClass[i] = id_best.size()+1;
				id_best.clear();
			}
			
			//Figuring my current level
			currLevel = 0;
			while (need_moves[currLevel] == 0) {
				currLevel++;
			}
			
			//Closing connection
			resultSet.close();
			statement.close();		
			connection.close();
			
		} catch (Exception e1) { e1.printStackTrace(); }
	}
}

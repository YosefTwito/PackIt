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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import algorithms.*;
import dataStructure.*;
import utils.*;

/**
 * This class makes a gui window to represent a graph and
 * use the Algorithms from class Graph_Algo on live.
 * (use the methods and represent it on the gui window while it is still up).
 * @author YosefTwito and EldarTakach
 */
public class GraphGui extends JFrame implements ActionListener, GraphListener{

	private static final long serialVersionUID = 1L;

	graph gr;

	public GraphGui(DGraph g){
		g.addListener(this);
		this.gr=g;
		initGUI(g);
	}

	public void paint(Graphics d) {
		super.paint(d);

		if (gr != null && gr.nodeSize()>1) {
			//get nodes
			Collection<node_data> nodes = gr.getV();

			for (node_data n : nodes) {
				//draw nodes
				Point3D p = n.getLocation();
				d.setColor(Color.BLACK);
				d.fillOval(p.ix(), p.iy(), 11, 11);

				//draw nodes-key's
				d.setColor(Color.BLUE);
				d.drawString(""+n.getKey(), p.ix()-4, p.iy()-4);

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
						//draw direction
						d.setColor(Color.MAGENTA);
						d.fillOval((int)((p.ix()*0.7)+(0.3*p2.ix()))+2, (int)((p.iy()*0.7)+(0.3*p2.iy())), 9, 9);
						//draw weight
						String sss = ""+String.valueOf(e.getWeight());
						d.drawString(sss, 1+(int)((p.ix()*0.7)+(0.3*p2.ix())), (int)((p.iy()*0.7)+(0.3*p2.iy()))-2);
					}
				}	
			}
		}	
	}

	private void initGUI(graph g) {
		this.gr=g;
		this.setSize(1280, 720);
		this.setTitle("Hello and welcome to The Maze Of Waze !");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon img = new ImageIcon("Rocket.png");
		this.setIconImage(img.getImage());
		this.setResizable(true);

		MenuBar menuBar = new MenuBar();
		this.setMenuBar(menuBar);

		Menu file = new Menu("File ");
		menuBar.add(file);

		Menu alg  = new Menu("Algorithms ");
		menuBar.add(alg);

		MenuItem item1 = new MenuItem("Init Original Graph");
		item1.addActionListener(this);
		file.add(item1);

		MenuItem item2 = new MenuItem("Init from File ");
		item2.addActionListener(this);
		file.add(item2);

		MenuItem item3 = new MenuItem("Save as File ");
		item3.addActionListener(this);
		file.add(item3);

		MenuItem item4 = new MenuItem("Save as png ");
		item4.addActionListener(this);
		file.add(item4);

		MenuItem item7 = new MenuItem("Is it Conncected ?"  );
		item7.addActionListener(this);
		alg.add(item7);

		MenuItem item12 = new MenuItem("Shortest Path Distance");
		item12.addActionListener(this);
		alg.add(item12);

		MenuItem item5 = new MenuItem("Show Shortest Path  ");
		item5.addActionListener(this);
		alg.add(item5);

		MenuItem item6 = new MenuItem("The SalesMan Problem");
		item6.addActionListener(this);
		alg.add(item6);
	}

	@Override
	public void actionPerformed(ActionEvent Command) {
		String str = Command.getActionCommand();		
		Graph_Algo t=new Graph_Algo();
		JFileChooser j;

		switch(str) {

		case "Init Original Graph":
			initGUI(this.gr);
			break;

		case "Init from File ":
			t=new Graph_Algo();

			j = new JFileChooser(FileSystemView.getFileSystemView());
			j.setDialogTitle("Init graph out of file.."); 

			int userSelection = j.showOpenDialog(null);
			if(userSelection == JFileChooser.APPROVE_OPTION) {
				System.out.println("You chose to open this file - " + j.getSelectedFile().getName());
				t.init(j.getSelectedFile().getAbsolutePath());
				graph gr_new=t.copy();
				initGUI(gr_new);	
			}			
			break;

		case "Save as File ":
			t=new Graph_Algo((DGraph)this.gr);		

			j = new JFileChooser(FileSystemView.getFileSystemView());
			j.setDialogTitle("Save graph to file..");

			int userSelection1 = j.showSaveDialog(null);
			if (userSelection1 == JFileChooser.APPROVE_OPTION) {
				System.out.println("Saved as file - " + j.getSelectedFile().getAbsolutePath());
				t.save(j.getSelectedFile().getAbsolutePath());
			}
			break;

		case "Save as png ":
			j = new JFileChooser(FileSystemView.getFileSystemView());
			j.setDialogTitle("Save as png..");
			FileNameExtensionFilter filter = new FileNameExtensionFilter(" .png","png");
			j.setFileFilter(filter);

			int userSelection2 = j.showSaveDialog(null);
			if (userSelection2 == JFileChooser.APPROVE_OPTION) {
				try {
					BufferedImage i = new BufferedImage(this.getWidth(), this.getHeight()+45, BufferedImage.TYPE_INT_RGB);
					Graphics g = i.getGraphics();
					paint(g);
					if (j.getSelectedFile().getName().endsWith(".png")) {
						ImageIO.write(i, "png", new File(j.getSelectedFile().getAbsolutePath()));
					}
					else {
						ImageIO.write(i, "png", new File(j.getSelectedFile().getAbsolutePath()+".png"));
					}
					System.out.println("Saved as png - " + j.getSelectedFile().getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;

		case "Show Shortest Path  ":
			try {
				System.out.println("Show Shortest Path: ");
				JFrame SSPin = new JFrame();

				int srcSSP=0;
				int destSSP=0;
				try {
					String SourceNodeSSP = JOptionPane.showInputDialog(SSPin,"Enter Source-Node:");
					String DestNodeSSP = JOptionPane.showInputDialog(SSPin,"Enter Destination-Node:");

					srcSSP = Integer.parseInt(SourceNodeSSP);
					destSSP = Integer.parseInt(DestNodeSSP);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(SSPin, "You've entered illegal node's-key");
					System.out.println("You've entered illegal node's-key");
					break;
				}
				if (this.gr.getNode(srcSSP)==null) {
					JOptionPane.showMessageDialog(SSPin, "You've entered illegal node's-key");
					System.out.println("You've entered illegal node's-key");
					break;
				}
				if (this.gr.getNode(destSSP)==null) {
					JOptionPane.showMessageDialog(SSPin, "You've entered illegal node's-key");
					System.out.println("You've entered illegal node's-key");
					break;
				}

				Graph_Algo newGSSP = new Graph_Algo();
				newGSSP.init(gr);

				List<node_data> SSPdis = newGSSP.shortestPath(srcSSP, destSSP);
				graph gr_new=new DGraph();
				if(SSPdis.size()==1) {
					System.out.println("There is no valid path between those nodes");
					JOptionPane.showMessageDialog(SSPin, "There is no valid path between those nodes");
					break;
				}
				gr_new.addNode(SSPdis.get(0));
				gr_new.getNode(SSPdis.get(0).getKey()).setInfo("");
				gr_new.getNode(SSPdis.get(0).getKey()).setTag(0);
				System.out.print(SSPdis.get(0).getKey());
				for (int i=1; i<SSPdis.size(); i++) {
					System.out.print(" --> "+SSPdis.get(i).getKey());
					gr_new.addNode(SSPdis.get(i));
					gr_new.getNode(SSPdis.get(i).getKey()).setInfo("");
					gr_new.getNode(SSPdis.get(i).getKey()).setTag(0);
					gr_new.connect(SSPdis.get(i-1).getKey(), SSPdis.get(i).getKey(), this.gr.getEdge(SSPdis.get(i-1).getKey(), SSPdis.get(i).getKey()).getWeight());
				}
				this.initGUI(gr_new);
			}	
			catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println();
			System.out.println();
			break;

		case "Shortest Path Distance":
			try
			{
				JFrame SPDinput = new JFrame();

				int srcSPD=0;
				int destSPD=0;
				try {
					String SourceNodeSPD = JOptionPane.showInputDialog(SPDinput,"Enter Source-Node:");
					String DestNodeSPD = JOptionPane.showInputDialog(SPDinput,"Enter Destination-Node:");

					srcSPD = Integer.parseInt(SourceNodeSPD);
					destSPD = Integer.parseInt(DestNodeSPD);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(SPDinput, "You've entered illegal node's-key");
					System.out.println("You've entered illegal node's-key");
					System.out.println();
					break;
				}

				Graph_Algo newg = new Graph_Algo();			
				newg.init(this.gr);

				double x = newg.shortestPathDist(srcSPD, destSPD);
				if (x==-1) {
					JOptionPane.showMessageDialog(SPDinput, "You've entered illegal node's-key");
					System.out.println("You've entered illegal node's-key");
				}
				else if(x==Double.MAX_VALUE) {
					JOptionPane.showMessageDialog(SPDinput, "There is no such path (Distance = Infinity).");
					System.out.println("There is no such path (Distance = Infinity).");
				}
				else {
					JOptionPane.showMessageDialog(SPDinput, "The Shortest Path Distance is: " + x);
					System.out.println("Shortest Path Distance is: " + x);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println();
			break;

		case "The SalesMan Problem": 
			JFrame TSPinput = new JFrame();

			System.out.println("The SalesMan Problem: ");
			String SourceNodeTSP = JOptionPane.showInputDialog(TSPinput,"How many nodes ?");
			int manyTSP=1;
			try {
				manyTSP = Integer.parseInt(SourceNodeTSP);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(TSPinput, "Ilegal number of nodes.");
				break;
			}
			if (manyTSP<1 || manyTSP>this.gr.nodeSize()) {
				JOptionPane.showMessageDialog(TSPinput, "Ilegal number of nodes.");
				break;
			}

			int cmon=0;
			if (manyTSP==1) {
				SourceNodeTSP = JOptionPane.showInputDialog(TSPinput,"Enter node-key");
				try {
					cmon = Integer.parseInt(SourceNodeTSP);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(TSPinput, "Ilegal key.");
					break;
				}
				graph gr_new=new DGraph();

				gr_new.addNode(this.gr.getNode(cmon));
				this.initGUI(gr_new);	
				break;
			}

			if (manyTSP==2) {
				int cmon2;
				graph gr_new=new DGraph();
				SourceNodeTSP = JOptionPane.showInputDialog(TSPinput,"Enter node-key 1/2:");
				try {
					cmon = Integer.parseInt(SourceNodeTSP);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(TSPinput, "Ilegal key.");
					break;
				}
				gr_new.addNode(this.gr.getNode(cmon));

				String DestNodeTSP = JOptionPane.showInputDialog(TSPinput,"Enter node-key 2/2:");
				try {
					cmon2 = Integer.parseInt(DestNodeTSP);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(TSPinput, "Ilegal key.");
					break;
				}
				gr_new.addNode(this.gr.getNode(cmon2));

				if (gr.getEdge(cmon, cmon2)!=null) {
					gr_new.connect(gr_new.getNode(cmon).getKey(), gr_new.getNode(cmon2).getKey(), this.gr.getEdge(cmon, cmon2).getWeight());
					this.initGUI(gr_new);
					System.out.println(cmon+" --> "+ cmon2);
					break;
				}
				else if(gr.getEdge(cmon2, cmon)!=null){
					gr_new.connect(gr_new.getNode(cmon2).getKey(), gr_new.getNode(cmon).getKey(), this.gr.getEdge(cmon2, cmon).getWeight());
					this.initGUI(gr_new);	
					System.out.println(cmon2+" --> "+ cmon);
					break;
				}
				else {
					JOptionPane.showMessageDialog(TSPinput, "Coudln't find path.");
					System.out.println("Coudln't find path.");
					break;	
				}
			}

			List<Integer> TSPnodes = new ArrayList<Integer>();
			int TSPkey=0;
			for (int i=0; i<manyTSP; i++) {
				SourceNodeTSP = JOptionPane.showInputDialog(TSPinput,"Enter node-key "+(i+1)+"/"+manyTSP);
				try {
					TSPkey = Integer.parseInt(SourceNodeTSP);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(TSPinput, "Ilegal key.");
					break;
				}
				if(this.gr.getNode(TSPkey)==null) {
					JOptionPane.showMessageDialog(TSPinput, "Ilegal key.");
					break;
				}	
				TSPnodes.add(TSPkey);
			}
			if (TSPnodes.size()!=manyTSP) { 
				JOptionPane.showMessageDialog(TSPinput, "You did not enter enough nodes.");
				break;
			}

			Graph_Algo newGTSP = new Graph_Algo();
			newGTSP.init(gr);

			List<node_data> TSP = newGTSP.TSP(TSPnodes);
			graph gr_new=new DGraph();

			String forJmessage="";
			if(TSP.isEmpty() || TSP==null) {
				JOptionPane.showMessageDialog(TSPinput, "Couldn't find path.");
				break;
			}
			gr_new.addNode(TSP.get(0));
			forJmessage=""+forJmessage+TSP.get(0).getKey();
			System.out.print(TSP.get(0).getKey());

			for (int i=1; i<TSP.size(); i++) {
				forJmessage=""+forJmessage+"-->"+TSP.get(i).getKey();
				System.out.print(" --> "+TSP.get(i).getKey());
				if (!((DGraph)gr_new).containsN(TSP.get(i).getKey())) {
					gr_new.addNode(TSP.get(i));	
				}
				if (!((DGraph)gr_new).containsE(TSP.get(i-1).getKey(), TSP.get(i).getKey())) {
					gr_new.connect(TSP.get(i-1).getKey(), TSP.get(i).getKey(), this.gr.getEdge(TSP.get(i-1).getKey(), TSP.get(i).getKey()).getWeight());
				}

			}
			this.initGUI(gr_new);
			JOptionPane.showMessageDialog(TSPinput, forJmessage);
			System.out.println();
			break;

		case "Is it Conncected ?":
			JFrame isIt = new JFrame();			
			Graph_Algo isCga = new Graph_Algo();
			isCga.init(this.gr);
			if (isCga.isConnected()) { 
				System.out.println("The graph is Connected !");
				JOptionPane.showMessageDialog(isIt, "The graph is Connected !");
			}
			else { 
				System.out.println("The graph is not Connected !");
				JOptionPane.showMessageDialog(isIt, "The graph is not Connected !");				
			}
			break;
		}
	}

	@Override
	public void graphUpdater() {	
		repaint();	
	}

}

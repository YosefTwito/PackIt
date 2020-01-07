package gameClient;
import java.awt.event.MouseEvent;
import java.util.Collection;

import dataStructure.graph;
import dataStructure.node_data;
import dataStructure.DGraph;
import dataStructure.edge_data;

import utils.Point3D;

public class MyGameGUI {
	graph graph;
	double minx=Integer.MAX_VALUE;
	double maxx=Integer.MIN_VALUE;
	double miny=Integer.MAX_VALUE;
	double maxy=Integer.MIN_VALUE;


	public static void main(String[] args) {
		graph x = new DGraph();
		MyGameGUI n = new MyGameGUI(x);
		n.initGUI();
		
		
	}
	public MyGameGUI(graph g) {
		graph=g;
	}
	public MyGameGUI() {
		graph=null;
	}
	
	private void initGUI() {
		StdDraw.setCanvasSize(1240, 860);
		StdDraw.setPenColor();
		
		System.out.println(StdDraw.isMousePressed());
		
		
	/*	if(graph != null)
		{
			Collection<node_data> nd = graph.getV();
			for (node_data node_data : nd) {
				Point3D s = node_data.getLocation();
				if(s.ix() < minx)
				{
					minx = s.ix();
				}
				if(s.ix() > maxx)
				{
					maxx = s.ix();
				}
				if(s.iy() > maxy)
				{
					maxy = s.iy();
				}
				if(s.iy() < miny)
				{
					miny = s.iy();
				}
			}
			StdDraw.setXscale(minx-(minx/10), maxx+(maxx/10));
			StdDraw.setYscale(miny-(miny/10),maxy+(maxy/10));
			//StdDraw.setG_GUI(this);
			//repaint();
		
		}*/
	}
	public void mouseClicked(MouseEvent e) {
		System.out.println("mouseClicked");
		
	}
}
	
	



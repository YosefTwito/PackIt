package gui;

import dataStructure.*;

public class Gui_Main {

	public static void main(String[] args) {

		graphFactory r = new graphFactory();
		DGraph g = r.randomGraphSmallConnected();

		GraphGui a = new GraphGui(g);
		a.setVisible(true);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		a.gr.addNode(r.nodeGenerator());
		a.gr.addNode(r.nodeGenerator());
		a.gr.addNode(r.nodeGenerator());
		a.gr.addNode(r.nodeGenerator());
	}
}


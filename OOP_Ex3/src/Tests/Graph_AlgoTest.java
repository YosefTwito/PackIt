package Tests;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import algorithms.Graph_Algo;
import dataStructure.*;
import gui.graphFactory;

class Graph_AlgoTest {

	private Graph_Algo factory() {
		graphFactory gf = new graphFactory();
		graph t = gf.randomGraphSmallConnected();
		Graph_Algo ga = new Graph_Algo(t);
		return ga;
	}

	@Test
	void testInitSave() {
	Graph_Algo ga = this.factory();
	ga.save("Test");
	Graph_Algo n_ga = new Graph_Algo();
	n_ga.init("Test");
	if(!ga.equals(n_ga)) { fail(); }
	}

	@Test
	void testIsConnected() {
		Graph_Algo ga = this.factory();
		if (!(ga.isConnected())) { fail(); }
	}

	@Test
	void testShortestPathDist() {
		graphFactory x = new graphFactory();
		node_data a1 = x.nodeGenerator();
		node_data a2 = x.nodeGenerator();
		node_data a3 = x.nodeGenerator();
		node_data a4 = x.nodeGenerator();
		
		DGraph ng = new DGraph();
		ng.addNode(a1);
		ng.addNode(a2);
		ng.addNode(a3);
		ng.addNode(a4);
		
		ng.connect(a1.getKey(), a2.getKey(), 3);
		ng.connect(a2.getKey(), a3.getKey(), 5);
		ng.connect(a4.getKey(), a1.getKey(), 8);
		ng.connect(a3.getKey(), a4.getKey(), 1);
		Graph_Algo n=new Graph_Algo(ng);
		assertEquals(n.shortestPathDist(a1.getKey(), a4.getKey()),9);	
	}

	@Test
	void testShortestPath() {
		Graph_Algo ga = this.factory();
		List<node_data> l = ga.shortestPath(2, 5);
		List<Integer> k = new ArrayList<Integer>();
		k.add(2);
		k.add(4);
		k.add(8);
		k.add(9);
		k.add(6);
		k.add(5);
		if(l.size()!=k.size()) {fail();}
		for(int i=0;i<l.size();i++) {
			if(l.get(i).getKey()!=k.get(i)) { fail(); }			
		}
	}

}

package dataStructure;

import java.io.Serializable;

public class edge implements edge_data,Serializable {

	private static final long serialVersionUID = 1L;

	//Edge's parameters:
	private int src, dest, tag;
	private double weight;
	private String info;

	//Constructors:
	public edge(int src, int dest, double w) {
		this.src=src;
		this.dest=dest;
		this.tag=0;
		this.weight=w;
		this.info="";
	}

	/**
	 * for details only
	 * @param o
	 */
	public edge(edge o) {
		this.src=o.src;
		this.dest=o.dest;
		this.tag=o.tag;
		this.weight=o.weight;
		this.info=o.info;
	}

	//Getters/Setters:
	@Override
	public int getSrc() { return this.src; }

	@Override
	public int getDest() { return this.dest; }

	@Override
	public double getWeight() { return this.weight; }

	@Override
	public String getInfo() { return this.info; }

	@Override
	public void setInfo(String s) { this.info = s; }

	@Override
	public int getTag() { return this.tag; }

	@Override
	public void setTag(int t) { this.tag = t; }

}

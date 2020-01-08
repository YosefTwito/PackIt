package dataStructure;

import java.io.Serializable;

import utils.Point3D;

public class node implements node_data,Serializable {

	private static final long serialVersionUID = 1L;

	public static int keyMaker=0;

	//Node Parameters:
	private int key, tag;
	private Point3D location;
	private double weight;
	private String info;

	//Constructors:
	public node () {
		this.key=keyMaker;
		this.tag=0;
		this.weight=0;
		this.info="";
		keyMaker++;
	}

	/**
	 * for details only
	 * @param o
	 */
	protected node (node o) {
		this.key=o.key;
		this.tag=o.tag;
		this.weight=o.weight;
		this.info=o.info;
		this.location=o.location;
	}

	public node (Point3D loc) {
		this.key=keyMaker;
		this.location=loc;
		this.tag=0;
		this.weight=0;
		this.info="";
		keyMaker++;
	}

	//Getters/Setters:
	@Override
	public int getKey() { return this.key; }

	@Override
	public Point3D getLocation() { return this.location; }

	@Override
	public void setLocation(Point3D p) { this.location = p; }

	@Override
	public double getWeight() { return this.weight; }

	@Override
	public void setWeight(double w) { this.weight = w; }

	@Override
	public String getInfo() { return this.info; }

	@Override
	public void setInfo(String s) { this.info = s; }

	@Override
	public int getTag() { return this.tag; }

	@Override
	public void setTag(int t) { this.tag = t; }

}

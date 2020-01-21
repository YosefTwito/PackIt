package elements;

import utils.Point3D;

/**
 * This class represents a fruit.
 * can be a banana or apple(-1 = banana, 1 = apple).
 * we need to read data from JSon file and fetch it to the fruit.
 * such:
 * where he is, his points, and the fruit itself.
 * @author YosefTwito and EldarTakach
 */
public class Fruit {
	
	//Parameters
	private double value;
	private int type;
	private Point3D pos;
	public int from;
	public int to;
	
	//Constructors

	public Fruit() {;}
	
	public Fruit(double value, int y, Point3D pos) {
		this.value=value;
		this.pos=pos;
		this.type=y;
	}
	
	public Fruit(Fruit f) {
		this.from=f.from;
		this.to=f.to;
		this.pos=f.pos;
		this.type=f.type;
		this.value=f.value;
	}
	
	//Getters and Setters
	
	public double getValue() { return value; }
	public void setValue(double value) { this.value = value; }

	public int getType() { return type; }
	public void setType(int type) { this.type = type; }

	public Point3D getPos() { return pos; }
	public void setPos(Point3D pos) { this.pos = pos; }

	public int getFrom() { return from; }
	public void setFrom(int from) { this.from = from; }

	public int getTo() { return to; }
	public void setTo(int to) { this.to = to; }
	
}

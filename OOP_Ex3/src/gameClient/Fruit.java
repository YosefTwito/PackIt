package gameClient;

import utils.Point3D;

/**
 * this class represtents a fruit.
 * can be a banana or apple(-1 = banana, 1 = apple).
 * we need to read data from json file and fetch it to the fruit.
 * such:
 * where he is, his points, and the fruit itself.
 * @author Eldar and Yossi.
 *
 */
public class Fruit {
	double value;
	enum type { banana, apple , eldar};
	Point3D pos;
	public int from;
	public int to;
	type fru;
	
	
	public Fruit(double value, int y, Point3D pos) {
		this.value=value;
		this.pos=pos;
		if(y==1) {
			fru=type.banana;
			
		}
		if(y==-1) {
			fru=type.apple;
		}
		else fru=type.eldar;
	}
	public Fruit() {
		
	}
	
	public int getType(){ 
		if (this.fru==type.apple) return 1;
		else if (this.fru==type.banana) return -1;
		else return 2;
	}
	
	public void setPos(Point3D np) { this.pos = np; }
	public Point3D getPos() { return this.pos; }
	public void setV(double v) { this.value=v; }
	public double getV() { return this.value; }
}

package gameClient;

import utils.Point3D;

/**
 * this class represtents a fruit.
 * can be a banana or apple(-1bananan,1-apple).
 * we need to read data from json file and fetch it to the fruit.
 * such:
 * where he is, his points, and the fruit itself.
 * @author Eldar and Yossi.
 *
 */
public class Fruit {
	double value;
	enum type { banana, apple };
	Point3D pos;
	type fru;
	
	
	public Fruit(double value, int y, Point3D pos) {
		this.value=value;
		this.pos=pos;
		if(y==-1) {
			fru=type.banana;
			
		}
		if(y==1) {
			fru=type.apple;
		}
		else fru=null;
	}
	public Fruit() {
		
	}

}

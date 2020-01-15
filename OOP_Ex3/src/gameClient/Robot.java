package gameClient;

import utils.Point3D;

/**
 * this class represents Robot.
 * we need to read data from json file and fetch it to our robot
 * the robot knows:
 * where he is, and where he is going.
 * the score (fruits) he collected.
 * @author Eldar and Yossi
 *
 */
public class Robot {
	int src; // the source of the robot.
	int dest; // the dest of the robot.
	int id; // the id of the robot.
	double value=0; // amount of points collected.
	Point3D pos; //3D pos of robot.

	public Robot(int rid, int src, int dest,Point3D pos,double value) {
		this.id=rid;
		this.src=src;
		this.dest=dest;
		this.value=value;
		this.pos=pos;
	}

	public Robot() {

	}
	
	public Point3D getPos() { return this.pos; }

}

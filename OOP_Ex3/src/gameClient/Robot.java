package gameClient;
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
	int amount=0; // amount of robots

	public Robot(int rid, int src, int dest) {
		this.id=rid;
		this.src=src;
		this.dest=dest;
	}
	
	public Robot() {
		
	}

}

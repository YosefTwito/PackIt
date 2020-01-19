package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import Server.*;
import dataStructure.*;
import gameClient.*;

class RobotTest {

	@Test
	void testRobotCon() {
		game_service game = Game_Server.getServer(0);
		String str = game.getGraph();
		DGraph gr = new DGraph();
		gr.init(str);
		MyGame myGame = new MyGame(gr, game);
		ArrayList<Robot> r = myGame.robo_list;
		System.out.println(r.get(0));
	}

}

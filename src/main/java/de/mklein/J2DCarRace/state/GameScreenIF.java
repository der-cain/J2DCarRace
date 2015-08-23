package de.mklein.J2DCarRace.state;

import org.jbox2d.common.Vec2;

public interface GameScreenIF {
	void logic();
	void input();
	void keystrokes();
	void mouseUp(Vec2 p, int button);
	void mouseDown(Vec2 p, int button);
	void mouseMove(Vec2 p);
	void mouseDrag(Vec2 p, int button);
	void render();
	void setUpObjects();
	void setUpMatrices();
	void pause();
	void exit();
}

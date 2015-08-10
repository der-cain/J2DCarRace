package de.mklein.J2DCarRace.state;

public interface GameScreenIF {
	void logic();
	void keystrokes();
	void input();
	void render();
	void setUpObjects();
	void setUpMatrices();
	void exit();
}

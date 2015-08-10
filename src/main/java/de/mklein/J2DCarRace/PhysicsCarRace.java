package de.mklein.J2DCarRace;

import static org.lwjgl.opengl.GL11.*;

import java.util.Stack;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import de.mklein.J2DCarRace.state.GameScreenIF;
import de.mklein.J2DCarRace.state.Menu;

public class PhysicsCarRace {

	public static final String 		WINDOW_TITLE      	= "J2D Car Race";
	public static final int[]  		WINDOW_DIMENSIONS 	= { 800, 600 };
	
	private GameScreenIF 	   		m_screen 			= null;
	private Stack<GameScreenIF> 	m_screenStack		= new Stack<GameScreenIF>();

	public void openScreen(GameScreenIF newScreen) {
		if(m_screen != null) {
			m_screen.exit();
			m_screenStack.push(m_screen);
		}
		m_screen = newScreen;
		setUpObjects();
		setUpMatrices();
	}
	
	public void exitScreen() {
		m_screen.exit();
		if(m_screenStack.isEmpty()) {
			cleanUp(false);
		}
		m_screen = m_screenStack.pop();
		setUpObjects();
		setUpMatrices();
	}
	
	private void cleanUp(boolean asCrash) {
		Display.destroy();
		System.exit(asCrash ? 1 : 0);
	}

	private void setUpDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]));
			Display.setTitle(WINDOW_TITLE);
			Display.create();

			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		} catch (LWJGLException e) {
			e.printStackTrace();
			cleanUp(true);
		}
	}

	private void setUpObjects() {
	    m_screen.setUpObjects();
    }

	private void setUpMatrices() {
		glMatrixMode(GL_PROJECTION);
	    glLoadIdentity();
		glOrtho(0, PhysicsCarRace.WINDOW_DIMENSIONS[0], 0, PhysicsCarRace.WINDOW_DIMENSIONS[1], 1, -1);
		
		glMatrixMode(GL_MODELVIEW);
	    glLoadIdentity();
	    glViewport(0, 0, PhysicsCarRace.WINDOW_DIMENSIONS[0], PhysicsCarRace.WINDOW_DIMENSIONS[1]);
	    
	    m_screen.setUpMatrices();
    }
	
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		m_screen.render();
	}

	private void update() {
		Display.update();
		Display.sync(60);
	}
	
	private void input() {
		m_screen.input();
		// process keystrokes
		while(Keyboard.next()) {
			// process screen specific input
			m_screen.keystrokes();
			
			switch(Keyboard.getEventKey()) {
			case Keyboard.KEY_ESCAPE:
				if(Keyboard.getEventKeyState()) {
					if(Keyboard.isRepeatEvent()) {
						// Key is held down
					} else {
						// Key was pressed
						exitScreen();
					}
				} else {
					// Key is released
				}
				break;
			}			
		}
	}
	
	public void enterGameLoop() {
		while (!Display.isCloseRequested()) {
			render();
			m_screen.logic();
			input();
			update();
		}
	}

	public static void main(String[] args) {
		PhysicsCarRace phyCarRace = new PhysicsCarRace();
		phyCarRace.setUpDisplay();
		phyCarRace.openScreen(new Menu(phyCarRace));
		phyCarRace.enterGameLoop();
	}

}
package de.mklein.J2DCarRace;

import static org.lwjgl.opengl.GL11.*;

import java.util.Stack;

import org.jbox2d.common.Vec2;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import de.mklein.J2DCarRace.state.GameScreenIF;
import de.mklein.J2DCarRace.state.Menu;

public class PhysicsCarRace {

	public static final String 		WINDOW_TITLE      	        = "J2D Car Race";
	public static final int[]  		WINDOW_DEFAULT_DIMENSIONS 	= { 800, 600 };
	
	private GameScreenIF 	   		m_screen 			= null;
	private Stack<GameScreenIF> 	m_screenStack		= new Stack<GameScreenIF>();
	
	private boolean mouseTracing;

	/** can be used to do calculations only every X-th frame using modulo */
	protected long framecounter = 0L;
    /** frames per second */
    protected int fps;
    /** last fps time */
    protected long lastFPS;
 
	public void openScreen(GameScreenIF newScreen) {
		if(m_screen != null) {
			m_screen.pause();
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
//		setUpObjects();
		setUpMatrices();
	}
	
	public long getFramecounter() {
		return framecounter;
	}

	private void cleanUp(boolean asCrash) {
		Display.destroy();
		System.exit(asCrash ? 1 : 0);
	}

	private void setUpDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(WINDOW_DEFAULT_DIMENSIONS[0], WINDOW_DEFAULT_DIMENSIONS[1]));
			Display.setTitle(WINDOW_TITLE);
			Display.setResizable(true);
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
		m_screen.setUpMatrices();
    }
	
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		m_screen.render();
	}

	private void update() {
		Display.update();
		Display.sync(60);
		updateFPS();
	}
	
	private void input() {
		m_screen.input();
		// process buffered keystrokes
		while(Keyboard.next()) {
			// process screen specific keystrokes
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
		
		while(Mouse.next()) {
			Vec2 mouse = new Vec2(Mouse.getX(), Mouse.getY());
			if(Mouse.getEventButton() > -1) {
				// Mouse button state has changed
				if (Mouse.getEventButtonState()) {
					// Button was pressed
					m_screen.mouseDown(new Vec2(mouse), Mouse.getEventButton());
					mouseTracing = true;
				} else {
					// Button was released
					m_screen.mouseUp(new Vec2(mouse), Mouse.getEventButton());
					mouseTracing = false;
				}
			} else {
				// Mouse was moved
				m_screen.mouseMove(new Vec2(mouse));
				if(mouseTracing)
					m_screen.mouseDrag(new Vec2(mouse), Mouse.getEventButton());
			}
		}

	}
	
	public boolean pressedOnce(int key) {
		if(Keyboard.getEventKey() == key) {
			if(Keyboard.getEventKeyState()) {
				return !Keyboard.isRepeatEvent();
			}
		}
		return false;
	}

	public void enterGameLoop() {
		lastFPS = getTime();
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

    /**
     * Get the accurate system time
     * 
     * @return The system time in milliseconds
     */
    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
     
    /**
     * Calculate the FPS and set it in the title bar
     */
    public void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            Display.setTitle(WINDOW_TITLE + " - FPS: " + fps);
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
		framecounter++;
    }
     
}
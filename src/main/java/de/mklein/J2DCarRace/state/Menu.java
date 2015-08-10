package de.mklein.J2DCarRace.state;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.lwjgl.input.Keyboard;

import de.mklein.J2DCarRace.Camera;
import de.mklein.J2DCarRace.PhysicsCarRace;
import de.mklein.J2DCarRace.debug.LwjglDebugDraw;

public class Menu implements GameScreenIF {

	protected DebugDraw m_dd;
	protected Camera    m_camera = null;

	enum MenuItems {
		START, MULTIPLAYER, SETTINGS, QUIT
	}

	MenuItems                active = MenuItems.START;

	protected PhysicsCarRace g;

	public Menu(PhysicsCarRace g) {
		this.g = g;
	}

	@Override
	public void logic() {
		return;
	}

	@Override
    public void input() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
	public void keystrokes() {
		switch (Keyboard.getEventKey()) {
		case Keyboard.KEY_DOWN:
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.isRepeatEvent()) {
					// Key is held down
				} else {
					// Key was pressed
					active = ringCount(active.ordinal() + 1, MenuItems.values().length);
				}
			} else {
				// Key is released
			}
			break;
		case Keyboard.KEY_UP:
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.isRepeatEvent()) {
					// Key is held down
				} else {
					// Key was pressed
					active = ringCount(active.ordinal() - 1, MenuItems.values().length);
				}
			} else {
				// Key is released
			}
			break;
		case Keyboard.KEY_RETURN:
			switch (active) {
			case START:
				g.openScreen(new Race(g));
			default:
				break;
			}
			break;

		default:
			break;
		}
	}

	private MenuItems ringCount(int value, int length) {
		return MenuItems.values()[(((value) % length) + length) % length];
	}

	@Override
	public void render() {
		Color3f color = Color3f.WHITE;
		for (MenuItems m_item : MenuItems.values()) {
			if (m_item.equals(active)) {
				color = Color3f.RED;
			} else {
				color = Color3f.WHITE;
			}
			m_dd.drawString(30, 30 + 30 * m_item.ordinal(), m_item.toString(), color);
		}
	}

	@Override
	public void setUpObjects() {
		m_camera = new Camera(new Vec2(0.0f, 0.0f), 15.0f, 0.05f);
		m_camera.getTransform().setExtents(
		        PhysicsCarRace.WINDOW_DIMENSIONS[0] / 2,
		        PhysicsCarRace.WINDOW_DIMENSIONS[1] / 2);

		m_dd = new LwjglDebugDraw();
		m_dd.setFlags(DebugDraw.e_shapeBit | DebugDraw.e_jointBit
		        | DebugDraw.e_pairBit);
		// dd.setFlags(DebugDraw.e_wireframeDrawingBit | DebugDraw.e_shapeBit |
		// DebugDraw.e_jointBit | DebugDraw.e_pairBit);
		m_dd.setViewportTransform(m_camera.getTransform());

		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void setUpMatrices() {
		// at the moment done in the main game

	}

	@Override
	public void exit() {
		// nothing here atm

	}

}

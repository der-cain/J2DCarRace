package de.mklein.J2DCarRace.state;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.lwjgl.input.Keyboard;

import de.mklein.J2DCarRace.PhysicsCarRace;

public class Menu extends GameScreenAB {

	enum MenuItems {
		CARRACE, TOWERBUILDER, SETTINGS, QUIT
	}

	MenuItems                active = MenuItems.CARRACE;

	public Menu(PhysicsCarRace g) {
		super(g);
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
			case CARRACE:
				g.openScreen(new Race(g));
				break;
			case TOWERBUILDER:
				g.openScreen(new Towerbuilder(g));
				break;
			case QUIT:
				g.exitScreen();
				break;
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
			m_dd.drawString(30, 30 + 18 * m_item.ordinal(), m_item.toString(), color);
		}
	}

	@Override
	public void setUpObjects() {
		m_camera.getTransform().setExtents(
		        PhysicsCarRace.WINDOW_DIMENSIONS[0] / 2,
		        PhysicsCarRace.WINDOW_DIMENSIONS[1] / 2);

		m_dd.setFlags(DebugDraw.e_shapeBit | DebugDraw.e_jointBit
		        | DebugDraw.e_pairBit);
		// dd.setFlags(DebugDraw.e_wireframeDrawingBit | DebugDraw.e_shapeBit |
		// DebugDraw.e_jointBit | DebugDraw.e_pairBit);
		m_dd.setViewportTransform(m_camera.getTransform());

		Keyboard.enableRepeatEvents(true);
	}
}

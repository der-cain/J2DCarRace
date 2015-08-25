package de.mklein.J2DCarRace.state;

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
		if(g.pressedOnce(Keyboard.KEY_DOWN))
			active = ringCount(active.ordinal() + 1, MenuItems.values().length);
		if(g.pressedOnce(Keyboard.KEY_UP))
			active = ringCount(active.ordinal() - 1, MenuItems.values().length);
		if(g.pressedOnce(Keyboard.KEY_RETURN)) {
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
		}
		super.keystrokes();
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
		super.setUpObjects();
	}
}

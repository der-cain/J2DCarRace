package de.mklein.J2DCarRace.state;

import java.util.Random;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.mklein.J2DCarRace.Camera;
import de.mklein.J2DCarRace.PhysicsCarRace;

public class Towerbuilder extends GameScreenAB {
	protected Body m_box;
	protected Body m_ground;

	public Towerbuilder(PhysicsCarRace g) {
		super(g);
	}

	public void render() {
		super.render();
	}

	public void logic() {
		super.logic();
	}

	@Override
	public void input() {
		super.input();
		// Camera scrolling
		// mouse in screen - camera in screen
		if (Mouse.isInsideWindow()) {
			Vec2 mousePosScreen = new Vec2((float) Mouse.getX(),
					(float) Mouse.getY());
			Vec2 mousePosScaled = new Vec2(mousePosScreen.x
					/ PhysicsCarRace.WINDOW_DIMENSIONS[0], mousePosScreen.y
					/ PhysicsCarRace.WINDOW_DIMENSIONS[1]);
			Vec2 screenCenter = new Vec2(0.5f, 0.5f);
			Vec2 offset = mousePosScaled.sub(screenCenter);
			float scrollX = Math.abs(offset.x) - 0.35f, scrollY = Math
					.abs(offset.y) - 0.35f;
			Vec2 scroll = new Vec2(Math.signum(offset.x)
					* ((scrollX > 0.0f) ? scrollX : 0.0f),
					Math.signum(offset.y) * ((scrollY > 0.0f) ? scrollY : 0.0f));
			// m_dd.drawString(30, 30, "Mousepos: " + offset.toString(),
			// Color3f.WHITE);
			// m_dd.drawString(30, 54, "Scroll: " + scroll.toString(),
			// Color3f.WHITE);
			m_camera.getTransform().setCenter(m_camera.getTransform().getCenter().add(scroll));

			// Camera zooming
			int notches = Mouse.getDWheel();
			if (notches != 0) {
				Camera.ZoomType zoom = notches < 0 ? Camera.ZoomType.ZOOM_OUT
						: Camera.ZoomType.ZOOM_IN;
				m_camera.zoomToPoint(mousePosScreen, zoom);
			}
		}
	}

	@Override
	public void keystrokes() {
		super.keystrokes();
		if(g.pressedOnce(Keyboard.KEY_S)) {
			spawnBox();
		}
	}

	public void setUpObjects() {
		super.setUpObjects();

		// create ground
		BodyDef groundDef = new BodyDef();
		groundDef.position.set(0, 0);
		groundDef.type = BodyType.STATIC;
		m_ground = m_world.createBody(groundDef);
		FixtureDef groundFixture = new FixtureDef();
		groundFixture.density = 1;
		groundFixture.friction = 1.0f;
		groundFixture.restitution = 0.3f;
		PolygonShape groundShape = new PolygonShape();
		groundShape.setAsBox(1000.0f, 1.0f);
		groundFixture.shape = groundShape;
		m_ground.createFixture(groundFixture);

		spawnBox();
	}
	
	public void spawnBox() {
		generateTile(new Random().nextInt(7));
	}
	
	public void generateTile(Integer number) {
		Vec2 tiles[][] = new Vec2[][] {
				new Vec2[] {new Vec2(0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f), new Vec2(3.0f, 0.0f)}, // | shape
				new Vec2[] {new Vec2(0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f), new Vec2(1.0f, 1.0f)}, // _|_ shape
				new Vec2[] {new Vec2(0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(0.0f, 1.0f), new Vec2(1.0f, 1.0f)}, // box shape
				new Vec2[] {new Vec2(0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f), new Vec2(2.0f, 1.0f)}, // L shape
				new Vec2[] {new Vec2(0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f), new Vec2(0.0f, 1.0f)}, // Rev-L shape
				new Vec2[] {new Vec2(0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(1.0f, 1.0f), new Vec2(2.0f, 1.0f)}, // Z shape
				new Vec2[] {new Vec2(0.0f, 1.0f), new Vec2(1.0f, 1.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f)}, // Rev-Z shape
		};
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.setPosition(new Vec2(0.0f, 5.0f));
		Body tile = m_world.createBody(bd);
		PolygonShape box = new PolygonShape();
		final float density = 1.0f;
		for (int i=0; i<4; i++) {
			box.setAsBox(0.5f, 0.5f, tiles[number][i], 0.0f);
			tile.createFixture(box, density);
		} 
	}

	/************ INPUT ************/

	/**
	 * Called for mouse-up
	 */
	@Override
	public void mouseUp(Vec2 p, int button) {
		super.mouseUp(p, button);
		destroyMouseJoint();
	}

	@Override
	public void mouseDown(Vec2 p, int button) {
		super.mouseDown(p, button);
		spawnMouseJoint(mouseWorld);
	}

	@Override
	public void mouseMove(Vec2 p) {
		super.mouseMove(p);
	}

	@Override
	public void mouseDrag(Vec2 p, int button) {
		super.mouseDrag(p, button);
		updateMouseJoint(mouseWorld);
	}

	/************ MOUSE JOINT ************/

	private final AABB queryAABB = new AABB();
	private final TestQueryCallback callback = new TestQueryCallback();
	private MouseJoint mouseJoint;

	private void spawnMouseJoint(Vec2 p) {
		if (mouseJoint != null) {
			return;
		}
		queryAABB.lowerBound.set(p.x - .001f, p.y - .001f);
		queryAABB.upperBound.set(p.x + .001f, p.y + .001f);
		callback.point.set(p);
		callback.fixture = null;
		m_world.queryAABB(callback, queryAABB);

		if (callback.fixture != null) {
			Body body = callback.fixture.getBody();
			MouseJointDef def = new MouseJointDef();
			def.bodyA = m_ground;
			def.bodyB = body;
			def.collideConnected = true;
			def.target.set(p);
			def.maxForce = 1000f * body.getMass();
			mouseJoint = (MouseJoint) m_world.createJoint(def);
			body.setAwake(true);
		}
	}

	private void updateMouseJoint(Vec2 target) {
		if (mouseJoint != null) {
			mouseJoint.setTarget(target);
		}
	}

	private void destroyMouseJoint() {
		if (mouseJoint != null) {
			m_world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
	}

}

class TestQueryCallback implements QueryCallback {

	public final Vec2 point;
	public Fixture fixture;

	public TestQueryCallback() {
		point = new Vec2();
		fixture = null;
	}

	public boolean reportFixture(Fixture argFixture) {
		Body body = argFixture.getBody();
		if (body.getType() == BodyType.DYNAMIC) {
			boolean inside = argFixture.testPoint(point);
			if (inside) {
				fixture = argFixture;

				return false;
			}
		}

		return true;
	}
}

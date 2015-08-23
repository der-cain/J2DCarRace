package de.mklein.J2DCarRace.state;

import java.util.Random;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

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
		if(g.getFramecounter() % 60 == 0) {
			// every second
			spawnTiles();
		}
		super.logic();
	}

	@Override
	public void input() {
		super.input();
	}

	@Override
	public void keystrokes() {
		super.keystrokes();
//		if(g.pressedOnce(Keyboard.KEY_S)) {
//			spawnTiles();
//		}
	}

	public void setUpObjects() {
		super.setUpObjects();

		// create ground
		BodyDef groundDef = new BodyDef();
		groundDef.position.set(0, 0);
		groundDef.type = BodyType.STATIC;
		m_ground = m_world.createBody(groundDef);
		FixtureDef groundFixture = new FixtureDef();
		groundFixture.density = 0.0f;
		groundFixture.friction = 1.0f;
		groundFixture.restitution = 0.3f;
		PolygonShape groundShape = new PolygonShape();
		groundShape.setAsBox(1000.0f, 1.0f);
		groundFixture.shape = groundShape;
		m_ground.createFixture(groundFixture);

		createBaseTile();
	}
	
	public void createBaseTile() {
		Vec2 baseTile[] = new Vec2[] {
				new Vec2(0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f), new Vec2(3.0f, 0.0f),
				new Vec2(4.0f, 0.0f), new Vec2(5.0f, 0.0f), new Vec2(6.0f, 0.0f), new Vec2(7.0f, 0.0f)}; // base tile
		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;
		bd.setPosition(new Vec2(-4.0f, 1.5f));
		Body tile = m_world.createBody(bd);
		PolygonShape box = new PolygonShape();
		final float density = 0.0f;
		for (int i=0; i<8; i++) {
			box.setAsBox(0.5f - 0.01f, 0.5f - 0.01f, baseTile[i], 0.0f);
			tile.createFixture(box, density);
		} 
	}
	
	public void spawnTiles() {
		Vec2 tileBoxes[] = new Vec2[] {
				new Vec2(8.0f,  2.0f),
				new Vec2(13.0f, 2.0f),
				new Vec2(18.0f, 2.0f),
			};
		
		AABB queryAABB = new AABB();
		FindFirstFixtureQueryCallback callback = new FindFirstFixtureQueryCallback();
		for(int i = 0; i<tileBoxes.length; i++) {
			queryAABB.lowerBound.set(tileBoxes[i].x - 2.0f, tileBoxes[i].y - 2.0f);
			queryAABB.upperBound.set(tileBoxes[i].x + 2.0f, tileBoxes[i].y + 2.0f);
			callback.found = false;
			m_world.queryAABB(callback, queryAABB);
			if (!callback.found) {
				generateTile(new Random().nextInt(7), tileBoxes[i]);
			}
		}
	}
	
	public void generateTile(Integer number, Vec2 startingPos) {
		Vec2 tiles[][] = new Vec2[][] {
				new Vec2[] {new Vec2(-1.0f, 0.0f), new Vec2(0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f)}, // | shape
				new Vec2[] {new Vec2( 0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f), new Vec2(1.0f, 1.0f)}, // _|_ shape
				new Vec2[] {new Vec2( 0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(0.0f, 1.0f), new Vec2(1.0f, 1.0f)}, // box shape
				new Vec2[] {new Vec2( 0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f), new Vec2(2.0f, 1.0f)}, // L shape
				new Vec2[] {new Vec2( 0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f), new Vec2(0.0f, 1.0f)}, // Rev-L shape
				new Vec2[] {new Vec2( 0.0f, 0.0f), new Vec2(1.0f, 0.0f), new Vec2(1.0f, 1.0f), new Vec2(2.0f, 1.0f)}, // Z shape
				new Vec2[] {new Vec2( 0.0f, 1.0f), new Vec2(1.0f, 1.0f), new Vec2(1.0f, 0.0f), new Vec2(2.0f, 0.0f)}, // Rev-Z shape
		};
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.setPosition(startingPos);
		Body tile = m_world.createBody(bd);
		PolygonShape box = new PolygonShape();
		FixtureDef tileFixture = new FixtureDef();
		tileFixture.density = 1.0f;
		tileFixture.friction = 1.0f;
		tileFixture.restitution = 0.3f;
		for (int i=0; i<4; i++) {
			box.setAsBox(0.5f - 0.01f, 0.5f - 0.01f, tiles[number][i], 0.0f);
			tileFixture.shape = box;
			tile.createFixture(tileFixture);
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
	private final PointInsideFixtureQueryCallback callback = new PointInsideFixtureQueryCallback();
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

class PointInsideFixtureQueryCallback implements QueryCallback {

	public final Vec2 point;
	public Fixture fixture;

	public PointInsideFixtureQueryCallback() {
		point = new Vec2();
		fixture = null;
	}

	public boolean reportFixture(Fixture fixture) {
		Body body = fixture.getBody();
		if (body.getType() == BodyType.DYNAMIC) {
			boolean inside = fixture.testPoint(point);
			if (inside) {
				this.fixture = fixture;

				return false;
			}
		}

		return true;
	}
}

class FindFirstFixtureQueryCallback implements QueryCallback {
	public Boolean found = false;

	@Override
	public boolean reportFixture(Fixture fixture) {
		Body body = fixture.getBody();
		if (body.getType() == BodyType.DYNAMIC) {
			found = true;
			return false;
		}
		return true;
	}
};
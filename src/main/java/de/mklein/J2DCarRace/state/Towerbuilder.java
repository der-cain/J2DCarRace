package de.mklein.J2DCarRace.state;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.mklein.J2DCarRace.Camera;
import de.mklein.J2DCarRace.PhysicsCarRace;
import de.mklein.J2DCarRace.debug.LwjglDebugDraw;

public class Towerbuilder implements GameScreenIF {
	private final World         m_world           = new World(new Vec2(0, -9.8f));
	private DebugDraw     		m_dd;
	protected Camera            m_camera          = null;

	protected PhysicsCarRace g;
	private Body m_box;
	
	public Towerbuilder(PhysicsCarRace g) {
		this.g = g;
	}
	
	public void render() {
		m_world.drawDebugData();
	}
	
	public void logic() {
		m_world.step(1 / 60f, 8, 3);
	}

	@Override
    public void input() {
		// Camera scrolling
		// mouse in screen - camera in screen
		Vec2 mousePosScreen = new Vec2((float)Mouse.getX(), (float)Mouse.getY());
		Vec2 mousePosScaled = new Vec2(
				mousePosScreen.x / PhysicsCarRace.WINDOW_DIMENSIONS[0], 
				mousePosScreen.y / PhysicsCarRace.WINDOW_DIMENSIONS[1]);
		Vec2 screenCenter = new Vec2(0.5f, 0.5f);
		Vec2 offset = mousePosScaled.sub(screenCenter);
		float scrollX = Math.abs(offset.x) - 0.35f, scrollY = Math.abs(offset.y) - 0.35f;
		Vec2 scroll = new Vec2(
				Math.signum(offset.x) * ((scrollX > 0.0f) ? scrollX : 0.0f), 
				Math.signum(offset.y) * ((scrollY > 0.0f) ? scrollY : 0.0f));
//		m_dd.drawString(30, 30, "Mousepos: " + offset.toString(), Color3f.WHITE);
//		m_dd.drawString(30, 54, "Scroll: " + scroll.toString(), Color3f.WHITE);
		m_camera.getTransform().setCenter(m_camera.getTransform().getCenter().add(scroll));
		
		// Camera zooming
        int notches = Mouse.getDWheel();
        if(notches != 0) {
            Camera.ZoomType zoom = notches < 0 ? Camera.ZoomType.ZOOM_OUT : Camera.ZoomType.ZOOM_IN;
            m_camera.zoomToPoint(mousePosScreen, zoom);
        }
    }

	@Override
	public void keystrokes() {
		// empty for now
	}

	public void setUpObjects() {

		Keyboard.enableRepeatEvents(true);

		m_camera = new Camera(new Vec2(0.0f, 5.0f), 40.0f, 0.05f);
		m_camera.getTransform().setExtents(PhysicsCarRace.WINDOW_DIMENSIONS[0] / 2, PhysicsCarRace.WINDOW_DIMENSIONS[1] / 2);

		m_dd = new LwjglDebugDraw();
		m_dd.setFlags(DebugDraw.e_shapeBit | DebugDraw.e_jointBit | DebugDraw.e_pairBit);
		// dd.setFlags(DebugDraw.e_wireframeDrawingBit | DebugDraw.e_shapeBit | DebugDraw.e_jointBit | DebugDraw.e_pairBit);
		m_dd.setViewportTransform(m_camera.getTransform());
		m_world.setDebugDraw(m_dd);

		// create ground
	    BodyDef groundDef = new BodyDef();
		groundDef.position.set(0, 0);
		groundDef.type = BodyType.STATIC;
		Body ground = m_world.createBody(groundDef);
		FixtureDef groundFixture = new FixtureDef();
		groundFixture.density = 1;
		groundFixture.friction = 1.0f;
		groundFixture.restitution = 0.3f;
		PolygonShape groundShape = new PolygonShape();
		groundShape.setAsBox(1000.0f, 1.0f);
		groundFixture.shape = groundShape;
		ground.createFixture(groundFixture);
	
		// create a dynamic sample box
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(1.0f, 1.0f);
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(0.0f, 3.0f);
		m_box = m_world.createBody(bd);
		m_box.createFixture(boxShape, 1.0f);

		// create a dynamic sample box
		bd.position.set(0.0f, 7.0f);
		m_box = m_world.createBody(bd);
		m_box.createFixture(boxShape, 1.0f);
}

	@Override
    public void setUpMatrices() {
	    // done in the main program
	    
    }

	@Override
    public void exit() {
	    // nothing to do here, example: save the state of the game here
	    
    }

	@Override
    public void pause() {
	    // TODO Auto-generated method stub
	    
    }
}

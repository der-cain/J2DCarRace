package de.mklein.J2DCarRace.state;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.mklein.J2DCarRace.Camera;
import de.mklein.J2DCarRace.PhysicsCarRace;
import de.mklein.J2DCarRace.debug.LwjglDebugDraw;

public class GameScreenAB implements GameScreenIF {

	protected final World m_world = new World(new Vec2(0, -9.8f));
	protected final DebugDraw m_dd = new LwjglDebugDraw();;
	protected final Camera m_camera = new Camera(new Vec2(0.0f, 5.0f), 40.0f, 0.05f);
	protected final PhysicsCarRace g;
	protected final Vec2 mouseWorld = new Vec2();
	
	public GameScreenAB(PhysicsCarRace g) {
		this.g = g;
	}

	public void render() {
		m_world.drawDebugData();
	}

	public void logic() {
		m_world.step(1 / 60f, 8, 3);
	}

	protected void scrollCamera() {
		if (Mouse.isInsideWindow()) {
			Vec2 mousePosScreen = new Vec2((float) Mouse.getX(), (float) Mouse.getY());
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
		}		
	}
	
	protected void zoomCamera() {
		if (Mouse.isInsideWindow()) {
			// Camera zooming
			Vec2 zoomPoint = new Vec2(Mouse.getX(), Mouse.getY());
			int notches = Mouse.getDWheel();
			if (notches != 0) {
				Camera.ZoomType zoom = notches < 0 ? Camera.ZoomType.ZOOM_OUT
						: Camera.ZoomType.ZOOM_IN;
				m_camera.zoomToPoint(zoomPoint, zoom);
			}
		}		
	}
	
	@Override
	public void input() {
		// Camera scrolling
		scrollCamera();
		//Camera zooming by default to the actual mouse position
		zoomCamera();
	}

	@Override
	public void keystrokes() {
		// Do nothing
	}

	public void setUpObjects() {
	
		Keyboard.enableRepeatEvents(true);
	
		m_camera.getTransform().setExtents(
				PhysicsCarRace.WINDOW_DIMENSIONS[0] / 2,
				PhysicsCarRace.WINDOW_DIMENSIONS[1] / 2);
	
		m_dd.setFlags(DebugDraw.e_shapeBit | DebugDraw.e_jointBit);
		// dd.setFlags(DebugDraw.e_wireframeDrawingBit | DebugDraw.e_shapeBit |
		// DebugDraw.e_jointBit | DebugDraw.e_pairBit);
		m_dd.setViewportTransform(m_camera.getTransform());
		m_world.setDebugDraw(m_dd);
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

	/**
	 * Called for mouse-up
	 */
	@Override
	public void mouseUp(Vec2 p, int button) {
	}

	public void keyPressed(char keyChar, int keyCode) {
	}

	public void keyReleased(char keyChar, int keyCode) {
	}

	public void mouseDown(Vec2 p, int button) {
		m_camera.getTransform().getScreenToWorld(p, mouseWorld);
	}

	public void mouseMove(Vec2 p) {
		m_camera.getTransform().getScreenToWorld(p, mouseWorld);
	}

	public void mouseDrag(Vec2 p, int button) {
		m_camera.getTransform().getScreenToWorld(p, mouseWorld);
	}

}
package de.mklein.J2DCarRace;

import static org.lwjgl.opengl.GL11.*;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.WheelJoint;
import org.jbox2d.dynamics.joints.WheelJointDef;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import de.mklein.J2DCarRace.debug.LwjglDebugDraw;

public class PhysicsCarRace {

	private static final String WINDOW_TITLE = "2D Car Race";
	private static final int[] WINDOW_DIMENSIONS = { 640, 480 };
	
	private final World world = new World(new Vec2(0, -9.8f));
	private WheelJoint spring1;
	private WheelJoint spring2;
	private float speed = 50.0f;

	protected Camera camera = null;

    private void render() {
    	glClear(GL_COLOR_BUFFER_BIT);
		world.drawDebugData();
	}

	private void logic() {
		world.step(1 / 60f, 8, 3);
	}

	private void input() {
		if (Keyboard.isKeyDown(Keyboard.KEY_A)
				&& !Keyboard.isKeyDown(Keyboard.KEY_D)) {
			spring1.enableMotor(true);
			spring1.setMotorSpeed(speed);
			spring2.enableMotor(true);
			spring2.setMotorSpeed(speed);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)
				&& !Keyboard.isKeyDown(Keyboard.KEY_A)) {
			spring1.enableMotor(true);
			spring1.setMotorSpeed(-speed);
			spring2.enableMotor(true);
			spring2.setMotorSpeed(-speed);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			spring1.enableMotor(true);
			spring1.setMotorSpeed(0.0f);
			spring2.enableMotor(true);
			spring2.setMotorSpeed(0.0f);
		} else {
			spring1.enableMotor(false);
			spring2.enableMotor(false);
		}
		for (Body body = world.getBodyList(); body != null; body = body.getNext()) {
			if (body.getType() == BodyType.DYNAMIC) {
				if (Mouse.isButtonDown(0)) {
					Vec2 mousePosition = new Vec2(Mouse.getX(), Mouse.getY());
					camera.getTransform().getScreenVectorToWorld(mousePosition, mousePosition);
					Vec2 bodyPosition = body.getPosition();
					Vec2 force = mousePosition.sub(bodyPosition);
					body.applyForce(force, body.getPosition());
				}
			}
		}
	}

	private void cleanUp(boolean asCrash) {
		Display.destroy();
		System.exit(asCrash ? 1 : 0);
	}

	private void setUpMatrices() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glMatrixMode(GL_PROJECTION);
		glOrtho(0, WINDOW_DIMENSIONS[0], 0, WINDOW_DIMENSIONS[1], 1, -1);
		glMatrixMode(GL_MODELVIEW);
	}

	private void setUpObjects() {
		
		camera = new Camera(new Vec2(0.0f, 0.0f), 25.0f, 0.05f);

		LwjglDebugDraw dd = new LwjglDebugDraw();
		dd.setFlags(DebugDraw.e_wireframeDrawingBit | DebugDraw.e_shapeBit | DebugDraw.e_jointBit | DebugDraw.e_pairBit);
		dd.setViewportTransform(camera.getTransform());
		world.setDebugDraw(dd);
		
		float hz = 4.0f;
		float zeta = 0.7f;

		// Car
		{
			PolygonShape chassis = new PolygonShape();
			Vec2 vertices[] = new Vec2[8];
			vertices[0] = new Vec2(-1.5f, -0.5f);
			vertices[1] = new Vec2(1.5f, -0.5f);
			vertices[2] = new Vec2(1.5f, 0.0f);
			vertices[3] = new Vec2(0.0f, 0.9f);
			vertices[4] = new Vec2(-1.15f, 0.9f);
			vertices[5] = new Vec2(-1.5f, 0.2f);
			chassis.set(vertices, 6);

			CircleShape circle = new CircleShape();
			circle.m_radius = 0.4f;

			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.position.set(0.0f, 1.0f);
			Body car = world.createBody(bd);
			car.createFixture(chassis, 1.0f);

			FixtureDef fd = new FixtureDef();
			fd.shape = circle;
			fd.density = 1.0f;
			fd.friction = 0.9f;

			bd.position.set(-1.0f, 0.35f);
			Body wheel1 = world.createBody(bd);
			wheel1.createFixture(fd);

			bd.position.set(1.0f, 0.4f);
			Body wheel2 = world.createBody(bd);
			wheel2.createFixture(fd);

			WheelJointDef jd = new WheelJointDef();
			Vec2 axis = new Vec2(0.0f, 1.0f);

			jd.initialize(car, wheel1, wheel1.getPosition(), axis);
			jd.motorSpeed = 0.0f;
			jd.maxMotorTorque = 20.0f;
			jd.enableMotor = true;
			jd.frequencyHz = hz;
			jd.dampingRatio = zeta;
			spring1 = (WheelJoint) world.createJoint(jd);

			jd.initialize(car, wheel2, wheel2.getPosition(), axis);
			jd.motorSpeed = 0.0f;
			jd.maxMotorTorque = 10.0f;
			jd.enableMotor = true;
			jd.frequencyHz = hz;
			jd.dampingRatio = zeta;
			spring2 = (WheelJoint) world.createJoint(jd);
		}

		BodyDef groundDef = new BodyDef();
		groundDef.position.set(0, 0);
		groundDef.type = BodyType.STATIC;
		PolygonShape groundShape = new PolygonShape();
		groundShape.setAsBox(1000, 0);
		Body ground = world.createBody(groundDef);
		FixtureDef groundFixture = new FixtureDef();
		groundFixture.density = 1;
		groundFixture.restitution = 0.3f;
		groundFixture.shape = groundShape;
		ground.createFixture(groundFixture);
		
	}

	private void update() {
		Display.update();
		Display.sync(60);
	}

	private void enterGameLoop() {
		while (!Display.isCloseRequested()) {
			render();
			logic();
			input();
			update();
		}
	}

	private void setUpDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]));
			Display.setTitle(WINDOW_TITLE);
			Display.create();

		} catch (LWJGLException e) {
			e.printStackTrace();
			cleanUp(true);
		}
	}

	public static void main(String[] args) {
		PhysicsCarRace phyCarRace = new PhysicsCarRace();
		phyCarRace.setUpDisplay();
		phyCarRace.setUpObjects();
		phyCarRace.setUpMatrices();
		phyCarRace.enterGameLoop();
		phyCarRace.cleanUp(false);
	}
}
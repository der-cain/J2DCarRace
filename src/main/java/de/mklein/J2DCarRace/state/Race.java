package de.mklein.J2DCarRace.state;

import java.util.Random;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.WheelJoint;
import org.jbox2d.dynamics.joints.WheelJointDef;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.mklein.J2DCarRace.Camera;
import de.mklein.J2DCarRace.PhysicsCarRace;

public class Race extends GameScreenAB {
	/** All of this is car specific and should be moved to its own class */
	private WheelJoint          m_spring1;
	private WheelJoint          m_spring2;
	private float               m_speed           = 50.0f;

	protected Body              m_car;
	
	protected Vec2 prevPos = new Vec2();
	protected long prevTime = 0L;

	protected long currTime;
	protected Vec2 currPos;
	protected float velocity = 0.0f;
	/** end car specific */
	
	public Race(PhysicsCarRace g) {
		super(g);
	}
	
	public void render() {
		m_dd.drawString(30, 30, "Car position: " + m_car.getWorldCenter(), Color3f.WHITE);
		if(g.getFramecounter() % 20 == 0) {
			updateVelocity();
		}
		m_dd.drawString(30, 54, "Car velocity: " + String.format("%.2f", velocity), Color3f.WHITE);
		m_world.drawDebugData();
	}
	
	private float updateVelocity() {
		long deltaTime = currTime - prevTime;
		Vec2 deltaPos = currPos.sub(prevPos);
		velocity = deltaPos.length() / deltaTime * 60 * 60;
		
		prevTime = currTime;
		prevPos = currPos;

		return velocity;
    }

	public void logic() {
		m_world.step(1 / 60f, 8, 3);
		m_camera.setCamera(m_car.getWorldCenter());

		currTime = g.getTime();
		currPos = m_car.getWorldCenter().clone();
	}

	@Override
	protected void zoomCamera() {
		if (Mouse.isInsideWindow()) {
			// Camera zooming
			int notches = Mouse.getDWheel();
			if (notches != 0) {
				Camera.ZoomType zoom = notches < 0 ? Camera.ZoomType.ZOOM_OUT
						: Camera.ZoomType.ZOOM_IN;
				m_camera.zoomToWorldPoint(m_car.getWorldCenter(), zoom);
			}
		}		
	}
	
	@Override
    public void input() {
		zoomCamera();
		// process motor
		if (Keyboard.isKeyDown(Keyboard.KEY_A)
		        && !Keyboard.isKeyDown(Keyboard.KEY_D)) {
			m_spring1.enableMotor(true);
			m_spring1.setMotorSpeed(m_speed);
			m_spring2.enableMotor(true);
			m_spring2.setMotorSpeed(m_speed);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)
		        && !Keyboard.isKeyDown(Keyboard.KEY_A)) {
			m_spring1.enableMotor(true);
			m_spring1.setMotorSpeed(-m_speed);
			m_spring2.enableMotor(true);
			m_spring2.setMotorSpeed(-m_speed);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			m_spring1.enableMotor(true);
			m_spring1.setMotorSpeed(0.0f);
			m_spring2.enableMotor(true);
			m_spring2.setMotorSpeed(0.0f);
		} else {
			m_spring1.enableMotor(false);
			m_spring2.enableMotor(false);
		}
		
		// process angular turn
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)
		        && !Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			m_car.applyAngularImpulse(-2.0f);
		} else if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)
		        && !Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			m_car.applyAngularImpulse(2.0f);			
		}

		for (Body body = m_world.getBodyList(); body != null; body = body.getNext()) {
			if (body.getType() == BodyType.DYNAMIC) {
				if (Mouse.isButtonDown(0)) {
					Vec2 mousePosition = new Vec2(Mouse.getX(), Mouse.getY());
					m_camera.getTransform().getScreenToWorld(mousePosition, mousePosition);
					Vec2 bodyPosition = body.getPosition();
					Vec2 force = mousePosition.sub(bodyPosition);
					body.applyForce(force.mul(5.0f), body.getPosition());
				}
			}
		}
    }

	public void setUpObjects() {
		super.setUpObjects();
		
		float hz = 7.0f;
		float zeta = 0.7f;

		// Car
		{
			PolygonShape chassis = new PolygonShape();
			Vec2 vertices[] = new Vec2[8];
			vertices[0] = new Vec2(-1.8f, -0.5f);
			vertices[1] = new Vec2(1.8f, -0.5f);
			vertices[2] = new Vec2(1.8f, 0.0f);
			vertices[3] = new Vec2(0.0f, 0.9f);
			vertices[4] = new Vec2(-1.15f, 0.9f);
			vertices[5] = new Vec2(-1.8f, 0.2f);
			chassis.set(vertices, 6);

			CircleShape circle = new CircleShape();
			circle.m_radius = 0.8f;

			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			bd.position.set(0.0f, 5.0f);
			m_car = m_world.createBody(bd);
			m_car.createFixture(chassis, 5.0f);

			FixtureDef fd = new FixtureDef();
			fd.shape = circle;
			fd.density = 0.3f;
			fd.friction = 1.0f;
			fd.restitution = 1.0f;

			bd.position.set(-1.2f, 4.0f);
			Body wheel1 = m_world.createBody(bd);
			wheel1.createFixture(fd);

			bd.position.set(1.2f, 4.05f);
			Body wheel2 = m_world.createBody(bd);
			wheel2.createFixture(fd);

			WheelJointDef jd = new WheelJointDef();
			Vec2 axis = new Vec2(0.0f, 1.0f);

			jd.initialize(m_car, wheel1, wheel1.getPosition(), axis);
			jd.motorSpeed = 0.0f;
			jd.maxMotorTorque = 50.0f;
			jd.enableMotor = true;
			jd.frequencyHz = hz;
			jd.dampingRatio = zeta;
			m_spring1 = (WheelJoint) m_world.createJoint(jd);

			jd.initialize(m_car, wheel2, wheel2.getPosition(), axis);
			jd.motorSpeed = 0.0f;
			jd.maxMotorTorque = 40.0f;
			jd.enableMotor = true;
			jd.frequencyHz = hz;
			jd.dampingRatio = zeta;
			m_spring2 = (WheelJoint) m_world.createJoint(jd);
		}

		createGround();
	}

	private void createGround() {
		Random rGenerator = new Random();
		float x = -10.0f, y = 1.0f, step = 0.2f, steepness = 0.1f;
	    BodyDef groundDef = new BodyDef();
		groundDef.position.set(0, 0);
		groundDef.type = BodyType.STATIC;
		Body ground = m_world.createBody(groundDef);
		FixtureDef groundFixture = new FixtureDef();
		groundFixture.density = 1;
		groundFixture.friction = 1.0f;
		groundFixture.restitution = 0.3f;
		for(int i = 0; i*step < 500.0f; i++) {
			PolygonShape groundShape = new PolygonShape();
	    	Vec2[] vertices = new Vec2[4];
	    	vertices[0] = new Vec2(x + (i-1)*step, 	y);
	    	vertices[1] = new Vec2(x + i*step, 		y += steepness * (rGenerator.nextInt(3) - 1));
	    	vertices[2] = new Vec2(x + i*step, 		-10.0f );
	    	vertices[3] = new Vec2(x + (i-1)*step, 	-10.0f );
			groundShape.set(vertices, 4);
			groundFixture.shape = groundShape;
			ground.createFixture(groundFixture);
	    }
    }
}

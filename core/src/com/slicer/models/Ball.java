package com.slicer.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.slicer.controller.GameInputController;
import com.slicer.world.GameWorld;


public class Ball implements ContactListener {

    private static float RADIUS = 0.7f;

    public Body box2dBall;
    private Vector2 lastPosition;
    private GameWorld gameWorld;

    public Ball(World world, BodyDef bodyDef, FixtureDef fixtureDef, GameWorld gw) {
        gameWorld = gw;
        lastPosition = new Vector2();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 0);

        CircleShape ballShape = new CircleShape();
        ballShape.setRadius(RADIUS);

        fixtureDef.shape = ballShape;
        fixtureDef.restitution = 1.2f;
        fixtureDef.density = 1f;
        fixtureDef.friction = .35f;

        box2dBall = world.createBody(bodyDef);
        box2dBall.createFixture(fixtureDef);

        ballShape.dispose();
    }

    public static float getRadius() {
        return RADIUS;
    }

    public void reset() {
        int velocityX = (int) (Math.random() * 10 + 4);
        int velocityY = (int) (Math.random() * 10 + 4);
        box2dBall.setTransform(0, 0, box2dBall.getAngle());
        box2dBall.setLinearVelocity(velocityX, velocityY);
        box2dBall.setActive(true);
        lastPosition.setZero();
    }

    public Vector2 getPosition() {
        if (box2dBall.isActive()) {
            lastPosition = box2dBall.getPosition();
        }

        return lastPosition;
    }

    /**
     * ContactListener *
     */

    @Override
    public void beginContact(Contact contact) {

        if (contact.getFixtureA().getShape() instanceof EdgeShape ||
                contact.getFixtureB().getShape() instanceof EdgeShape) {
            gameWorld.setGameState(GameWorld.GameState.GAME_OVER);
            GameInputController.setFlag(true);
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
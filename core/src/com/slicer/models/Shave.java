package com.slicer.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Shave {

    private Body box2dShave;
    private World box2dWorld;
    private BodyDef box2dBodyDef;
    private FixtureDef box2dFixtureDef;

    private Vector2 p1;
    private Vector2 p2;

    private EdgeShape edgeShape;

    public Shave(World box2dWorld, BodyDef box2dBodyDef, FixtureDef box2dFixtureDef) {
        this.box2dWorld = box2dWorld;
        this.box2dBodyDef = box2dBodyDef;
        this.box2dFixtureDef = box2dFixtureDef;
    }

    public void updateShave(Vector2 p1, Vector2 p2) {
        destroyBody();

        edgeShape = new EdgeShape();
        this.p1 = p1;
        this.p2 = p2;
        edgeShape.set(p1, p2);

        box2dBodyDef.type = BodyDef.BodyType.StaticBody;

        box2dFixtureDef.shape = edgeShape;
        box2dFixtureDef.friction = .35f;

        box2dShave = box2dWorld.createBody(box2dBodyDef);
        box2dShave.createFixture(box2dFixtureDef);

        edgeShape.dispose();
    }

    public Vector2[] getPositions() {
        if (p1 != null || p2 != null) {
            return new Vector2[]{p1, p2};
        }
        return null;
    }

    public void reset() {
        Vector2 zeroVector = new Vector2(-100, -100);
        updateShave(zeroVector, zeroVector);
    }

    public void doInactive() {
        updateShave(new Vector2(0, 0), new Vector2(0, 0));
        box2dShave.setActive(false);
    }

    private void destroyBody() {
        if (edgeShape != null)
            box2dWorld.destroyBody(box2dShave);
    }
}
package com.slicer.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Field {

    //Box2D
    private Body box2dField;
    private World box2dWorld;
    private BodyDef box2dBodyDef;
    private FixtureDef box2dFixtureDef;

    private Vector2[] vertices;
    private Vector2[] tmp;

    private ChainShape fieldShape;
    
    private boolean isSliced = false;

    public Field(final World world, BodyDef bodyDef, FixtureDef fixtureDef) {
        this.box2dWorld = world;
        this.box2dBodyDef = bodyDef;
        this.box2dFixtureDef = fixtureDef;
    }

    public void reset() {
        vertices = tmp;
        createBody();
    }

    public void createBody(Vector2[] vertices) {
        this.vertices = vertices;
        tmp = vertices;
        createBody();
    }

    public Vector2[] getVertices() {
        return vertices;
    }

    public void setVertices(Vector2[] vertices) {
        tmp = this.vertices;
        this.vertices = vertices;
    }

    public boolean isSliced() {
        return isSliced;
    }

    public void setIsSliced(boolean isSliced) {
        this.isSliced = isSliced;
    }

    private void createBody() {
        Body b = box2dField;

        box2dBodyDef.type = BodyDef.BodyType.StaticBody;
        box2dBodyDef.position.set(0, 0);

        fieldShape = new ChainShape();
        fieldShape.createChain(vertices);

        box2dFixtureDef.shape = fieldShape;
        box2dFixtureDef.restitution = 1f;
        box2dFixtureDef.density = 1f;
        box2dFixtureDef.friction = .35f;

        box2dField = box2dWorld.createBody(box2dBodyDef);
        box2dField.createFixture(box2dFixtureDef);

        fieldShape.dispose();

        destroyBody(b);
    }

    private void destroyBody(Body body) {
        if (body != null)
            box2dWorld.destroyBody(body);
    }
}
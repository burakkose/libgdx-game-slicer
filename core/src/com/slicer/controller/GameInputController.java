package com.slicer.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.Timer;
import com.slicer.models.Ball;
import com.slicer.models.Field;
import com.slicer.models.Shave;
import com.slicer.utils.AssetLoader;
import com.slicer.world.GameWorld;
import net.dermetfan.gdx.math.GeometryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameInputController extends InputAdapter {

    private static Vector2 p1 = new Vector2();
    private static Vector2 p2 = new Vector2();
    private static Vector3 tmp = new Vector3();

    private static boolean flag = false;

    private Ball ball;
    private Shave shave;
    private Field field;
    private World box2dWorld;
    private OrthographicCamera cam;

    private GameWorld gameWorld;

    private Map<Body, List<Vector2>> rayCastMap = new HashMap<Body, List<Vector2>>();

    private float score = 0;

    private final RayCastCallback callback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            //We can just slice the field and field is ChainShape
            if (!(fixture.getShape() instanceof ChainShape))
                return -1;

            Body body = fixture.getBody();
            List<Vector2> pointVec = rayCastMap.get(body);

            if (pointVec == null) {
                pointVec = new ArrayList<Vector2>();
                rayCastMap.put(body, pointVec);
            }

            if (!pointVec.isEmpty() && !pointVec.get(0).equals(point)) {
                pointVec.add(point.cpy());
                splitObj(body, pointVec);
            } else {
                pointVec.add(point.cpy());
            }

            return -1;
        }
    };

    public GameInputController(GameWorld gameWorld, OrthographicCamera cam) {
        this.cam = cam;
        this.gameWorld = gameWorld;
        this.ball = gameWorld.getBall();
        this.shave = gameWorld.getShave();
        this.field = gameWorld.getField();
        this.box2dWorld = gameWorld.getBox2dWorld();
    }

    static private float det(Vector2 p1, Vector2 p2, Vector2 p3) {
        return p1.x * p2.y + p2.x * p3.y + p3.x * p1.y - p1.y * p2.x - p2.y * p3.x - p3.y * p1.x;
    }

    static public FloatArray arrayToFloatArray(Array<Vector2> vector2Array) {
        FloatArray f = new FloatArray(false, vector2Array.size * 2);
        for (Vector2 vec : vector2Array) {
            f.addAll(vec.x, vec.y);
        }
        return f;
    }

    public static void setFlag(boolean flag) {
        GameInputController.flag = flag;
    }

    private void splitObj(Body sliceBody, List<Vector2> splitedPoints) {
        AssetLoader.play(AssetLoader.slice);

        Vector2 a = splitedPoints.get(0);
        Vector2 b = splitedPoints.get(1);

        Array<Vector2> shape1Vertices = new Array<Vector2>();
        shape1Vertices.addAll(a, b);
        Array<Vector2> shape2Vertices = new Array<Vector2>();
        shape2Vertices.addAll(a, b);

        for (Vector2 vec : field.getVertices()) {
            float determinant = det(a, b, vec);
            if (determinant > 0) {
                if (!shape1Vertices.contains(vec, false))
                    shape1Vertices.add(vec);

            } else if (determinant < 0) {
                if (!shape2Vertices.contains(vec, false))
                    shape2Vertices.add(vec);
            }
        }

        GeometryUtils.arrangeClockwise(shape1Vertices);
        GeometryUtils.arrangeClockwise(shape2Vertices);

        FloatArray shape1 = arrayToFloatArray(shape1Vertices);
        FloatArray shape2 = arrayToFloatArray(shape2Vertices);
        FloatArray newShape;

        float shape1Area = calculateArea(shape1);
        float shape2Area = calculateArea(shape2);

        box2dWorld.destroyBody(sliceBody);
        ball.box2dBall.setActive(false);

        float splicedArea;

        if (det(a, b, ball.getPosition()) > 0) {
            splicedArea = shape2Area * 100 / (shape1Area + shape2Area);
            newShape = shape1;
        } else {
            splicedArea = shape1Area * 100 / (shape1Area + shape2Area);
            newShape = shape2;
        }

        ///fix here
        field.setVertices((Vector2[]) GeometryUtils.toVector2Array(newShape).toArray(Vector2.class));

        field.setIsSliced(true);
        // setting the properties of the two newly created shapes
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(sliceBody.getPosition());
        FixtureDef fixtureDef = new FixtureDef();

        // creating the first shape
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(newShape.toArray());
        fixtureDef.shape = polygonShape;

        sliceBody = box2dWorld.createBody(bodyDef);
        sliceBody.createFixture(fixtureDef);

        score = Math.abs(splicedArea);
        gameWorld.setTimeFlag(true);
        Gdx.input.setInputProcessor(null);
        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {
                flag = true;
                if (Math.ceil(score) < 60) {
                    gameWorld.setGameState(GameWorld.GameState.GAME_OVER);
                } else {
                    gameWorld.setGameState(GameWorld.GameState.NEXT_LEVEL);
                }
                field.setIsSliced(false);
            }

        }, 1);
    }

    public float getScore() {
        return score;
    }

    public void resetScore() {
        this.score = 0;
    }

    private float calculateArea(FloatArray arr) {

        if (GeometryUtils.isConvex(arr))
            return GeometryUtils.polygonArea(arr);

        EarClippingTriangulator earClippingTriangulator = new EarClippingTriangulator();
        ShortArray triangles = earClippingTriangulator.computeTriangles(arr);

        float area = 0;

        for (int i = 0; i < triangles.size; i += 3) {
            int v1 = triangles.get(i);
            int v2 = triangles.get(i + 1);
            int v3 = triangles.get(i + 2);

            FloatArray f = new FloatArray(false, 6);

            f.add(arr.get(v1));
            f.add(arr.get(v1 + 1));
            f.add(arr.get(v2));
            f.add(arr.get(v2 + 1));
            f.add(arr.get(v3));
            f.add(arr.get(v3 + 1));

            area += GeometryUtils.polygonArea(f);
        }

        return area;
    }

    /**
     * InputProcessor *
     */

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        box2dWorld.setContactListener(ball);
        tmp.set(screenX, screenY, 0);
        cam.unproject(tmp);
        p1.set(tmp.x, tmp.y);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        tmp.set(screenX, screenY, 0);
        cam.unproject(tmp);
        p2.set(tmp.x, tmp.y);
        shave.updateShave(p1, p2);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        box2dWorld.setContactListener(null);
        if (!flag && !p1.equals(p2)) {
            box2dWorld.rayCast(callback, p1, p2);
        }
        //box2dWorld.rayCast(callback, p2, p1);
        flag = false;
        p1.setZero();
        p2.setZero();
        tmp.setZero();
        rayCastMap.clear();
        shave.doInactive();
        return true;
    }
}
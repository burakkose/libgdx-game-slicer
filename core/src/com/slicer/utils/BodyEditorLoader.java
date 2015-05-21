package com.slicer.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.*;

public class BodyEditorLoader {

    // Model
    private final Model model;

    public BodyEditorLoader(FileHandle file) {
        if (file == null) throw new NullPointerException("file is null");
        model = readJson(file.readString());
    }

    public Vector2[] getVertices(String name, float scale) {
        RigidBodyModel rbModel = model.rigidBodies.get(name);
        if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

        List<Vector2> vertices = new LinkedList<Vector2>();

        for (int i = 0, n = rbModel.polygons.size(); i < n; i++) {
            PolygonModel polygon = rbModel.polygons.get(i);

            for (Vector2 vec : polygon.vertices) {
                vertices.add(vec.scl(scale));
            }

        }
        vertices.add(vertices.get(0));
        return vertices.toArray(new Vector2[vertices.size()]);
    }

    public int getLevelSize() {
        return model.rigidBodies.size();
    }

    /**
     * Gets the image path attached to the given name.
     */
    public String getImagePath(String name) {
        RigidBodyModel rbModel = model.rigidBodies.get(name);
        if (rbModel == null) throw new RuntimeException("Name '" + name + "' was not found.");

        return rbModel.imagePath;
    }

    // -------------------------------------------------------------------------
    // Json Models
    // -------------------------------------------------------------------------

    private Model readJson(String str) {
        Model m = new Model();

        JsonValue map = new JsonReader().parse(str);

        JsonValue bodyElem = map.getChild("rigidBodies");
        for (; bodyElem != null; bodyElem = bodyElem.next()) {
            RigidBodyModel rbModel = readRigidBody(bodyElem);
            m.rigidBodies.put(rbModel.name, rbModel);
        }

        return m;
    }

    private RigidBodyModel readRigidBody(JsonValue bodyElem) {
        RigidBodyModel rbModel = new RigidBodyModel();
        rbModel.name = bodyElem.getString("name");
        rbModel.imagePath = bodyElem.getString("imagePath");

        JsonValue originElem = bodyElem.get("origin");
        rbModel.origin.x = originElem.getFloat("x");
        rbModel.origin.y = originElem.getFloat("y");

        // polygons
        JsonValue polygonsElem = bodyElem.getChild("polygons");
        for (; polygonsElem != null; polygonsElem = polygonsElem.next()) {

            PolygonModel polygon = new PolygonModel();
            rbModel.polygons.add(polygon);

            JsonValue vertexElem = polygonsElem.child();
            for (; vertexElem != null; vertexElem = vertexElem.next()) {
                float x = vertexElem.getFloat("x");
                float y = vertexElem.getFloat("y");
                polygon.vertices.add(new Vector2(x, y));
            }
        }

        // circles
        JsonValue circleElem = bodyElem.getChild("circles");

        for (; circleElem != null; circleElem = circleElem.next()) {
            CircleModel circle = new CircleModel();
            rbModel.circles.add(circle);

            circle.center.x = circleElem.getFloat("cx");
            circle.center.y = circleElem.getFloat("cy");
            circle.radius = circleElem.getFloat("r");
        }

        return rbModel;
    }

    // -------------------------------------------------------------------------
    // Json reading process
    // -------------------------------------------------------------------------

    public static class Model {
        public final Map<String, RigidBodyModel> rigidBodies = new HashMap<String, RigidBodyModel>();
    }

    public static class RigidBodyModel {
        public final Vector2 origin = new Vector2();
        public final List<PolygonModel> polygons = new ArrayList<PolygonModel>();
        public final List<CircleModel> circles = new ArrayList<CircleModel>();
        public String name;
        public String imagePath;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    public static class PolygonModel {
        public final List<Vector2> vertices = new ArrayList<Vector2>();
    }

    public static class CircleModel {
        public final Vector2 center = new Vector2();
        public float radius;
    }
}

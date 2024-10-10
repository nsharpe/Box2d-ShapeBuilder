package com.sharpe.shape.builder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.graphics.Texture;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoredShapeLoaderTest {

    AssetManager assetManager;

    private String directory = "src/test/resources/com/sharpe/shape/builder/";

    @AfterEach
    public void after() {
        assetManager.dispose();
    }

    @BeforeEach
    public void before() {
        assetManager = new AssetManager();

        Gdx.files = new Lwjgl3Files();
    }

    // Test will not work as loading textures require a libgdx application to be running
    @Ignore
    @Test
    public void testLoad() {

        new Texture(directory + "test.png");

        assetManager.setLoader(FixtureWithImage.class, new StoredShapeLoader(assetManager.getFileHandleResolver()));

        assetManager.load(directory + "test.json", FixtureWithImage.class);
        assetManager.finishLoading();

        FixtureWithImage fixtureWithImage = assetManager.get(directory + "test.json", FixtureWithImage.class);

        assertNotNull(fixtureWithImage);
        assertEquals(2, fixtureWithImage.getShapeScaffold().size());

        assertEquals(1f, fixtureWithImage.getAnchor().x);
        assertEquals(-1f, fixtureWithImage.getAnchor().y);

        ShapeScaffold shapeScaffold = fixtureWithImage.getShapeScaffold().get("main");
        assertNotNull(shapeScaffold);

        assertEquals(4, shapeScaffold.shapeVectors().size());

        assertEquals(-0.5f, shapeScaffold.shapeVectors().get(0).x);
        assertEquals(0f, shapeScaffold.shapeVectors().get(0).y);

        assertEquals(0.5f, shapeScaffold.shapeVectors().get(1).x);
        assertEquals(0f, shapeScaffold.shapeVectors().get(1).y);

        assertEquals(0.5f, shapeScaffold.shapeVectors().get(2).x);
        assertEquals(0.5f, shapeScaffold.shapeVectors().get(2).y);

        assertEquals(-0.5f, shapeScaffold.shapeVectors().get(3).x);
        assertEquals(0.5f, shapeScaffold.shapeVectors().get(3).y);
        assertFalse(shapeScaffold.isEdge());

        //Sensor Test
        shapeScaffold = fixtureWithImage.getShapeScaffold().get("sensor");
        assertNotNull(shapeScaffold);

        assertEquals(2, shapeScaffold.shapeVectors().size());

        assertEquals(0.6f, shapeScaffold.shapeVectors().get(0).x);
        assertEquals(0f, shapeScaffold.shapeVectors().get(0).y);

        assertEquals(-0.6f, shapeScaffold.shapeVectors().get(1).x);
        assertEquals(0f, shapeScaffold.shapeVectors().get(1).y);

        assertTrue(shapeScaffold.isEdge());
    }

}

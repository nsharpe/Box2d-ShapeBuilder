package com.sharpe.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TestScreen implements Screen, Serializable {

    @Serial
    private static final long serialVersionUID = -1813174300898045084L;

    @Getter
    private transient World world;
    private transient Box2DDebugRenderer debugRenderer;

    @Getter
    private transient Viewport viewport;
    private transient OrthographicCamera camera;

    private transient SpriteBatch spriteBatch;

    @Getter
    private transient AssetManager assetManager;

    @Setter
    private transient Body bodyToCenterOn;
    private final static int VELOCITY_ITERATIONS = 8;
    private final static int POSITION_ITERATION = 3;

    private transient final Array<Body> bodies = new Array<>(4000);

    @Setter
    @Getter
    private transient boolean debugEnabled = false;

    public TestScreen(){
        instantiateObjects();
    }

    @Override
    public void show() {

        // Connect and get objects ready
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    private void loadAssets() {
    }

    private void instantiateObjects() {
        assetManager = new AssetManager();
        loadAssets();

        camera = new OrthographicCamera();
        viewport = new ScalingViewport(Scaling.fill,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            camera);
        spriteBatch = new SpriteBatch();

        world = new World(new Vector2(0, 0), true);

        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void render(float delta) {

        step(delta);

        long start = System.currentTimeMillis();
        draw();
        Gdx.app.debug("WorldEntityScreen", "world.drawMS:" + (System.currentTimeMillis() - start));
    }

    public void step(float delta) {
        long start = System.currentTimeMillis();
        world.step(delta, VELOCITY_ITERATIONS, POSITION_ITERATION);
        Gdx.app.debug("WorldEntityScreen", "step.world.stepMS:" + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        Gdx.app.debug("WorldEntityScreen", "step.actionIterator.stepMS:" + (System.currentTimeMillis() - start));
    }

    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.getBodies(bodies);

        if (bodyToCenterOn != null) {
            camera.position.set(bodyToCenterOn.getPosition(), 0);
        }
        spriteBatch.getProjectionMatrix().set(camera.combined);

        camera.update();
        viewport.apply();

        spriteBatch.begin();

        spriteBatch.end();

        long start = System.currentTimeMillis();
        debugRenderer.render(world, camera.combined);

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {

        if (world == null) {
            return;
        }

        debugRenderer.dispose();
        Gdx.app.debug("WorldEntityScreen", "DebugRenderer Disposed");

        spriteBatch.dispose();
        Gdx.app.debug("WorldEntityScreen", "spriteBatch Disposed");

        assetManager.dispose();
        Gdx.app.debug("WorldEntityScreen", "assetManager Disposed");

        world.dispose();
        Gdx.app.debug("WorldEntityScreen", "World Disposed");

    }

    public void zoom(int diff) {
        final float zoomMin = 0.001f;

        if (diff > 0) {
            camera.zoom *= 2;
        }
        if (diff < 0) {
            camera.zoom = camera.zoom / 2;
        }

        if (camera.zoom < zoomMin) {
            camera.zoom = zoomMin;
        }

        final int zoomMax = 10;
        if (camera.zoom > zoomMax) {
            camera.zoom = zoomMax;
        }

        camera.update();

    }
}

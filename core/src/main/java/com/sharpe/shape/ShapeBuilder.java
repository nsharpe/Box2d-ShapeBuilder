package com.sharpe.shape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShapeBuilder implements Screen, InputProcessor {

    private final List<ShapeBeingBuilt> shapesBeingBuilt = new ArrayList<>();
    private ShapeBeingBuilt currentShapeBeingBuilt;
    private Vector2 selectedVector;
    private Vector2 currentMousePosition;

    private Viewport viewport;
    private Camera camera;
    private Stage menu;
    private SpriteBatch spriteBatch;
    private Texture currentTexture;
    private ShapeRenderer shapeRenderer;
    private TextureAtlas atlas;
    private Skin skin;

    Color backgroudColor = Color.DARK_GRAY;

    private static final float POINT_WORLD_SIZE=1f;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ScalingViewport(Scaling.fill,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            camera);

        viewport.setWorldSize(500,500);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        menu = new Stage(viewport, spriteBatch);
        this.shapeRenderer = new ShapeRenderer();
        add(new ShapeBeingBuilt());

        atlas = new TextureAtlas("ui/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"),atlas);

        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.bottom();
        mainTable.right();

        TextButton exitButton = new TextButton("Exit", skin);

        exitButton.addListener(clickListener(()->Gdx.app.exit()));

        mainTable.add(exitButton);

        menu.addActor(mainTable);

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(backgroudColor.r,backgroudColor.g,backgroudColor.b,backgroudColor.a);
        viewport.apply();
        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);


        shapesBeingBuilt.forEach(this::render);

        menu.act(delta);
        menu.draw();

    }

    private void render(ShapeBeingBuilt sbb){
        if(sbb.shapeVectors.isEmpty()){
            return;
        }
        Vector2 previous = null;

        for(Vector2 vector2: sbb.shapeVectors){
            drawVectorAsPosition(vector2);
            if(previous!=null){
                drawLine(previous,vector2);
            }
            previous = vector2;
        }

        drawLine(sbb.shapeVectors().getFirst(), sbb.shapeVectors().getLast());


    }

    private void drawVectorAsPosition(Vector2 vector2){
        float size = POINT_WORLD_SIZE;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(vector2.x-size/2,vector2.y-size/2,size,size);
        shapeRenderer.end();
    }

    private void drawLine(Vector2 start, Vector2 end){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(start,end);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
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
        if (currentTexture != null) {
            currentTexture.dispose();
        }
        menu.dispose();
        shapeRenderer.dispose();
        spriteBatch.dispose();
        skin.dispose();
        atlas.dispose();

    }

    @Override
    public boolean keyDown(int keycode) {
        if (menu.keyDown(keycode)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (menu.keyUp(keycode)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (menu.keyTyped(character)) {
            return true;
        }

        if(character == 'n'){
            if(currentShapeBeingBuilt!=null){
                selectedVector = new Vector2(currentMousePosition);
                currentShapeBeingBuilt.shapeVectors.add(selectedVector);
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (menu.touchDown(screenX, screenY, pointer, button)) {
            return true;
        }
        if(Input.Buttons.LEFT == button) {
            if (this.selectedVector != null) {
                selectedVector = null;
                return true;
            } else {
                selectedVector = getVector((float) screenX, (float) screenY)
                    .orElse(null);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (menu.touchUp(screenX, screenY, pointer, button)) {
            return true;
        }
        currentMousePosition = viewport.unproject(new Vector2(screenX,screenY));
        if(Input.Buttons.LEFT == button) {
            if (this.selectedVector != null) {
                selectedVector.set(currentMousePosition);
                selectedVector = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        if (menu.touchCancelled(screenX, screenY, pointer, button)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (menu.touchDragged(screenX, screenY, pointer)) {
            return true;
        }
        currentMousePosition = viewport.unproject(new Vector2(screenX,screenY));
        if(selectedVector!=null){
            selectedVector.set(currentMousePosition);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (menu.mouseMoved(screenX, screenY)) {
            return true;
        }
        currentMousePosition = viewport.unproject(new Vector2(screenX,screenY));
        if(selectedVector!=null){
            selectedVector.set(currentMousePosition);
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (this.menu.scrolled(amountX, amountY)) {
            return true;
        }
        return false;
    }

    private void loadTexture(String assetLocation) {
        if (currentTexture != null) {
            currentTexture.dispose();
        }

        currentTexture = new Texture(assetLocation);
    }

    private Optional<Vector2> getVector(final float screenX, final float screenY) {
        if(currentShapeBeingBuilt == null){
            return Optional.empty();
        }
        Vector2 screenCoordinates = viewport.unproject(new Vector2(screenX , screenY ));

        float size = POINT_WORLD_SIZE;

        return currentShapeBeingBuilt.shapeVectors.stream()
            .filter(x->vertexInBound(x,screenCoordinates.x-size,screenCoordinates.x+size,
                screenCoordinates.y-size,screenCoordinates.y+size))
            .findAny();
    }

    private void add(ShapeBeingBuilt shapeBeingBuilt){
        this.currentShapeBeingBuilt = shapeBeingBuilt;
        this.shapesBeingBuilt.add(shapeBeingBuilt);
    }

    private static boolean vertexInBound(Vector2 theVector, float minX, float maxX, float minY, float maxY) {
        return theVector.x >= minX && theVector.x <= maxX && theVector.y >= minY && theVector.y <= maxY;
    }

    private record ShapeBeingBuilt(List<Vector2> shapeVectors) {

        private ShapeBeingBuilt() {
            this(new ArrayList<>());
        }
    }

    public static ClickListener clickListener(Runnable runnable){
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                runnable.run();
            }
        };
    }

}

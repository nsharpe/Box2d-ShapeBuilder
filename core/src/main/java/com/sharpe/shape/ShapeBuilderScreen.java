package com.sharpe.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShapeBuilderScreen implements Screen, InputProcessor {

    private final List<ShapeBeingBuilt> shapesBeingBuilt = new ArrayList<>();
    private ShapeBeingBuilt currentShapeBeingBuilt;
    private Vector2 selectedVector;
    private Vector2 currentMousePosition;

    private Viewport viewport;
    private Camera camera;
    private Texture currentTexture;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private InputProcessor wrappedInputProcessor;

    private Color backgroudColor = Color.DARK_GRAY;

    private Sprite sprite;

    private static final float POINT_WORLD_SIZE=.015f;

    private static final float WORLD_SIZE = 500;

    public ShapeBuilderScreen(InputProcessor wrappedInputProcessor) {
        this.wrappedInputProcessor = wrappedInputProcessor;
    }

    @Override
    public void show() {

        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            camera);
        viewport.setWorldHeight(2);
        viewport.setWorldWidth(2);

        spriteBatch = new SpriteBatch();

        this.shapeRenderer = new ShapeRenderer();
        add(new ShapeBeingBuilt());

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(backgroudColor.r,backgroudColor.g,backgroudColor.b,backgroudColor.a);
        viewport.apply();

        camera.position.x = viewport.getWorldWidth()/2;
        camera.position.y = viewport.getWorldHeight()/2;;
        camera.update();

        spriteBatch.getProjectionMatrix().set(camera.combined);

        shapeRenderer.setProjectionMatrix(camera.combined);

        if(sprite!=null) {
            spriteBatch.begin();

            sprite.draw(spriteBatch);

            spriteBatch.end();
        }

        shapesBeingBuilt.forEach(this::render);

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
        camera.position.set(0,0, 0);
        viewport.update(width,height);
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
        if (currentTexture != null) {
            currentTexture.dispose();
        }
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (wrappedInputProcessor.keyDown(keycode)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (wrappedInputProcessor.keyUp(keycode)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (wrappedInputProcessor.keyTyped(character)) {
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
        if (wrappedInputProcessor.touchDown(screenX, screenY, pointer, button)) {
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
        if (wrappedInputProcessor.touchUp(screenX, screenY, pointer, button)) {
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
        if (wrappedInputProcessor.touchCancelled(screenX, screenY, pointer, button)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (wrappedInputProcessor.touchDragged(screenX, screenY, pointer)) {
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
        if (wrappedInputProcessor.mouseMoved(screenX, screenY)) {
            return true;
        }
        currentMousePosition = viewport.unproject(new Vector2(screenX,screenY));
        System.out.println(currentMousePosition);
        if(selectedVector!=null){
            selectedVector.set(currentMousePosition);
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (this.wrappedInputProcessor.scrolled(amountX, amountY)) {
            return true;
        }
        return false;
    }

    public void loadTexture(File file) {
        if (currentTexture != null) {
            currentTexture.dispose();
        }

        currentTexture = new Texture(new FileHandle(file));
        float height = currentTexture.getHeight();
        float width = currentTexture.getWidth();

        if(height > width){
            width = height/width;
            height = 1;
        }else{
            height = width/height;
            width = 1;
        }

        sprite = new Sprite(currentTexture);

        sprite.setSize(width,height);
        sprite.setOrigin(0.5f,.5f);
        sprite.setPosition(0.5f,0.5f);
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

}

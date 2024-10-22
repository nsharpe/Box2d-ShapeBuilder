package com.sharpe.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sharpe.shape.builder.FixtureWithImage;
import com.sharpe.shape.builder.ShapeScaffold;
import com.sharpe.shape.serialization.Vector2JsonDeserializer;
import com.sharpe.shape.serialization.Vector2JsonSerializer;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShapeBuilderScreen implements Screen, InputProcessor {

    private FixtureWithImage fixtureWithImage = new FixtureWithImage();
    @Setter
    private ShapeScaffold currentShapeScaffold;
    private Vector2 selectedVector;
    private Vector2 currentMousePosition;

    private AssetManager assetManager;

    private Viewport viewport;
    private Camera camera;
    private Texture currentTexture;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private InputProcessor wrappedInputProcessor;

    private Color backgroudColor = Color.DARK_GRAY;

    private Sprite sprite;
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final float POINT_WORLD_SIZE=.015f;

    public ShapeBuilderScreen(InputProcessor wrappedInputProcessor) {
        this.wrappedInputProcessor = wrappedInputProcessor;

        SimpleModule module = new SimpleModule();
        module.addSerializer(Vector2.class, new Vector2JsonSerializer());
        module.addDeserializer(Vector2.class,new Vector2JsonDeserializer());

        objectMapper.registerModule(module);
    }

    @Override
    public void show() {
        assetManager = new AssetManager();
        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            camera);
        viewport.setWorldHeight(1.25f);
        viewport.setWorldWidth(1.25f);

        spriteBatch = new SpriteBatch();

        this.shapeRenderer = new ShapeRenderer();
        add("main",new ShapeScaffold(false));

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(backgroudColor.r,backgroudColor.g,backgroudColor.b,backgroudColor.a);
        viewport.apply();

        camera.position.x = 0;
        camera.position.y = 0;
        camera.update();

        spriteBatch.getProjectionMatrix().set(camera.combined);

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(-.5f,-0.5f,-0.5f,0.5f);
        shapeRenderer.line(-0.5f,-0.5f,0.5f,-0.5f);
        shapeRenderer.line(0.5f,0.5f,0.5f,-0.5f);
        shapeRenderer.line(0.5f,0.5f,-0.5f,0.5f);
        shapeRenderer.end();

        if(sprite!=null) {
            spriteBatch.begin();

            sprite.draw(spriteBatch);

            spriteBatch.end();
        }

        fixtureWithImage.getShapeScaffold().values().forEach(this::render);
        drawVectorAsPosition(fixtureWithImage.getAnchor(),Color.RED);

    }

    private void render(ShapeScaffold sbb){
        if(sbb.shapeVectors().isEmpty()){
            return;
        }
        Vector2 previous = null;

        for(Vector2 vector2: sbb.shapeVectors()){
            drawVectorAsPosition(vector2);
            if(previous!=null ){
                drawLine(previous,vector2);
            }
            previous = vector2;
        }

        if(!sbb.isEdge()) {
            drawLine(sbb.shapeVectors().getFirst(), sbb.shapeVectors().getLast());
        }

    }

    private void drawVectorAsPosition(Vector2 vector2){
        drawVectorAsPosition(vector2,Color.GREEN);
    }

    private void drawVectorAsPosition(Vector2 vector2, Color color){
        float size = POINT_WORLD_SIZE;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
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
        assetManager.dispose();
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
            if(currentShapeScaffold !=null){
                selectedVector = new Vector2(currentMousePosition);
                currentShapeScaffold.shapeVectors().add(selectedVector);
                return true;
            }
        }

        if(character == '\b'){
            if(currentShapeScaffold !=null && selectedVector != null){
                currentShapeScaffold.shapeVectors().remove(selectedVector);
                selectedVector = null;
                return true;
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
                    .or(()-> atMousePosition(this.fixtureWithImage.getAnchor(),screenX,screenY) ? Optional.of(fixtureWithImage.getAnchor()): Optional.empty())
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

    public void save(File file){
        if(!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("fixture")){
            RuntimeException rte = new IllegalStateException("Must be saved to a fixture file");
            Gdx.app.error("ShapeBuilderScreen","Error loading shape", rte);
            throw rte;
        }
        try {
            fixtureWithImage.setImageLocation(file.toPath().getParent().relativize(new File(fixtureWithImage.getImageLocation()).toPath()).toString());
            objectMapper.writeValue(file, fixtureWithImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadTexture(File file) {
        if (currentTexture != null) {
            currentTexture.dispose();
        }

        fixtureWithImage.setImageLocation(file.getAbsolutePath());

       setCurrentTexture( new Texture(new FileHandle(file)));

    }

    public void loadFixture(File file){
        try {
            fixtureWithImage = objectMapper.readValue(file, FixtureWithImage.class);
            Path textureFile = file.toPath().resolveSibling(fixtureWithImage.getImageLocation());
            System.out.println("textureFile: "+textureFile);
            assetManager.load(textureFile.toString(), Texture.class);
            assetManager.finishLoading();
            setCurrentTexture( assetManager.get(textureFile.toString(),Texture.class) );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCurrentTexture(Texture texture){
        if(currentTexture != null){
            currentTexture.dispose();
        }
        currentTexture = texture;
        float height = currentTexture.getHeight();
        float width = currentTexture.getWidth();

        if(height > width){
            width = width/height;
            height = 1;
        }else{
            height = height/width;
            width = 1;
        }

        sprite = new Sprite(currentTexture);

        sprite.setOrigin(height/2f,width/2f);
        sprite.setBounds(-width/2f,-height/2f,width,height);
        sprite.setOrigin(height/2f,width/2f);
    }

    private Optional<Vector2> getVector(final float screenX, final float screenY) {
        if(currentShapeScaffold == null){
            return Optional.empty();
        }

        return currentShapeScaffold.shapeVectors().stream()
            .filter(x-> atMousePosition(x,screenX,screenY))
            .findAny();
    }

    public boolean atMousePosition(Vector2 vector2, float screenX, float screenY){
        Vector2 screenCoordinates = viewport.unproject(new Vector2(screenX , screenY ));
        float size = POINT_WORLD_SIZE;
        return vertexInBound(vector2,screenCoordinates.x-size,screenCoordinates.x+size,
            screenCoordinates.y-size,screenCoordinates.y+size);
    }

    public void add(String name, ShapeScaffold shapeScaffold){
        this.currentShapeScaffold = shapeScaffold;
        this.fixtureWithImage.getShapeScaffold().put(name,shapeScaffold);
    }

    public Map<String,ShapeScaffold> getShapeScaffolds(){
        return fixtureWithImage.getShapeScaffold();
    }

    private static boolean vertexInBound(Vector2 theVector, float minX, float maxX, float minY, float maxY) {
        return theVector.x >= minX && theVector.x <= maxX && theVector.y >= minY && theVector.y <= maxY;
    }

}

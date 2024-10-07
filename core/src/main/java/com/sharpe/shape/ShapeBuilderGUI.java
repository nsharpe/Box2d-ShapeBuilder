package com.sharpe.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserListener;
import com.sharpe.libgdx.file.SinglePathChooserListener;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;

public class ShapeBuilderGUI implements Screen {

    private ShapeBuilderScreen shapeBuilderScreen;

    private Viewport viewport;
    private Camera camera;
    private Stage menu;
    private SpriteBatch spriteBatch;

    private TextureAtlas atlas;
    private Skin skin;

    private Table controlTable;

    private FileChooser bodySaver;
    private FileChooser imageLoader;
    private Preferences preferences;

    @Override
    public void show() {
        VisUI.load();


        preferences = Gdx.app.getPreferences("ShapeBuilderScreen");


        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ScalingViewport(Scaling.fill,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            camera);

        bodySaver = new FileChooser(FileChooser.Mode.SAVE);
        imageLoader = new FileChooser(FileChooser.Mode.OPEN);
        String startingDirectory = preferences.getString("startingDirectory");
        if(startingDirectory != null) {
            imageLoader.setDirectory(startingDirectory);
            bodySaver.setDirectory(startingDirectory);
        }
        imageLoader.setFileFilter((file)->
                    file.canRead()
                    && !file.isHidden()
                    && (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("png")|| file.isDirectory()));

        imageLoader.setMultiSelectionEnabled(false);
        imageLoader.setListener(SinglePathChooserListener.of(x->
            x.ifPresent(this::imageLoader)
        ));

        bodySaver.setListener(SinglePathChooserListener.of(x->x.ifPresent(this::saveShape)));

        menu = new Stage(viewport, spriteBatch);

        atlas = new TextureAtlas("ui/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

        //Create Table
        controlTable = new Table();
        controlTable.setTransform(true);
        controlTable.padRight(600f);
        controlTable.padBottom(300f);
        controlTable.setScale(1.3f);
        //Set table to fill stage
        controlTable.setFillParent(true);

        TextButton loadImage = textButton("Load Image", this::openLoadImage);
        TextButton saveButton = textButton("Save", this::openSaveBody);
        TextButton exitButton = textButton("Exit",() -> Gdx.app.exit());

        controlTable.add(loadImage);
        controlTable.row();
        controlTable.add(saveButton);
        controlTable.row();
        controlTable.add(exitButton);

        controlTable.setTransform(true);

        //Set alignment of contents in the table.
        controlTable.bottom();
        controlTable.right();

        menu.addActor(controlTable);

        shapeBuilderScreen = new ShapeBuilderScreen(menu);

        shapeBuilderScreen.show();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void openSaveBody(){
        menu.addActor(bodySaver);
    }

    private void openLoadImage(){
        menu.addActor(imageLoader);
    }

    private void saveShape(Path path){
        preferences.putString("startingDirectory",path.getParent().toString());
        preferences.flush();
        if(!path.toString().endsWith(".json")){
            path = new File(path.toString()+".json").toPath();
        }
        try {
            this.shapeBuilderScreen.save(path.toFile());
        }catch (Exception e){
            Dialog dialog = new Dialog("Warning", skin, "dialog") {
                public void result(Object obj) {
                    // Ignored;
                }
            };
            dialog.text("Could not save due to error");
            dialog.button("Confirm", true); //sends "true" as the result
            dialog.key(Input.Keys.ENTER, true); //sends "true" when the ENTER key is pressed
            dialog.show(menu);
        }
    }

    private void imageLoader(Path path){
        preferences.putString("startingDirectory",path.getParent().toString());
        preferences.flush();
        this.shapeBuilderScreen.loadTexture(path.toFile());
    }

    @Override
    public void render(float delta) {
        shapeBuilderScreen.render(delta);
        viewport.apply();
        camera.update();
        menu.act(delta);
        menu.draw();
    }

    @Override
    public void resize(int width, int height) {

        shapeBuilderScreen.resize(width, height);
        viewport.update(width,height);
        camera.update();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 1);
        spriteBatch.getProjectionMatrix().set(camera.combined);
    }

    @Override
    public void pause() {
        shapeBuilderScreen.pause();
    }

    @Override
    public void resume() {
        shapeBuilderScreen.resume();
    }

    @Override
    public void hide() {
        shapeBuilderScreen.hide();
        dispose();
    }

    @Override
    public void dispose() {
        this.menu.dispose();
        this.atlas.dispose();
        this.spriteBatch.dispose();
        this.skin.dispose();

        VisUI.dispose();
    }

    public TextButton textButton(String text, Runnable runnable){
        TextButton button = new TextButton(text, skin);
        button.setTransform(true);
        button.setScale(2);
        button.addListener(clickListener(runnable));
        return button;
    }



    public static ClickListener clickListener(Runnable runnable) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                runnable.run();
            }
        };
    }
}

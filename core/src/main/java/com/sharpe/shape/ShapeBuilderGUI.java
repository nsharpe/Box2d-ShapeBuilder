package com.sharpe.shape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ShapeBuilderGUI implements Screen {

    private ShapeBuilderScreen shapeBuilderScreen;

    private Viewport viewport;
    private Camera camera;
    private Stage menu;
    private SpriteBatch spriteBatch;

    private TextureAtlas atlas;
    private Skin skin;

    private Table controlTable;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ScalingViewport(Scaling.fill,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            camera);

        viewport.setWorldSize(Gdx.graphics.getWidth()/3f,Gdx.graphics.getHeight()/3f);

        menu = new Stage(viewport, spriteBatch);

        atlas = new TextureAtlas("ui/uiskin.atlas");
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

        //Create Table
        controlTable = new Table();
        //Set table to fill stage
        controlTable.setFillParent(true);

        TextButton exitButton = textButton("exit",() -> Gdx.app.exit());

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
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 1);
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
    }

    public TextButton textButton(String text, Runnable runnable){
        TextButton button = new TextButton("Exit", skin);
        button.setTransform(true);

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

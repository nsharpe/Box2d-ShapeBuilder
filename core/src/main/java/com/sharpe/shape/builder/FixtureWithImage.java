package com.sharpe.shape.builder;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class FixtureWithImage implements Disposable {
    private final Map<String, ShapeScaffold> shapeScaffold;
    private final Vector2 anchor;

    @Setter
    private String imageLocation;

    @JsonIgnore
    @Setter
    private Texture texture;

    public FixtureWithImage() {
        this(new HashMap<>(), new Vector2(0, 0), null);
    }

    public FixtureWithImage(Map<String, ShapeScaffold> shapeScaffold,
                            Vector2 anchor,
                            String imageLocation) {
        this.shapeScaffold = shapeScaffold;
        this.anchor = anchor;
        this.imageLocation = imageLocation;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}

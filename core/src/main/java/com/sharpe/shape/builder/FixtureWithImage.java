package com.sharpe.shape.builder;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import javax.swing.text.Position;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class FixtureWithImage {
    private final Map<String, ShapeScaffold> shapeScaffold;
    private final Vector2 anchor;

    private String imageLocation;

    @JsonIgnore
    private AssetManager assetManager;

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

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
        if(assetManager!=null){
            assetManager.load(imageLocation, Texture.class);
        }
    }

    public Sprite createSprite(Position position){

        Texture texture = new Texture(this.imageLocation);
        float height = texture.getHeight();
        float width = texture.getWidth();

        if(height > width){
            width = height/width;
            height = 1;
        }else{
            height = width/height;
            width = 1;
        }

        Sprite sprite = new Sprite(texture);

        sprite.setOrigin(height/2f,width/2f);
        sprite.setBounds(-width/2f,-height/2f,width,height);
        sprite.setOrigin(height/2f,width/2f);

        return sprite;
    }
}

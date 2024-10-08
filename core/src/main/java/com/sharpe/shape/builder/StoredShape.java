package com.sharpe.shape.builder;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoredShape {


    @Getter
    public static class BodyWithImage{
        private final Map<String, ShapeScaffold> shapeScaffold;
        private final Vector2 anchor;

        public BodyWithImage(){
            this(new HashMap<>(), new Vector2(0,0));
        }

        public BodyWithImage(Map<String, ShapeScaffold> shapeScaffold,
                             Vector2 anchor){
            this.shapeScaffold = shapeScaffold;
            this.anchor = anchor;
        }
    }

    public static record ShapeScaffold(List<Vector2> shapeVectors,
                                       boolean isContinuous) {

        public ShapeScaffold(boolean isContinuous) {
            this(new ArrayList<>(), isContinuous);
        }
    }
}

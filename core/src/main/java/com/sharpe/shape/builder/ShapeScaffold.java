package com.sharpe.shape.builder;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public record ShapeScaffold(List<Vector2> shapeVectors,
                            boolean isEdge) {
    public ShapeScaffold(boolean isEdge) {
        this(new ArrayList<>(), isEdge);
    }
}

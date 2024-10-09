package com.sharpe.shape.serialization;

import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.annotation.JsonProperty;

class SerializedVertex {
    @JsonProperty("x")
    float x;
    @JsonProperty("y")
    float y;

    public SerializedVertex() {
    }

    public SerializedVertex(Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
    }

    public Vector2 toVector() {
        return new Vector2(x, y);
    }
}

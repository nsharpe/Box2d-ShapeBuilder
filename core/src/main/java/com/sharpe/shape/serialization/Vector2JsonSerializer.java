package com.sharpe.shape.serialization;

import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class Vector2JsonSerializer extends JsonSerializer<Vector2> {

    @Override
    public void serialize(Vector2 vector2, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(new SerializedVertex(vector2));
    }
}

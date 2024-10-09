package com.sharpe.shape.serialization;

import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class Vector2JsonDeserializer extends JsonDeserializer<Vector2> {

    @Override
    public Vector2 deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return jsonParser.readValueAs(SerializedVertex.class).toVector();
    }

}

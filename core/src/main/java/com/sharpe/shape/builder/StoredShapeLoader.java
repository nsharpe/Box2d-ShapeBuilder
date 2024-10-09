package com.sharpe.shape.builder;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sharpe.shape.serialization.Vector2JsonDeserializer;
import com.sharpe.shape.serialization.Vector2JsonSerializer;

import java.io.IOException;
import java.io.InputStream;

public class StoredShapeLoader extends AsynchronousAssetLoader<FixtureWithImage, StoredShapeLoader.Parameters> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StoredShapeLoader(FileHandleResolver fileHandleResolver) {
        super(fileHandleResolver);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Vector2.class,new Vector2JsonDeserializer());
        simpleModule.addSerializer(Vector2.class,new Vector2JsonSerializer());
        objectMapper.registerModule(simpleModule);
    }

    public FixtureWithImage fixtureWithImage(InputStream is){
        try {
            return objectMapper.readValue(is, FixtureWithImage.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, Parameters parameter) {
        return new Array<>();
    }

    public static class Parameters extends AssetLoaderParameters<FixtureWithImage> {
        public Parameters() {
            super();
        }
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, Parameters parameter) {
    }

    @Override
    public FixtureWithImage loadSync(AssetManager manager, String fileName, FileHandle file, Parameters parameter) {
        return fixtureWithImage(file.read());
    }
}

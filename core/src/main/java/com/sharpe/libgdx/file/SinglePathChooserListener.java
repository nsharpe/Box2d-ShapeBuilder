package com.sharpe.libgdx.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooserListener;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public class SinglePathChooserListener implements FileChooserListener {

    private Consumer<Optional<Path>> pathConsumer;
    private Runnable onCancel;

    private SinglePathChooserListener(Consumer<Optional<Path>> pathConsumer, Runnable onCancel) {
        this.pathConsumer = pathConsumer;
        this.onCancel = onCancel;
    }

    @Override
    public void selected(Array<FileHandle> array) {
        if(array == null || array.isEmpty()){
            pathConsumer.accept(Optional.empty());
            return;
        }

        if(array.size>1){
            throw new IllegalStateException("Single file expected");
        }
        pathConsumer.accept(Optional.of(array.get(0).file().toPath()));
    }

    @Override
    public void canceled() {
        onCancel.run();
    }

    public static FileChooserListener of(Consumer<Optional<Path>> pathConsumer,
                                         Runnable runnable){
        return new SinglePathChooserListener(pathConsumer,runnable);
    }

    public static FileChooserListener of(Consumer<Optional<Path>> pathConsumer){
        return new SinglePathChooserListener(pathConsumer,()->{});
    }
}

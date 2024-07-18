package kr.co.seoultel.message.mt.mms.core_module.storage;

import kr.co.seoultel.message.mt.mms.core_module.storage.fileIoHandler.FileIOHandler;
import kr.co.seoultel.message.mt.mms.core_module.storage.fileIoHandler.JsonFileIOHandler;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public abstract class Storage<E> {

    protected final String filePath;
    protected final FileIOHandler fileIOHandler;

    protected Supplier<Collection<E>> destroyBySupplier = this::snapshot;

    protected Storage(String filePath) {
        this.filePath = filePath;
        this.fileIOHandler = new JsonFileIOHandler();
    }

    protected Storage(String filePath, FileIOHandler fileIOHandler) {
        this.filePath = filePath;
        this.fileIOHandler = fileIOHandler;
    }



    abstract Collection<E> snapshot();

    public void createFile() throws IOException {
        fileIOHandler.createFile(filePath);
    }

    public void createFile(FileAttribute<?>... attrs) throws IOException {
        fileIOHandler.createFile(filePath, attrs);
    }

    public Storage<E> createFileAnd() throws IOException {
        fileIOHandler.createFile(filePath);
        return this;
    }

    public Storage<E> createFileAnd(FileAttribute<?>... attrs) throws IOException {
        fileIOHandler.createFile(filePath, attrs);
        return this;
    }



    public Storage<E> deleteFileIfExists() throws IOException {
        fileIOHandler.deleteFileIfExists(filePath);
        return this;
    }

    public Optional<Collection<E>> readFileAsCollection(Type type) throws IOException {
        return Optional.ofNullable(fileIOHandler.read(filePath, type));
    }


    public Optional<Collection<E>> readFileAsCollection() throws IOException {
        return Optional.ofNullable(fileIOHandler.read(filePath));
    }



    public Storage<E> destroyBy(Supplier<Collection<E>> destroyBySupplier) throws IOException {
        this.destroyBySupplier = destroyBySupplier;
        return this;
    }

    @PreDestroy
    protected void destroy() throws IOException {
        Collection<E> c = destroyBySupplier.get();
        boolean isDone = false;
        do {
            try {
                fileIOHandler.write(filePath, c);
                isDone = true;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } while (!isDone);

        log.info("Successfully write data[{}] to [{}]", c, filePath);
    }

}

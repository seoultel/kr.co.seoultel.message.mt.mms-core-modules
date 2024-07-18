package kr.co.seoultel.message.mt.mms.core_module.storage.fileIoHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;

public abstract class FileIOHandler<E> {

    public abstract void write(String filePath, E e) throws IOException;
    public abstract void write(String filePath, Collection<E> c) throws IOException;

    public abstract void append(E e);
    public abstract void append(Collection<E> c);

    public abstract Collection<E> read(String filePath) throws IOException;
    public abstract Collection<E> read(String filePath, Type type) throws IOException;

    public String readFileAsString(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    public byte[] readFileAsByteArray(String filePath) throws IOException {
        return Files.readAllBytes(Path.of(filePath));
    }


    public void createFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    public void createFile(String filePath, FileAttribute<?>... attrs) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path, attrs);
        }
    }


    public void deleteFileIfExists(String filePath) throws IOException {
        Files.deleteIfExists(Path.of(filePath));
    }


}

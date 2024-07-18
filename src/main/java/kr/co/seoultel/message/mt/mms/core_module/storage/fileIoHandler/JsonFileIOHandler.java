package kr.co.seoultel.message.mt.mms.core_module.storage.fileIoHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.common.serializer.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
public class JsonFileIOHandler<E> extends FileIOHandler<E> {

    private final Gson gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();



    @Override
    public void write(String filePath, E e) throws IOException {
        String json = gson.toJson(e);
        Files.write(Path.of(filePath),
                json.getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public void write(String filePath, Collection<E> c) throws IOException {
        String json = gson.toJson(c);
        Files.write(Path.of(filePath),
                    json.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
    }

    /* TODO : E 타입 데이터를 JSON 형태로 직렬화하여 파일에 append 하는 코드가 당장에 필요하지 않아 추후 작성 */
    @Override
    @Deprecated
    public void append(E e) {

    }

    /* TODO : E 타입 데이터를 JSON 형태로 직렬화하여 파일에 append 하는 코드가 당장에 필요하지 않아 추후 작성 */
    @Override
    @Deprecated
    public void append(Collection<E> c) {

    }

    @Override
    public Collection<E> read(String filePath) throws IOException {
        Type type = new TypeToken<Collection<E>>(){}.getType();

        String readAsString = readFileAsString(filePath);
        return gson.fromJson(readAsString, type);
    }

    @Override
    public Collection<E> read(String filePath, Type type) throws IOException {
        String readAsString = readFileAsString(filePath);
        return gson.fromJson(readAsString, type);
    }

}

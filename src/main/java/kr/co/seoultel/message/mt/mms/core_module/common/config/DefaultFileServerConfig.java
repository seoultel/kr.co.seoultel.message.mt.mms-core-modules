package kr.co.seoultel.message.mt.mms.core_module.common.config;


import kr.co.seoultel.message.mt.mms.core.common.interfaces.Checkable;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Objects;


@Slf4j
public abstract class DefaultFileServerConfig implements Checkable {

    public static String TOKEN;
    public static String IMAGE_REQUEST_URI;
    public static String IMAGE_DOWNLOAD_PATH;

    @Override
    @PostConstruct
    public void check() {
        Objects.requireNonNull(TOKEN);
        Objects.requireNonNull(IMAGE_REQUEST_URI);
        Objects.requireNonNull(IMAGE_DOWNLOAD_PATH);

        log.info("[FILE-SERVER-CONFIG] {}", this);
    }

    @Override
    public String toString() {
        return "FileServerConfig{" +
                    "IMAGE_REQUEST_URI='" + IMAGE_REQUEST_URI + '\'' +
                    ", IMAGE_DOWNLOAD_PATH='" + IMAGE_DOWNLOAD_PATH + '\'' +
                '}';
    }
}

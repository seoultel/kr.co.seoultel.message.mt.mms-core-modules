package kr.co.seoultel.message.mt.mms.core_module.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.seoultel.message.mt.mms.core.util.CommonUtil;
import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultFileServerConfig;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer.*;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import kr.co.seoultel.message.mt.mms.core_module.dto.ImageReqBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static kr.co.seoultel.message.mt.mms.core.constant.Constants.JPEG_EXTENSION;
import static kr.co.seoultel.message.mt.mms.core.constant.Constants.JPG_EXTENSION;


@Slf4j
public class ImageUtil {

    private static final RestTemplate restTemplate = getRestTemplateInstance();

    public static String getImageKey(String groupCode, String imageId) {
        return String.join(":", groupCode, imageId);
    }

    public static String getGroupCodeByImageKey(String imageKey) {
        int colonIndex = imageKey.indexOf(":");
        return imageKey.substring(0, colonIndex);
    }

    public static String getImageIdFromImageKey(String imageKey) {
        int colonIndex = imageKey.indexOf(":");
        return imageKey.substring(colonIndex + 1);
    }


    public static boolean isUsableImageFile(String fileName) {
        return isJpg(fileName) || isJpeg(fileName);
    }


    public static boolean isJpg(String fileName) {
        return fileName.toLowerCase().contains(JPG_EXTENSION);
    }

    public static boolean isJpeg(String fileName) {
        return fileName.toLowerCase().contains(JPEG_EXTENSION);
    }


    public static byte[] getImageBytes(String imagePath) throws IOException {
        Path path = Path.of(imagePath);
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (Exception e) {
                log.error("[EXCEPTION] Exception occured during reading to image in {}", imagePath, e);
                throw e;
            }
        }

        throw new FileNotFoundException(String.format("File[path : %s] not exists", imagePath));
    }


    public static byte[] requestImagesByteArrToFileServer(InboundMessage inboundMessage, Set<String> mediaFileSet) throws FileServerException {
        String groupCode = inboundMessage.getMessageDelivery().getGroupCode();
        String umsMsgId = inboundMessage.getMessageDelivery().getUmsMsgId();

        ImageReqBody imageReqBody = new ImageReqBody(groupCode, mediaFileSet);

        try {
            HttpEntity<ImageReqBody> imageRequestHttpEntity = new HttpEntity<>(imageReqBody);
            ResponseEntity<byte[]> response = restTemplate.exchange(DefaultFileServerConfig.IMAGE_REQUEST_URI, HttpMethod.POST, imageRequestHttpEntity, byte[].class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return response.getBody();
            } else {
                log.error("[IMAGE-UTIL] image-request's status-code is {}", response.getStatusCode());
            }
        } catch (HttpClientErrorException.BadRequest e) {
            throw new ImageNotFoundException(inboundMessage);
        } catch (HttpClientErrorException.NotFound e) {
            // if images, request to file server, is expired
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                List<String> expiredImageIdList = (List<String>) objectMapper.readValue(e.getResponseBodyAsString(), Map.class).get("expireFileIdList");

                throw new ImageExpiredException(inboundMessage, expiredImageIdList);
            } catch (JsonProcessingException ex) {
                /*
                 * * Excepiton : When, response of expiredImage from FileServer parsing to json
                 */

                log.error("[JsonParsingException] Fail to parsing ExpiredFileIdList to json in Message[umsMsgId : {}]", umsMsgId);
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("[UNAUTHORIZED] please check the file-server's token");
            throw new FileServerTokenException(inboundMessage);
        } catch (HttpClientErrorException | HttpServerErrorException.InternalServerError | org.springframework.web.client.ResourceAccessException e) {
            /*
             * 1. HttpServerErrorException.InternalServerError : 500 Error, file-server power-off
             * 2. org.springframework.web.client.ResourceAccessException : request to wrong uri
             * 3. ConnectionTimeOutException
             *    -> connection timed out: /192.168.50.19:8000; nested exception is io.netty.channel.ConnectTimeoutException: connection timed out: /192.168.50.19:8000
             *    -> class org.springframework.web.reactive.function.client.WebClientRequestException
             */
            log.error("[SYSTEM] Please check file server's connection[{}]", e.getClass());
            CommonUtil.doThreadSleep(1000L);
            throw new FileServerDisconnectionException(inboundMessage);
        } catch(Exception e) {
            log.error("[Exception] {}", e.getMessage());
            log.error("[Exception] {}", e.getClass());
            log.error("IMAGE-UTIL ???", e);
        }

        throw new FileServerException();
    }

    public static RestTemplate getRestTemplateInstance() {
        if (restTemplate == null) {
            return new RestTemplateBuilder()
                    .defaultHeader(HttpHeaders.AUTHORIZATION, DefaultFileServerConfig.TOKEN)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .build();
        }

        return restTemplate;
    }

}

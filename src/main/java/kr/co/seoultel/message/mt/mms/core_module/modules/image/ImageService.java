package kr.co.seoultel.message.mt.mms.core_module.modules.image;

import kr.co.seoultel.message.mt.mms.core_module.common.config.DefaultFileServerConfig;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer.AttachedImageFormatException;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer.FileServerException;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer.ImageExpiredException;
import kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer.ImageNotFoundException;
import kr.co.seoultel.message.mt.mms.core_module.dto.InboundMessage;
import kr.co.seoultel.message.mt.mms.core_module.modules.redis.RedisService;
import kr.co.seoultel.message.mt.mms.core_module.utils.ImageUtil;
import kr.co.seoultel.message.mt.mms.core_module.utils.RedisUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static kr.co.seoultel.message.mt.mms.core_module.utils.ImageUtil.isUsableImageFile;
import static kr.co.seoultel.message.mt.mms.core_module.utils.ImageUtil.requestImagesByteArrToFileServer;


@Slf4j
public class ImageService {

    protected final RedisService redisService;

    @Getter
    private static final ConcurrentHashMap<String, String> images = new ConcurrentHashMap<String, String>();

    public ImageService(RedisService redisService) {
        this.redisService = redisService;
    }


    public void setup() {
        File directory = new File(DefaultFileServerConfig.IMAGE_DOWNLOAD_PATH);

        try {
            if (!directory.exists()) {
                Files.createDirectories(directory.toPath());
            }

            List<File> directories = Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                    .filter(File::isDirectory).collect(Collectors.toList());


            if (!directories.isEmpty()) {
                // directory = groupCode
                directories.forEach((groupCode) -> {
                    List<File> imageFiles = Arrays.stream(Objects.requireNonNull(groupCode.listFiles()))
                            .filter((file) -> {
                                String fileName = file.getName();
                                return file.isFile() && (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"));
                            }).collect(Collectors.toList());

                    for (File image : imageFiles) {
                        // imageId.jpg or imageId.jpeg, etc ..
                        String imageName = image.getName();
                        int extensionDotIndex = imageName.lastIndexOf(".");

                        // imageName = imageId
                        imageName = imageName.substring(0, extensionDotIndex);

                        // imagePath = IMAGE_ROOT_PATH/groupCode/[imageId.jpg|imageId.jpeg]
                        String imagePath = image.getAbsolutePath();
                        String imageKey = ImageUtil.getImageKey(groupCode.getName(), imageName);

                        images.put(imageKey, imagePath);
                    }
                });
            }
        } catch (IOException e) {
            log.error("[SYSTEM] EXCEPTION OCCURED DURING ImageUtil.setup()", e);
        }
    }


    public void removeExpiredImages() {
        Map<String, String> aliveImagePathMap = ImageService.getImages();
        for (Map.Entry<String, String> entry : aliveImagePathMap.entrySet()) {
            String imageKey = entry.getKey();
            String imagePath = entry.getValue();

            String groupCode = ImageUtil.getGroupCodeByImageKey(imageKey);
            String imageId = ImageUtil.getImageIdFromImageKey(imageKey);

            try {
                if (redisService.hasKey(groupCode, imageId)) {
                    File expiredImage = new File(imagePath);
                    if (expiredImage.exists()) {
                        expiredImage.delete();
                    }

                    aliveImagePathMap.remove(imageKey);
                }
            } catch(Exception e) {
                e.getCause();
                log.error(e.getMessage());
            }
        }
    }
    public static void searchUndownloadedImagesAndRemoveInHashMap(String groupCode, @NonNull List<String> imageIdList) {
        Objects.requireNonNull(imageIdList);

        imageIdList.stream().filter((imageId) -> {
            String imageKey = ImageUtil.getImageKey(groupCode, imageId);
            if (images.containsKey(imageKey)) {
                String imagePath = images.get(imageKey);

                File image = new File(imagePath);
                return !image.exists();
            }

            return false;
        }).forEach((imageId) -> {
            String imageKey = ImageUtil.getImageKey(groupCode, imageId);
            images.remove(imageKey);
        });
    }

    public void hasExpiredImages(InboundMessage inboundMessage, String groupCode, Collection<String> imageIds) throws ImageNotFoundException {
        List<String> expiredImageIds = (List) imageIds.stream().filter((imageId) -> {
            String imageKey = RedisUtil.getRedisKeyOfImage(groupCode);
            return !this.redisService.hasKey(imageKey, imageId);
        }).collect(Collectors.toList());
        if (!expiredImageIds.isEmpty()) {
            throw new ImageNotFoundException(inboundMessage, expiredImageIds);
        }
    }

    public static void checkAttachedImageCount(InboundMessage inboundMessage, List<String> imageIdList) throws AttachedImageFormatException {
        if (imageIdList.size() > 3) {
            throw new AttachedImageFormatException(inboundMessage);
        }
    }

    public static Set<String> getUndowndloadedImageIdSet(String groupCode, List<String> undownloadedImageIds) {
        return undownloadedImageIds.stream().filter((imageId) -> {
            String imageKey = ImageUtil.getImageKey(groupCode, imageId);
            return !images.containsKey(imageKey);
        }).collect(Collectors.toSet());
    }

    public static void saveImagesByImageId(InboundMessage inboundMessage, Set<String> undownloadedImageIdSet) throws FileServerException {
        String groupCode = inboundMessage.getMessageDelivery().getGroupCode();
        String saveImagePath = String.join("/", DefaultFileServerConfig.IMAGE_DOWNLOAD_PATH, groupCode);

        try {
            byte[] byteArrOfImages = requestImagesByteArrToFileServer(inboundMessage, undownloadedImageIdSet); // ImageExpiredException,  FileServerDisconnectionException,
            saveImagesInByteArr(groupCode, saveImagePath, byteArrOfImages);
        } catch (ImageExpiredException e) {
            Collection<String> expiredImageIds = e.getExpiredImageIds();

            expiredImageIds.forEach((expiredImageId) -> {
                String imageKey = ImageUtil.getImageKey(groupCode, expiredImageId);
                images.remove(imageKey);

                String clientImageDirecotryPath = String.join("/", DefaultFileServerConfig.IMAGE_DOWNLOAD_PATH, groupCode);

                File clientDirectory = new File(clientImageDirecotryPath);
                if (clientDirectory.exists()) {
                    Arrays.stream(Objects.requireNonNull(clientDirectory.listFiles()))
                            .filter((file) -> file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")))
                            .filter((image) -> {
                                String imagePath = String.join("/", clientImageDirecotryPath, expiredImageId);
                                return image.getPath().startsWith(imagePath);
                            }).forEach(File::delete);
                }
            });
            throw e;
        } catch (FileServerException e) {
            throw e;
        }
    }

    private static void saveImagesInByteArr(String groupCode, String saveDirectoryPath, byte[] imagesByteArr) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imagesByteArr);
            ZipInputStream zis = new ZipInputStream(bais)) {

            // create directory -> path : imageRootPath/groupCode/imageName
            Path directoryPath = Paths.get(saveDirectoryPath);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // save images(entry) in Zip;
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                String entryPath = saveDirectoryPath + File.separator + entryName;

                // if image's extension jpg or jpeg and isn't exist in entryPath -> save
                if (isUsableImageFile(entryName)) {
                    int dotIdx = entryName.indexOf(".");
                    String imageId = entryName.substring(0, dotIdx);
                    String key = String.join(":", groupCode, imageId);

                    if (!images.containsKey(key)) {
                        // save image
                        saveImage(imageId, zis, entryPath);
                        images.put(key, entryPath);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveImage(String imageId, ZipInputStream zis, String saveDirectoryPath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveDirectoryPath))) {
            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = zis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        log.info("Successfully saved image[{}] to {}", imageId, saveDirectoryPath);
    }

}

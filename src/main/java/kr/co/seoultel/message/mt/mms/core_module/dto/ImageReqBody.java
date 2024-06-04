package kr.co.seoultel.message.mt.mms.core_module.dto;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@NoArgsConstructor
public class ImageReqBody {
    private String groupCode;
    private Set<String> fileIdList;

    public ImageReqBody(String groupCode, Set<String> fileIdList) {
        this.groupCode = groupCode;
        this.fileIdList = fileIdList;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public ImageReqBody setGroupCode(String groupCode) {
        this.groupCode = groupCode;
        return this;
    }

    public Set<String> getFileIdList() {
        return fileIdList;
    }

    public ImageReqBody setFileIdList(Set<String> fileIdList) {
        this.fileIdList = fileIdList;
        return this;
    }

    @Override
    public String toString() {
        return "ImageRequestBody{" +
                    "groupCode='" + groupCode + '\'' +
                    ", fileIdList=" + fileIdList +
                '}';
    }
}

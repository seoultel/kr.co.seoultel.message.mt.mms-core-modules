package kr.co.seoultel.message.mt.mms.core_module.common.exceptions.fileServer;

import kr.co.seoultel.message.core.dto.MessageDelivery;
import kr.co.seoultel.message.mt.mms.core.constant.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileServerException extends Exception {

    protected MessageDelivery messageDelivery;

    protected String mnoResult = Constants.UNKNOWN_FILE_SERVER_ERROR_MNO_RESULT;
    protected String reportMessage = Constants.UNKNOWN_FILE_SERVER_ERROR;



    public FileServerException(String message) {
        super(message);
    }

    public FileServerException(Throwable cause) {
        super(cause);
    }
}

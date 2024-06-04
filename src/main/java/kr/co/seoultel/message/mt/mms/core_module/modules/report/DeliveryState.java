package kr.co.seoultel.message.mt.mms.core_module.modules.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryState {
    SUBMIT("SUBMIT"), SUBMIT_ACK("SUBMIT_ACK"), REPORT("REPORT");

    private String state;

    public static void main(String[] args) {
        System.out.println(SUBMIT);
    }

    public static String getMessageDeliveryState(int deliveryState) {
        switch (deliveryState) {
            case 0:
                return "READY";
            case 1:
                return "SENDING";
            case 2:
                return "SUBMIT";
            case 3:
                return "SUCCESS";
            case 4:
                return "FAILED";
            case 5:
                return "RETRY";
            default:
                return "UNKNOWN";
        }
    }
}

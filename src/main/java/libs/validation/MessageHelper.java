package libs.validation;

import static fr.maif.commons.resourcewrapper.HttpStatusCode.BAD_REQUEST;
import static fr.maif.commons.resourcewrapper.HttpStatusCode.INTERNAL_SERVER_ERROR;

import java.util.Collections;
import java.util.List;

import fr.maif.commons.resourcewrapper.HttpStatusCode;
import fr.maif.commons.resourcewrapper.Message;

public final class MessageHelper {

    private MessageHelper() {
    }

    public static List<Message> internalServerError() {
        return msg(MessageGDR.T00001, INTERNAL_SERVER_ERROR);
    }

    public static List<Message> internalServerError(String description) {
        return msg(MessageGDR.T00001, description, INTERNAL_SERVER_ERROR);
    }

    public static List<Message> msg(MessageGDR messageKey) {
        return msg(messageKey, null, null, null, BAD_REQUEST);
    }

    public static List<Message> msg(MessageGDR messageKey, String arg) {
        return msg(messageKey, null, List.of(arg), arg, BAD_REQUEST);
    }

    public static List<Message> msg(MessageGDR messageKey, List<String> args) {
        return msg(messageKey, null, args, null, BAD_REQUEST);
    }

    public static List<Message> msg(MessageGDR messageKey, HttpStatusCode httpStatus) {
        return msg(messageKey, null, null, null, httpStatus);
    }

    public static List<Message> msg(MessageGDR messageKey, String description, String arg, String fieldPath) {
        return msg(messageKey, description, Collections.singletonList(arg), fieldPath, BAD_REQUEST);
    }

    public static List<Message> msg(MessageGDR messageKey, String description, HttpStatusCode httpStatus) {
        return msg(messageKey, description, null, null, httpStatus);
    }

    public static List<Message> msg(MessageGDR messageKey, String description, List<String> args, String fieldPath, HttpStatusCode httpStatus) {
        return List.of(
                Message.newBuilder(null)
                        .description(description)
                        .args(args)
                        .fieldPath(fieldPath)
                        .status(httpStatus)
                        .messageKey(messageKey.name())
                        .build()
        );
    }
}

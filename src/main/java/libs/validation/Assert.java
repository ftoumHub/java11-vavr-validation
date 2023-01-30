package libs.validation;

import static fr.maif.commons.resourcewrapper.HttpStatusCode.BAD_REQUEST;
import static fr.maif.reference.type.ReferenceSocietaire.fromRefSoc;
import static io.vavr.API.Try;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static libs.validation.MessageGDR.F00200;
import static libs.validation.MessageGDR.F00202;
import static libs.validation.MessageHelper.msg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import fr.maif.commons.resourcewrapper.HttpStatusCode;
import fr.maif.commons.resourcewrapper.Message;
import fr.maif.reference.type.ReferenceEvenement;
import fr.maif.reference.type.ReferenceSocietaire;
import io.vavr.control.Either;
import io.vavr.control.Validation;

public final class Assert {

    private static final String RG_PARAM_FORMAT = "Le format du paramètre %s est incorrect";
    private static final String REF_EVT = "referenceEvenement";
    private static final String REF_SOC = "referenceSocietaire";

    private Assert() {
        // Hide public constructor
    }

    public static Either<List<Message>, ReferenceEvenement> validRefEvt(String refEvt) {
        return Try(() -> ReferenceEvenement.fromRefEvt(refEvt))
                .toEither()
                .mapLeft(error -> msg(F00200, String.format(RG_PARAM_FORMAT, REF_EVT), refEvt, REF_EVT));
    }

    public static Either<List<Message>, ReferenceEvenement> notNullAndValidRefEvt(String refEvt) {
        return notBlank(refEvt, F00202, null, BAD_REQUEST, REF_EVT, singletonList(REF_EVT))
                .flatMap(Assert::validRefEvt);
    }

    public static Either<List<Message>, ReferenceSocietaire> validRefSoc(String refSoc) {
        return nonNull(refSoc)
                ? Try(() -> fromRefSoc(refSoc)).toEither().mapLeft(error ->
                    msg(F00200, String.format(RG_PARAM_FORMAT, REF_SOC), refSoc, REF_SOC))
                : Either.right(null);
    }

    public static Either<List<Message>, ReferenceSocietaire> notNullAndValidRefSoc(String refSoc) {
        return notBlank(refSoc, F00202, null, BAD_REQUEST, REF_SOC, singletonList(REF_SOC))
                .flatMap(Assert::validRefSoc);
    }

    public static Either<List<Message>, List<ReferenceSocietaire>> notNullAndValidRefSoc(List<String> refSocs) {
        return Assert.notEmpty(refSocs, F00202, null, BAD_REQUEST, "referenceSocietaires",
                singletonList("referenceSocietaires")).flatMap(notEmpty -> Eithers.sequence(refSocs, Assert::notNullAndValidRefSoc));
    }

    public static <T> Either<List<Message>, List<T>> notEmpty(Collection<T> list, String arg) {
        return notEmpty(list, F00202, null, BAD_REQUEST, arg, List.of(arg));
    }

    public static <T> Either<List<Message>, List<T>> notEmpty(Collection<T> value, MessageGDR messageKey, String description, List<String> args) {
        return notEmpty(value, messageKey, description, BAD_REQUEST, null, args);
    }

    public static <T> Either<List<Message>, List<T>> notEmpty(Collection<T> value, MessageGDR messageKey, String description,
                                                              HttpStatusCode httpStatus,
                                                              String fieldPath, List<String> args) {
        if (CollectionUtils.isEmpty(value)) {
            return Either.left(msg(messageKey, description, args, fieldPath, httpStatus));
        } else {
            return Either.right(new ArrayList<>(value));
        }
    }

    public static Either<List<Message>, String> notBlank(String value, MessageGDR messageKey, String description,
                                                         HttpStatusCode httpStatus,
                                                         String fieldPath, List<String> args) {
        if (StringUtils.isBlank(value)) {
            return Either.left(msg(messageKey, description, args, fieldPath, httpStatus));
        } else {
            return Either.right(value);
        }
    }

    public static <T> Either<List<Message>, T> notNull(T value, String arg) {
        return notNull(value, F00202, null, BAD_REQUEST, arg, List.of(arg));
    }

    public static <T> Either<List<Message>, T> notNull(T value, MessageGDR error) {
        return notNull(value, error, null, BAD_REQUEST, null, null);
    }

    public static <T> Either<List<Message>, T> notNull(T value, MessageGDR error, String description) {
        return notNull(value, error, description, BAD_REQUEST, null, null);
    }

    public static <T> Either<List<Message>, T> notNull(T value, MessageGDR error, String description, String arg) {
        return notNull(value, error, description, BAD_REQUEST, arg, singletonList(arg));
    }

    public static <T> Either<List<Message>, T> notNull(T value, MessageGDR messageKey, String description, HttpStatusCode httpStatus,
                                                       String fieldPath, List<String> args) {
        return value == null ? Either.left(msg(messageKey, description, args, fieldPath, httpStatus)) : Either.right(value);
    }
}

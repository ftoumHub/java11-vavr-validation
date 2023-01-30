package patterns;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import fr.maif.commons.resourcewrapper.HttpStatusCode;
import fr.maif.commons.resourcewrapper.Message;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Validation;
import lombok.NonNull;
import lombok.Value;

/**
 * Created by mtumilowicz on 2018-12-09.
 */
@Value
public class Email {
    public static final Predicate<String> VALIDATOR = Pattern.compile("[\\w._%+-]+@[\\w.-]+\\.[\\w]{2,}").asMatchPredicate();

    public final String email;

    private Email(String email) {
        this.email = email;
    }

    public static Email of(@NonNull String email) {
        Preconditions.checkArgument(VALIDATOR.test(email));

        return new Email(email);
    }

    public static Validation<List<String>, List<String>> validate(List<String> emails) {
        return emails.partition(VALIDATOR)
                .apply((successes, failures) -> failures.isEmpty()
                        ? Validation.valid(successes)
                        : Validation.invalid(failures.map(email -> email + " is not a valid email!")));
    }

    public static Either<java.util.List<Message>, java.util.List<String>> valid(java.util.List<String> emails) {
        return List.ofAll(emails).partition(VALIDATOR)
                .apply((successes, failures) -> {
                    if (failures.isEmpty()) {
                        return Either.right(successes.toJavaList());
                    }

                    List<Message> desc = failures.map(
                            email -> Message.create(email + " is not a valid email!", HttpStatusCode.BAD_REQUEST, "", "", "desc",
                                    java.util.List.of()));
                    return Either.left(desc.asJava());
                });
    }
}

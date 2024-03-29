package patterns;

import static fr.maif.commons.resourcewrapper.HttpStatusCode.BAD_REQUEST;

import com.google.common.base.Preconditions;

import fr.maif.commons.resourcewrapper.Message;
import io.vavr.control.Either;
import io.vavr.control.Validation;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by mtumilowicz on 2018-12-09.
 */
@Value
public class PostalCode {
    public static final Predicate<String> VALIDATOR = Pattern.compile("\\d{2}-\\d{3}").asMatchPredicate();
    
    public final String postalCode;

    private PostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public static PostalCode of(@NonNull String postalCode) {
        Preconditions.checkArgument(VALIDATOR.test(postalCode));
        
        return new PostalCode(postalCode);
    }

    public static Validation<String, String> validate(String postalCode) {
        return VALIDATOR.test(postalCode)
                ? Validation.valid(postalCode)
                : Validation.invalid(postalCode + " is not a proper postal code!");
    }

    public static Either<List<Message>, PostalCode> valid(String postalCode) {
        return VALIDATOR.test(postalCode)
                ? Either.right(PostalCode.of(postalCode))
                : Either.left(List.of(Message.create(postalCode + " is not a proper postal code!",
                BAD_REQUEST,
                "",
                "",
                "desc",
                java.util.List.of())));
    }
}

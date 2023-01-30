package patterns;


import static fr.maif.commons.resourcewrapper.HttpStatusCode.BAD_REQUEST;

import com.google.common.base.Preconditions;

import fr.maif.commons.resourcewrapper.HttpStatusCode;
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
public class Word {
    public static final Predicate<String> VALIDATOR = Pattern.compile("[\\w]+").asMatchPredicate();
    
    String word;

    private Word(String word) {
        this.word = word;
    }

    public static Word of(@NonNull String word) {
        Preconditions.checkArgument(VALIDATOR.test(word));
        
        return new Word(word);
    }

    public static Validation<String, String> validate(String word) {
        return VALIDATOR.test(word)
                ? Validation.valid(word)
                : Validation.invalid(word + " is not a proper word!");
    }

    public static Either<List<Message>, Word> valid(String word) {
        return VALIDATOR.test(word)
                ? Either.right(Word.of(word))
                : Either.left(List.of(Message.create(word + " is not a proper word!",
                BAD_REQUEST,
                "",
                "",
                "desc",
                java.util.List.of())));
    }
}

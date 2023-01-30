package patterns;

import static fr.maif.commons.resourcewrapper.HttpStatusCode.BAD_REQUEST;

import java.util.List;

import com.google.common.base.Preconditions;

import fr.maif.commons.resourcewrapper.Message;
import io.vavr.control.Either;
import lombok.Value;

/**
 * Created by mtumilowicz on 2018-12-09.
 */
@Value
public class Age {
    int age;

    private Age(int age) {
        this.age = age;
    }
    
    public static Age of(int age) {
        Preconditions.checkArgument(age > 0);
        
        return new Age(age);
    }

    public static Either<List<Message>, Age> valid(int age) {
        return age > 0
                ? Either.right(Age.of(age))
                : Either.left(List.of(Message.create(age + " is not > 0",
                BAD_REQUEST,
                "",
                "",
                "desc",
                java.util.List.of())));
    }
}

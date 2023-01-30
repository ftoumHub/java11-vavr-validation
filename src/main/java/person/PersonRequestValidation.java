package person;

import static libs.validation.Assert.notNull;
import static libs.validation.MessageGDR.F00220;

import java.util.List;

import address.AddressRequestValidation;
import fr.maif.commons.resourcewrapper.Message;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Validation;
import libs.validation.Eithers;
import patterns.Age;
import patterns.Email;
import patterns.Emails;
import patterns.Word;
import validation.NumberValidation;

/**
 * Created by mtumilowicz on 2018-12-09.
 */
public class PersonRequestValidation {
    public static Validation<Seq<String>, ValidPersonRequest> validate(PersonRequest request) {

        return Validation
                .combine(
                        Word.validate(request.getName()),
                        Email.validate(request.getEmails()).mapError(error -> error.mkString(", ")),
                        AddressRequestValidation.validate(request.getAddress()).mapError(error -> error.mkString(", ")),
                        NumberValidation.positive(request.getAge()))
                .ap((name, emails, address, age) -> ValidPersonRequest.builder()
                        .name(Word.of(name))
                        .emails(emails.map(Email::of).transform(Emails::new))
                        .address(address)
                        .age(Age.of(age))
                        .build());
    }

    public static Either<List<Message>, ValidPersonRequest> valid(PersonRequest request) {
        return notNull(request, F00220)
                .flatMap(notNull ->
                        Eithers.zipEnd(
                                Word.valid(request.getName()),
                                Email.valid(request.getEmails().asJava()),
                                AddressRequestValidation.valid(request.getAddress()),
                                Age.valid(request.getAge()),
                                () -> ValidPersonRequest.builder()
                                        .name(Word.of(notNull.getName()))
                                        .emails(notNull.getEmails().map(Email::of).transform(Emails::new))
                                        .address(AddressRequestValidation.valid(request.getAddress()).get())
                                        .age(Age.of(notNull.getAge()))
                                        .build())
                        );

    }
}

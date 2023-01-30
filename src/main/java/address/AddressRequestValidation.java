package address;

import static libs.validation.Assert.notNull;
import static libs.validation.MessageGDR.F00220;

import java.util.List;

import fr.maif.commons.resourcewrapper.Message;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Validation;
import libs.validation.Eithers;
import patterns.Age;
import patterns.Email;
import patterns.Emails;
import patterns.PostalCode;
import patterns.Word;
import person.ValidPersonRequest;

/**
 * Created by mtumilowicz on 2018-12-09.
 */
public class AddressRequestValidation {
    public static Validation<Seq<String>, ValidAddressRequest> validate(AddressRequest request) {

        return Validation
                .combine(
                        Word.validate(request.getCity()),
                        PostalCode.validate(request.getPostalCode()))
                .ap((city, postalCode) -> ValidAddressRequest.builder()
                        .city(Word.of(city))
                        .postalCode(PostalCode.of(postalCode))
                        .build());
    }

    public static Either<List<Message>, ValidAddressRequest> valid(AddressRequest request) {
        return notNull(request, F00220)
                .flatMap(req ->
                        Eithers.zipEnd(
                                Word.valid(req.getCity()),
                                PostalCode.valid(req.getPostalCode()),
                                () -> ValidAddressRequest.builder()
                                        .city(Word.of(req.getCity()))
                                        .postalCode(PostalCode.of(req.getPostalCode()))
                                        .build()
                        )
                );

    }
}

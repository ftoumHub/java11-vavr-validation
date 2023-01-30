package person;

import static fr.maif.commons.resourcewrapper.HttpStatusCode.BAD_REQUEST;
import static java.util.Arrays.asList;
import static libs.validation.MessageGDR.F00220;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import address.AddressRequest;
import address.ValidAddressRequest;
import fr.maif.commons.resourcewrapper.HttpStatusCode;
import fr.maif.commons.resourcewrapper.Message;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import patterns.Age;
import patterns.Email;
import patterns.Emails;
import patterns.PostalCode;
import patterns.Word;

public class PersonEithersValidationTest {

    @Test
    @DisplayName("test validate - null")
    void test_validate_null() {
        var report = PersonRequestValidation.valid(null);

        //then:
        assertTrue(report.isLeft());
        assertThat(report.getLeft())
                .isEqualTo(asList(
                        Message.create(null, BAD_REQUEST, "F00220", null, null, asList())
                ));
    }

    @Test
    @DisplayName("test validate - valid")
    void test_validate_valid() {
        //given:
        var addressRequest = AddressRequest.builder()
                .city("Warsaw")
                .postalCode("00-001")
                .build();
        //and:
        var validAddressRequest = ValidAddressRequest.builder()
                .city(Word.of("Warsaw"))
                .postalCode(PostalCode.of("00-001"))
                .build();

        //and:
        var personRequest = PersonRequest.builder()
                .name("Michal")
                .age(1)
                .emails(List.of("michal@gmail.com"))
                .address(addressRequest)
                .build();

        //when:
        var report = PersonRequestValidation.valid(personRequest);

        //then:
        assertTrue(report.isRight());
        assertThat(report.get()).isEqualTo(ValidPersonRequest.builder()
                .name(Word.of("Michal"))
                .age(Age.of(1))
                .emails(new Emails(List.of(Email.of("michal@gmail.com"))))
                .address(validAddressRequest)
                .build());
    }

    @Test
    @DisplayName("test validate - invalid")
    void test_validate_invalid() {
        //given:
        var addressRequest = AddressRequest.builder()
                .city("Warsaw^")
                .postalCode("a")
                .build();

        //and:
        var personRequest = PersonRequest.builder()
                .name("Michal_")
                .age(-1)
                .emails(List.of("b"))
                .address(addressRequest)
                .build();

        //when:
        var report = PersonRequestValidation.valid(personRequest);

        //then:
        assertTrue(report.isLeft());
        assertThat(report.getLeft()).isEqualTo(asList(
                Message.create("b is not a valid email!", BAD_REQUEST, "", "", "desc", asList()),
                Message.create("Warsaw^ is not a proper word!", BAD_REQUEST, "", "", "desc", asList()),
                Message.create("a is not a proper postal code!", BAD_REQUEST, "", "", "desc", asList()),
                Message.create("-1 is not > 0", BAD_REQUEST, "", "", "desc", asList())
        ));
    }

    @Test
    @DisplayName("test validate - partially valid")
    void test_validate_partially_invalid() {
        //given:
        var addressRequest = AddressRequest.builder()
                .city("Warsaw")
                .postalCode("00-001")
                .build();

        //and:
        var personRequest = PersonRequest.builder()
                .name("Michal")
                .emails(List.of("b"))
                .address(addressRequest)
                .build();

        //when:
        var report = PersonRequestValidation.valid(personRequest);

        //then:
        assertTrue(report.isLeft());
        assertThat(report.getLeft()).isEqualTo(asList(
                Message.create("b is not a valid email!", BAD_REQUEST, "", "", "desc", asList()),
                Message.create("0 is not > 0", BAD_REQUEST, "", "", "desc", asList())
        ));
    }
}

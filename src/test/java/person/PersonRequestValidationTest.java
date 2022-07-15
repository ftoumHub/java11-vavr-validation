package person;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import address.AddressRequest;
import address.ValidAddressRequest;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import patterns.Age;
import patterns.Email;
import patterns.Emails;
import patterns.PostalCode;
import patterns.Word;

public class PersonRequestValidationTest {

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
        var report = PersonRequestValidation.validate(personRequest);

        //then:
        assertTrue(report.isValid());
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
        var report = PersonRequestValidation.validate(personRequest);

        //then:
        assertTrue(report.isInvalid());
        assertThat(report.getError()).isEqualTo(Array.of("b is not a valid email!",
                "Warsaw^ is not a proper word!, a is not a proper postal code!",
                "-1 is not > 0"));
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
        var report = PersonRequestValidation.validate(personRequest);

        //then:
        assertTrue(report.isInvalid());
        assertThat(report.getError()).isEqualTo(Array.of("b is not a valid email!", "0 is not > 0"));
    }
}

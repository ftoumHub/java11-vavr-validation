package address;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import patterns.PostalCode;
import patterns.Word;

public class AddressRequestValidationTest {

    @Test
    @DisplayName("valid AddressRequest")
    void valide_AdressRequest() {
        //given:
        var request = AddressRequest.builder()
                .city("Olkusz")
                .postalCode("32-300")
                .build();

        //when:
        var report = AddressRequestValidation.validate(request);

        //then:
        assertTrue(report.isValid());
        assertThat(report.get()).isEqualTo(ValidAddressRequest.builder()
                .city(Word.of("Olkusz"))
                .postalCode(PostalCode.of("32-300"))
                .build());
    }

    @Test
    @DisplayName("full not valid AddressRequest")
    void full_not_valid_AdressRequest() {
        //given:
        var request = AddressRequest.builder().city("wrong!").postalCode("wrong!").build();

        //when:
        var report = AddressRequestValidation.validate(request);

        //then:
        assertTrue(report.isInvalid());
        assertThat(report.getError())
                .isEqualTo(Array.of("wrong! is not a proper word!", "wrong! is not a proper postal code!"));
    }

    @Test
    @DisplayName("partially valid AddressRequest")
    void partially_valid_AdressRequest() {
        //given:
        var request = AddressRequest.builder()
                .city("Warsaw")
                .postalCode("wrong!")
                .build();

        //when:
        var report = AddressRequestValidation.validate(request);

        //then:
        assertTrue(report.isInvalid());
        assertThat(report.getError())
                .isEqualTo(Array.of("wrong! is not a proper postal code!"));
    }
}

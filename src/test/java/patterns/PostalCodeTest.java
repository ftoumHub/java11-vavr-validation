package patterns;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PostalCodeTest {

    @Test
    @DisplayName("test of - valid")
    void test_of_valid() {
        //expect:
        assertThat(PostalCode.of("32-300").postalCode).isEqualTo("32-300");
    }

    @Test
    @DisplayName("test of - invalid")
    void test_of_invalid() {
        assertThatThrownBy(() -> PostalCode.of("a")) //when:
                        .isInstanceOf(IllegalArgumentException.class); //then
    }

    @Test
    @DisplayName("test validate - valid")
    void test_validate_valid() {
        //when:
        var report = PostalCode.validate("32-300");

        //then:
        assertTrue(report.isValid());
        assertThat(report.get()).isEqualTo("32-300");
    }

    @Test
    @DisplayName("test validate - invalid")
    void test_validate_invalid() {
        //when:
        var report = PostalCode.validate("a");

        //then:
        assertTrue(report.isInvalid());
        assertThat(report.getError()).isEqualTo("a is not a proper postal code!");
    }
}

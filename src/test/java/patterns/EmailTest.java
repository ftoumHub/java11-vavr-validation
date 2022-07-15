package patterns;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.vavr.collection.Array;
import io.vavr.collection.List;

public class EmailTest {

    @Test
    @DisplayName("test of - valid")
    void test_of_valid() {
        //expect:
        assertThat(Email.of("a@a.pl").email).isEqualTo("a@a.pl");
    }

    @Test
    @DisplayName("test validate - invalid")
    void test_validate_invalid() {
        assertThatThrownBy(() -> Email.of("a@a")) //when:
                .isInstanceOf(IllegalArgumentException.class); //then
    }

    @Test
    @DisplayName("validate - valid emails")
    void validate_valid_emails() {
        //given:
        var emails = Email.validate(List.of("a@a.pl", "b@b.com"));

        //expect:
        assertTrue(emails.isValid());
        assertThat(emails.get()).isEqualTo(Array.of("a@a.pl","b@b.com"));
    }

    @Test
    @DisplayName("validate - invalid emails")
    void validate_invalid_emails() {
        //given:
        var emails = Email.validate(List.of("a", "b"));
        
        //expect:
        assertTrue(emails.isInvalid());
        assertThat(emails.getError()).isEqualTo(Array.of("a is not a valid email!", "b is not a valid email!"));
    }

    @Test
    @DisplayName("validate - partially valid emails")
    void validate_partially_valid_emails() {
        //given:
        var emails = Email.validate(List.of("a@a.pl", "b"));

        //expect:
        assertTrue(emails.isInvalid());
        assertThat(emails.getError()).isEqualTo(Array.of("b is not a valid email!"));
    }
}

package patterns;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WordTest {

    @Test
    @DisplayName("test of - valid")
    void test_of_valid() {
        //expect:
        assertThat(Word.of("abc").getWord()).isEqualTo("abc");
    }

    @Test
    @DisplayName("test of - invalid")
    void test_of_invalid() {
        assertThatThrownBy(() -> Word.of("_^")) //when:
                .isInstanceOf(IllegalArgumentException.class); //then
    }

    @Test
    @DisplayName("test validate - valid")
    void test_validate_valid() {
        //when:
        var report = Word.validate("abc");

        //then:
        assertTrue(report.isValid());
        assertThat(report.get()).isEqualTo("abc");
    }

    @Test
    @DisplayName("test validate - invalid")
    void test_validate_invalid() {
        //when:
        var report = Word.validate("^");

        //then:
        assertTrue(report.isInvalid());
        assertThat(report.getError()).isEqualTo("^ is not a proper word!");
    }
}

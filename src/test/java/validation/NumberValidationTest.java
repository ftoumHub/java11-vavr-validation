package validation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NumberValidationTest {

    @Test
    @DisplayName("test positive - int > 0")
    void test_positive_int_sup_0() {
        //when:
        var report = NumberValidation.positive(10);

        //then:
        assertTrue(report.isValid());
        assertThat(report.get()).isEqualTo(10);
    }

    @Test
    @DisplayName("test positive - int < 0")
    void test_positive_int_inf_0() {
        //when:
        var report = NumberValidation.positive(-1);

        //then:
        assertTrue(report.isInvalid());
        assertThat(report.getError()).isEqualTo("-1 is not > 0");
    }
}

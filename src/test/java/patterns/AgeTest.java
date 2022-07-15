package patterns;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AgeTest {

    @Test
    @DisplayName("test of - invalid")
    void test_of_invalid() {
        assertThatThrownBy(() -> Age.of(-1)) //when:
                .isInstanceOf(IllegalArgumentException.class); //then
    }

    @Test
    @DisplayName("test of - valid")
    void test_of_valid() {
        //given:
        var age = Age.of(1);

        //expect:
        assertThat(age.getAge()).isEqualTo(1);
    }
}

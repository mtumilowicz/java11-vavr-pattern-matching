import io.vavr.*;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.function.IntFunction;

import static io.vavr.API.*;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;
import static io.vavr.Predicates.exists;
import static io.vavr.Predicates.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by mtumilowicz on 2018-12-09.
 */
public class PatternMatchingTest {

    @Test
    public void of_withDefault() {
        IntFunction<String> transform = (int i) -> API.Match(i).of(
                Case($(1), "one"),
                Case($(), "default")
        );

        assertThat(transform.apply(1), is("one"));
        assertThat(transform.apply(2), is("default"));
    }

    @Test(expected = MatchError.class)
    public void of_withoutDefault() {
        API.Match(5).of(
        );
    }

    @Test
    public void option() {
        Option<Object> transformed = API.Match(5).option(
        );

        assertTrue(transformed.isEmpty());
    }

    @Test
    public void try_success_failure() {
        Function1<Try<Integer>, String> transform = (Try<Integer> _try) -> Match(_try).of(
                Case($Success($(1)), value -> "success 1"),
                Case($Success($()), value -> "success default"),
                Case($Failure($(instanceOf(IllegalArgumentException.class))), x -> "failure IllegalArgumentException"),
                Case($Failure($(instanceOf(NullPointerException.class))), x -> "failure NPE")
        );

        assertThat(transform.apply(Try.of(() -> 1)), is("success 1"));
        assertThat(transform.apply(Try.of(() -> 2)), is("success default"));
        assertThat(transform.apply(Try.of(() -> {
                    throw new IllegalArgumentException();
                })),
                is("failure IllegalArgumentException"));
        assertThat(transform.apply(Try.of(() -> {
                    throw new NullPointerException();
                })),
                is("failure NPE"));
    }

    @Test
    public void forAll() {
        List<Integer> evens = List.of(2, 4, 6);
        List<Integer> odds = List.of(1, 3, 5);
        List<Integer> randoms = List.of(1, 4);

        Function1<List<Integer>, String> check = (List<Integer> numbers) -> Match(numbers).of(
                Case($(Predicates.forAll(n -> n % 2 == 0)), "evens"),
                Case($(Predicates.forAll(n -> n % 2 != 0)), "odds"),
                Case($(), "neither evens nor odds")
        );
        
        assertThat(check.apply(evens), is("evens"));
        assertThat(check.apply(odds), is("odds"));
        assertThat(check.apply(randoms), is("neither evens nor odds"));
    }

    @Test
    public void forAll2() {
        List<Integer> evens = List.of(2, 4, 6);
        List<Integer> odds = List.of(1, 3, 5);
        List<Integer> randoms = List.of(1, 4);

        Function1<List<Integer>, String> check = (List<Integer> numbers) -> Match(numbers).of(
                Case($(exists(n -> n > 1000)), "> 1000"),
                Case($(exists(n -> n > 2000)), "> 2000"),
                Case($(), "neither evens nor odds")
        );

        assertThat(check.apply(evens), is("evens"));
        assertThat(check.apply(odds), is("odds"));
        assertThat(check.apply(randoms), is("neither evens nor odds"));
    }
}

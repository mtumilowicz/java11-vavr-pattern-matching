import io.vavr.API;
import io.vavr.Function1;
import io.vavr.MatchError;
import io.vavr.Predicates;
import io.vavr.collection.List;
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
        IntFunction<String> transform = (var i) -> API.Match(i).of(
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
        var transformed = API.Match(5).option(
        );

        assertTrue(transformed.isEmpty());
    }

    @Test
    public void try_success_failure() {
        Function1<Try<Integer>, String> transform = (var _try) -> Match(_try).of(
                Case($Success($(1)), value -> "success 1"),
                Case($Success($()), value -> "success default"),
                Case($Failure($(instanceOf(IllegalArgumentException.class))), err -> "failure IllegalArgumentException"),
                Case($Failure($(instanceOf(NullPointerException.class))), err -> "failure NPE")
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
        var evens = List.of(2, 4, 6);
        var odds = List.of(1, 3, 5);
        var randoms = List.of(1, 4);

        Function1<List<Integer>, String> check = (var numbers) -> Match(numbers).of(
                Case($(Predicates.forAll(n -> n % 2 == 0)), "evens"),
                Case($(Predicates.forAll(n -> n % 2 != 0)), "odds"),
                Case($(), "neither evens nor odds")
        );

        assertThat(check.apply(evens), is("evens"));
        assertThat(check.apply(odds), is("odds"));
        assertThat(check.apply(randoms), is("neither evens nor odds"));
    }

    @Test
    public void existsTest() {
        var moreThan2000 = List.of(2, 2001);
        var moreThan1000LessThan2000 = List.of(1500, 3);
        var lessThan1000 = List.of(0, 5);

        Function1<List<Integer>, String> check = (var numbers) -> Match(numbers).of(
                Case($(exists(n -> n > 2000)), "> 2000"),
                Case($(exists(n -> n > 1000)), "> 1000 & <= 2000"),
                Case($(), "<= 1000")
        );

        assertThat(check.apply(moreThan2000), is("> 2000"));
        assertThat(check.apply(moreThan1000LessThan2000), is("> 1000 & <= 2000"));
        assertThat(check.apply(lessThan1000), is("<= 1000"));
    }

    @Test
    public void planeService() {
        Function1<Integer, PlaneService> get = (var price) -> Match(price).of(
                Case($(n -> n > 3000), value -> PlaneService.BUSINESS_CLASS),
                Case($(Predicates.<Integer>allOf(n -> n > 2000, n -> n <= 3000)), value -> PlaneService.FIRST_CLASS),
                Case($(n -> n <= 2000), value -> PlaneService.ECONOMY_CLASS)
        );

        assertThat(get.apply(3500), is(PlaneService.BUSINESS_CLASS));
        assertThat(get.apply(2500), is(PlaneService.FIRST_CLASS));
        assertThat(get.apply(1500), is(PlaneService.ECONOMY_CLASS));
    }
}

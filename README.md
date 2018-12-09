# java11-vavr-pattern-matching
Overview of vavr pattern matching API.

_Reference_: https://www.vavr.io/vavr-docs/#_patterns  
_Reference_: http://blog.vavr.io/pattern-matching-essentials/  
_Reference_: https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/Patterns.html  
_Reference_: https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/Predicates.html

# preface
Pattern matching is a great feature that saves us 
from writing stacks of if-then-else branches. It 
reduces the amount of code while focusing on the 
relevant parts.

Vavr provides a match API that is close to Scala’s match.

* `$()` - wildcard pattern 
    * saves us from a `MatchError` which is 
    thrown if no case matches
    * it is often very handy to return `Option`
    as a way of programming no matches scenario
        ```
        Option<String> s = Match(i).option(
            Case($(0), "zero")
        );
        ```
* `$(value)` - equals pattern
* `$(predicate)` - conditional pattern



## predicates
Class `Predicates` defines general-purpose predicates which are 
particularly useful when working with `API.Match`.

|method   |description   |
|---|---|
|`allOf(Predicate<T>... predicates)`          |A combinator that checks if all of the given predicates are satisfied.   |
|`anyOf(Predicate<T>... predicates)`          |A combinator that checks if at least one of the given predicates is satisfies.   |
|`exists(Predicate<? super T> predicate)`     |A combinator that checks if one or more elements of an Iterable satisfy the predicate.   |
|`forAll(Predicate<? super T> predicate)`     |A combinator that checks if all elements of an Iterable satisfy the predicate.   |
|`instanceOf(Class<? extends T> type)`        |Creates a Predicate that tests, if an object is instance of the specified type.   |
|`is(T value)`                                |Creates a Predicate that tests, if an object is equal to the specified value using  Objects.equals(Object, Object) for comparison.   |
|`isIn(T... values)`                          |Creates a Predicate that tests, if an object is equal to at least one of the specified values using Objects.equals(Object, Object) for comparison.   |
|`isNotNull()`                                |Creates a Predicate that tests, if an object is not null   |
|`isNull()`                                   |Creates a Predicate that tests, if an object is null   |
|`noneOf(Predicate<T>... predicates)`         |A combinator that checks if none of the given predicates is satisfied.   |

## patterns
**Descriptions are not available in documentation**.

|method   |description   |
|---|---|
|`$Cons(API.Match.Pattern<_1,?> p1, API.Match.Pattern<_2,?> p2)`        |?   |
|`$Success(API.Match.Pattern<_1,?> p1)`    | ?  |
|`$Failure(API.Match.Pattern<_1,?> p1)`    | ?  |
|`$Future(API.Match.Pattern<_1,?> p1)`     | ?  |
|`$Invalid(API.Match.Pattern<_1,?> p1)`    | ?  |
|`$Valid(API.Match.Pattern<_1,?> p1)`      | ?  |
|`$Left(API.Match.Pattern<_1,?> p1)`       | ?  |
|`$Right(API.Match.Pattern<_1,?> p1)`      | ?  |
|`$Nil()`                                  | ?  |
|`$Some(API.Match.Pattern<_1,?> p1)`       | ?  |
|`$None()`                                 | ?  |
|`$TupleN(N...)`                           | ?  |

# project description
We provide basic examples for some methods mentioned above (
that seemed useful).

* ranges + `allOf`
    ```
    Function1<Integer, PlaneService> get = (var price) -> Match(price).of(
            Case($(n -> n > 3000), value -> PlaneService.BUSINESS_CLASS),
            Case($(Predicates.<Integer>allOf(n -> n > 2000, n -> n <= 3000)), value -> PlaneService.FIRST_CLASS),
            Case($(n -> n <= 2000), value -> PlaneService.ECONOMY_CLASS)
    );

    assertThat(get.apply(3500), is(PlaneService.BUSINESS_CLASS));
    assertThat(get.apply(2500), is(PlaneService.FIRST_CLASS));
    assertThat(get.apply(1500), is(PlaneService.ECONOMY_CLASS));
    ```
* `exists`
    ```
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
    ```
* `forAll`
    ```
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
    ```
* `Try`
    ```
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
    ```
* `option` vs `of`
    ```
    var transformed = API.Match(5).option(
    );
    
    assertTrue(transformed.isEmpty());
    ```
    ```
    @Test(expected = MatchError.class)
    public void of_withoutDefault() {
        API.Match(5).of(
        );
    }
    ```
    ```
    IntFunction<String> transform = (var i) -> API.Match(i).of(
            Case($(1), "one"),
            Case($(), "default")
    );
    
    assertThat(transform.apply(1), is("one"));
    assertThat(transform.apply(2), is("default"));
    ```
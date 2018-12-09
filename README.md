# java11-vavr-pattern-matching
Overview of vavr pattern matching API.

_Reference_: https://www.vavr.io/vavr-docs/#_patterns  
_Reference_: http://blog.vavr.io/pattern-matching-essentials/  
_Reference_: https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/Patterns.html  
_Reference_: https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/Predicates.html

# preface
## predicates
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

# project description
We provide basic examples of above mentioned methods.
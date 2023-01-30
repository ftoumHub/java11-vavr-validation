package libs.validation;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.Tuple;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;
import static io.vavr.Patterns.$Tuple2;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import fr.maif.commons.resourcewrapper.Message;
import io.vavr.API;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.Function5;
import io.vavr.Function6;
import io.vavr.Function7;
import io.vavr.Function8;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple4;
import io.vavr.Tuple5;
import io.vavr.Tuple6;
import io.vavr.Tuple7;
import io.vavr.collection.Seq;
import io.vavr.control.Either;

public final class Eithers {

    private Eithers() {
        // Utility class
    }

    public static <ERR extends Message, T, RESULT> Either<List<ERR>, List<RESULT>> sequence(
            List<T> values,
            Function<T, Either<List<ERR>, RESULT>> validation) {

        return Either.sequence(values.stream().map(validation).collect(toList()))
                .bimap(Seq::asJava, Seq::asJava)
                .mapLeft(flatten());
    }

    public static <ERR extends Message, T, RESULT> Either<List<ERR>, List<RESULT>> sequence(
            Set<T> values,
            Function<T, Either<List<ERR>, RESULT>> validation) {

        return Either.sequence(values.stream().map(validation).collect(toList()))
                .bimap(Seq::asJava, Seq::asJava).mapLeft(flatten());
    }

    public static <ERR extends Message, E1, E2, RESULT> Either<List<ERR>, RESULT> zip(Either<List<ERR>, E1> e1,
                                                                                          Either<List<ERR>, E2> e2,
                                                                                          BiFunction<E1, E2, RESULT> combine) {
        Tuple2<Either<List<ERR>, E1>, Either<List<ERR>, E2>> tuple = Tuple(e1, e2);

        return Match(tuple).of(
                Case($Tuple2($Right($()), $Right($())), (Either.Right<List<ERR>, E1> r1, Either.Right<List<ERR>, E2> r2) -> {
                    RESULT apply = combine.apply(r1.get(), r2.get());
                    return Either.<List<List<ERR>>, RESULT>right(apply);
                }),
                Case($Tuple2($Left($()), $Right($())), (Either.Left<List<ERR>, E1> l1, Either.Right<List<ERR>, E2> r2) ->
                        Either.<List<List<ERR>>, RESULT>left(API.List(l1.getLeft()).asJava())
                ),
                Case($Tuple2($Right($()), $Left($())), (Either.Right<List<ERR>, E1> r1, Either.Left<List<ERR>, E2> l2) ->
                        Either.<List<List<ERR>>, RESULT>left(API.List(l2.getLeft()).asJava())
                ),
                Case($Tuple2($Left($()), $Left($())), (Either.Left<List<ERR>, E1> l1, Either.Left<List<ERR>, E2> l2) ->
                        Either.<List<List<ERR>>, RESULT>left(API.List(l1.getLeft(), l2.getLeft()).asJava()))
        ).mapLeft(flatten());
    }

    public static <ERR extends Message, E1, E2, RESULT> Either<List<ERR>, RESULT> zipEnd(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Supplier<RESULT> value) {
        return zip(e1, e2, (ee1, ee2) -> value.get());
    }

    public static <ERR extends Message, E1, E2, E3, RESULT> Either<List<ERR>, RESULT> zip(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Function3<E1, E2, E3, RESULT> combine) {
        Either<List<ERR>, Tuple2<E1, E2>> e1e2e = zip(e1, e2, Tuple::of);

        return zip(e1e2e, e3, (t, value) -> combine.apply(t._1, t._2, value));
    }

    public static <ERR extends Message, E1, E2, E3, RESULT> Either<List<ERR>, RESULT> zipEnd(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Supplier<RESULT> value) {

        return zip(e1, e2, e3, (ee1, ee2, ee3) -> value.get());
    }

    public static <ERR extends Message, E1, E2, E3, E4, RESULT> Either<List<ERR>, RESULT> zip(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Either<List<ERR>, E4> e4,
            Function4<E1, E2, E3, E4, RESULT> combine) {
        Either<List<ERR>, Tuple3<E1, E2, E3>> e1e2e3 = zip(e1, e2, e3, Tuple::of);
        return zip(e1e2e3, e4, (t, value) -> combine.apply(t._1, t._2, t._3, value));
    }

    public static <ERR extends Message, E1, E2, E3, E4, RESULT> Either<List<ERR>, RESULT> zipEnd(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Either<List<ERR>, E4> e4,
            Supplier<RESULT> value) {
        return zip(e1, e2, e3, e4, (ee1, ee2, ee3, ee4) -> value.get());
    }

    public static <ERR extends Message, E1, E2, E3, E4, E5, RESULT> Either<List<ERR>, RESULT> zip(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Either<List<ERR>, E4> e4,
            Either<List<ERR>, E5> e5,
            Function5<E1, E2, E3, E4, E5, RESULT> combine) {
        Either<List<ERR>, Tuple4<E1, E2, E3, E4>> e1e2e3e4 = zip(e1, e2, e3, e4, Tuple::of);
        return zip(e1e2e3e4, e5, (t, value) -> combine.apply(t._1, t._2, t._3, t._4, value));
    }

    public static <ERR extends Message, E1, E2, E3, E4, E5, RESULT> Either<List<ERR>, RESULT> zipEnd(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Either<List<ERR>, E4> e4,
            Either<List<ERR>, E5> e5,
            Supplier<RESULT> value) {
        return zip(e1, e2, e3, e4, e5, (ee1, ee2, ee3, ee4, ee5) -> value.get());
    }

    public static <ERR extends Message, E1, E2, E3, E4, E5, E6, RESULT> Either<List<ERR>, RESULT> zip(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Either<List<ERR>, E4> e4,
            Either<List<ERR>, E5> e5,
            Either<List<ERR>, E6> e6,
            Function6<E1, E2, E3, E4, E5, E6, RESULT> combine) {
        Either<List<ERR>, Tuple5<E1, E2, E3, E4, E5>> e1e2e3e4e5 = zip(e1, e2, e3, e4, e5, Tuple::of);
        return zip(e1e2e3e4e5, e6, (t, value) -> combine.apply(t._1, t._2, t._3, t._4, t._5, value));
    }

    public static <ERR extends Message, E1, E2, E3, E4, E5, E6, RESULT> Either<List<ERR>, RESULT> zipEnd(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Either<List<ERR>, E4> e4,
            Either<List<ERR>, E5> e5,
            Either<List<ERR>, E6> e6,
            Supplier<RESULT> value) {
        return zip(e1, e2, e3, e4, e5, e6, (ee1, ee2, ee3, ee4, ee5, ee6) -> value.get());
    }

    public static <ERR extends Message, E1, E2, E3, E4, E5, E6, E7, RESULT> Either<List<ERR>, RESULT> zip(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Either<List<ERR>, E4> e4,
            Either<List<ERR>, E5> e5,
            Either<List<ERR>, E6> e6,
            Either<List<ERR>, E7> e7,
            Function7<E1, E2, E3, E4, E5, E6, E7, RESULT> combine) {
        Either<List<ERR>, Tuple6<E1, E2, E3, E4, E5, E6>> e1e2e3e4e5E6 = zip(e1, e2, e3, e4, e5, e6, Tuple::of);
        return zip(e1e2e3e4e5E6, e7, (t, value) -> combine.apply(t._1, t._2, t._3, t._4, t._5, t._6, value));
    }

    public static <ERR extends Message, E1, E2, E3, E4, E5, E6, E7, RESULT> Either<List<ERR>, RESULT> zipEnd(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Either<List<ERR>, E4> e4,
            Either<List<ERR>, E5> e5,
            Either<List<ERR>, E6> e6,
            Either<List<ERR>, E7> e7,
            Supplier<RESULT> value) {
        return zip(e1, e2, e3, e4, e5, e6, e7, (ee1, ee2, ee3, ee4, ee5, ee6, ee7) -> value.get());
    }

    public static <ERR extends Message, E1, E2, E3, E4, E5, E6, E7, E8, RESULT> Either<List<ERR>, RESULT> zip(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Either<List<ERR>, E4> e4,
            Either<List<ERR>, E5> e5,
            Either<List<ERR>, E6> e6,
            Either<List<ERR>, E7> e7,
            Either<List<ERR>, E8> e8,
            Function8<E1, E2, E3, E4, E5, E6, E7, E8, RESULT> combine) {
        Either<List<ERR>, Tuple7<E1, E2, E3, E4, E5, E6, E7>> e1e2e3e4e5e6e7 = zip(e1, e2, e3, e4, e5, e6, e7, Tuple::of);
        return zip(e1e2e3e4e5e6e7, e8, (t, value) -> combine.apply(t._1, t._2, t._3, t._4, t._5, t._6, t._7, value));
    }

    public static <ERR extends Message, E1, E2, E3, E4, E5, E6, E7, E8, RESULT> Either<List<ERR>, RESULT> zipEnd(
            Either<List<ERR>, E1> e1,
            Either<List<ERR>, E2> e2,
            Either<List<ERR>, E3> e3,
            Either<List<ERR>, E4> e4,
            Either<List<ERR>, E5> e5,
            Either<List<ERR>, E6> e6,
            Either<List<ERR>, E7> e7,
            Either<List<ERR>, E8> e8,
            Supplier<RESULT> value) {
        return zip(e1, e2, e3, e4, e5, e6, e7, e8, (ee1, ee2, ee3, ee4, ee5, ee6, ee7, ee8) -> value.get());
    }

    public static <T> Function<List<List<T>>, List<T>> flatten() {
        return listOfList -> listOfList.stream().flatMap(List::stream).collect(toList());
    }
}


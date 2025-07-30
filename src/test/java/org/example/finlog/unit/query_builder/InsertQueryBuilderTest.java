package org.example.finlog.unit.query_builder;

import org.example.finlog.query_builder.builder.InsertQueryBuilder;
import org.example.finlog.query_builder.util.RawExpression;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.example.finlog.query_builder.util.RawExpression.raw;

public class InsertQueryBuilderTest {

    @Test
    void shouldBuildCorrectly() {
        String expected = "INSERT INTO \"table\" " +
                          "(\"f1\", \"f2\", \"f3\") " +
                          "VALUES (100, 'hello', 3.14)";

        String query = InsertQueryBuilder.builder()
                .insertInto("table")
                .fields("f1", "f2", "f3")
                .values(100, "hello", 3.14)
                .build();

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void shouldThrowsWhenValuesNumberNotMatchFieldsNumber() {
        assertThatThrownBy(() ->
                InsertQueryBuilder.builder()
                        .insertInto("table")
                        .fields("f1", "f2", "f3")
                        .values(1, 2, 3, 4)
                        .build()
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "The number of VALUES parameters " +
                        "must match with the number of fields"
                );
    }

    @Test
    void shouldThrowsWhenZeroFields() {
        assertThatThrownBy(() ->
                InsertQueryBuilder.builder()
                        .insertInto("table")
                        .fields()
                        .values(1, 2, 3, 4)
                        .build()
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "Zero fields are not allowed"
                );
    }

    @Test
    void shouldBuildWhenSetRawExpressionAsValue() {
        String expected = "INSERT INTO \"table\" " +
                          "(\"f1\", \"f2\", \"f3\") " +
                          "VALUES ('foo + bar', GREATEST(1, 11), 3.14 * 41.3)";

        String query = InsertQueryBuilder.builder()
                .insertInto("table")
                .fields("f1", "f2", "f3")
                .values(
                        "foo + bar", // should be escaped
                        raw("GREATEST(1, 11)"),
                        raw("3.14 * 41.3")
                ).build();

        assertThat(query).isEqualTo(expected);
    }

//    This code won't compile - values() can't come before insertInto()
//
//    @Test
//    void shouldNotCompileWhenValuesBeforeInsertInto() {
//        InsertQueryBuilder.builder().values(1, 2, 3).insertInto("table");
//
//    }
}

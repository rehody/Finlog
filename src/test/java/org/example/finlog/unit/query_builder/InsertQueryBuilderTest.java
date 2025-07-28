package org.example.finlog.unit.query_builder;

import org.example.finlog.query_builder.dsl.InsertBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class InsertQueryBuilderTest {

    @Test
    void shouldBuildCorrectly() {
        String expected = "INSERT INTO \"table\" " +
                          "(\"f1\", \"f2\", \"f3\") " +
                          "VALUES (100, 'hello', 3.14)";

        String query = InsertBuilder.builder()
                .insertInto("table")
                .fields("f1", "f2", "f3")
                .values(100, "hello", 3.14)
                .build();

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void shouldThrowsWhenValuesNumberNotMatchFieldsNumber() {
        assertThatThrownBy(() ->
                InsertBuilder.builder()
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
                InsertBuilder.builder()
                        .insertInto("table")
                        .fields()
                        .values(1, 2, 3, 4)
                        .build()
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "Zero fields are not allowed"
                );
    }


//    This code won't compile - values() can't come before insertInto()
//
//    @Test
//    void shouldNotCompileWhenValuesBeforeInsertInto() {
//        InsertBuilder.builder().values(1, 2, 3).insertInto("table");
//
//    }
}

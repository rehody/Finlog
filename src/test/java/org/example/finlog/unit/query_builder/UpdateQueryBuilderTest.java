package org.example.finlog.unit.query_builder;

import org.example.finlog.query_builder.builder.UpdateQueryBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class UpdateQueryBuilderTest {

    @Test
    void shouldBuildWithoutWhereClause() {
        String expected = "UPDATE \"table\" " +
                          "SET \"f1\" = 1, \"f2\" = 2, \"f3\" = 3";

        String query = UpdateQueryBuilder.builder()
                .update("table")
                .set("f1", "f2", "f3")
                .values(1, 2, 3)
                .build();

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void shouldBuildWithWhereClause() {
        String expected = "UPDATE \"table\" " +
                          "SET \"f1\" = 1, \"f2\" = 2, \"f3\" = 3 " +
                          "WHERE \"f1\" = 21 " +
                          "AND \"f2\" < 'smth big' " +
                          "OR \"f3\" BETWEEN 100 AND 200";

        String query = UpdateQueryBuilder.builder()
                .update("table")
                .set("f1", "f2", "f3")
                .values(1, 2, 3)
                .where("f1").eq(21)
                .and("f2").lessThan("smth big")
                .or("f3").between(100, 200)
                .build();

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void shouldThrowsWhenFieldsMismatch() {
        assertThatThrownBy(() ->
                UpdateQueryBuilder.builder()
                        .update("table")
                        .set("f1", "f2", "f3")
                        .values(1, 2, 3, 4)
                        .build()
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("The number of VALUES parameters " +
                                      "must match with the number of fields");
    }

    @Test
    void shouldThrowsWhenZeroFields() {
        assertThatThrownBy(() ->
                UpdateQueryBuilder.builder()
                        .update("table")
                        .set()
                        .values(1, 2, 3, 4)
                        .build()
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Zero fields are not allowed");
    }


//    This code won't compile - where() can't come before set()
//
//    @Test
//    void shouldNotCompileWhenWhereBeforeSet() {
//        UpdateQueryBuilder.builder().update("table").where("f1").set("f2");
//
//    }
}

package org.example.finlog.unit.query_builder;

import org.example.finlog.query_builder.dsl.SelectBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SelectQueryBuilderTest {

    @Test
    void shouldBuildWithoutWhereClause() {
        String expected = "SELECT \"f1\", \"f2\", \"f3\" FROM \"table\"";

        String query = SelectBuilder.builder()
                .select("f1", "f2", "f3")
                .from("table")
                .build();

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void shouldBuildWithWhereClause() {
        String expected = "SELECT \"f1\", \"f2\", \"f3\" " +
                          "FROM \"table\" " +
                          "WHERE \"f1\" = NULL " +
                          "AND \"f2\" BETWEEN 100 AND 200 " +
                          "OR \"f3\" < 'f4'";

        String query = SelectBuilder.builder()
                .select("f1", "f2", "f3")
                .from("table")
                .where("f1").eq(null)
                .and("f2").between(100, 200)
                .or("f3").lessThan("f4")
                .build();

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void shouldBuildWithOrderAndLimitWithoutWhere() {
        String expected = "SELECT \"f1\", \"f2\", \"f3\" " +
                          "FROM \"table\" " +
                          "ORDER BY \"f1\" LIMIT 100";

        String query = SelectBuilder.builder()
                .select("f1", "f2", "f3")
                .from("table")
                .orderBy("f1")
                .limit(100)
                .build();

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void shouldBuildWithWhereAndOrderByAndLimit() {
        String expected = "SELECT \"f1\", \"f2\", \"f3\" " +
                          "FROM \"table\" " +
                          "WHERE \"f1\" = NULL " +
                          "AND \"f2\" BETWEEN 100 AND 200 " +
                          "OR \"f3\" != 'smth' " +
                          "ORDER BY \"f1\" " +
                          "LIMIT 100";

        String query = SelectBuilder.builder()
                .select("f1", "f2", "f3")
                .from("table")
                .where("f1").eq(null)
                .and("f2").between(100, 200)
                .or("f3").notEq("smth")
                .orderBy("f1")
                .limit(100)
                .build();

        assertThat(query).isEqualTo(expected);
    }
}

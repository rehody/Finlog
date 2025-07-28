package org.example.finlog.unit.query_builder;

import org.example.finlog.query_builder.dsl.SelectBuilder;
import org.example.finlog.query_builder.util.QueryFormatter;
import org.example.finlog.util.TableName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SelectQueryBuilderTest {

    private String table;
    private String formattedTable;
    private String[] fields;
    private String formattedFields;

    @BeforeEach
    void setUp() {
        table = TableName.TRANSACTION;
        formattedTable = QueryFormatter.escapeIdentifier(table);

        fields = new String[]{"f1", "f2", "f3"};
        formattedFields = Arrays.stream(fields)
                .map(QueryFormatter::escapeIdentifier)
                .collect(Collectors.joining(", "));


    }

    @Test
    void shouldBuildWithoutWhereClause() {
        String expected = "SELECT " + formattedFields + " FROM " + formattedTable;

        String query = SelectBuilder.builder()
                .select(fields)
                .from(table)
                .build();

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void shouldBuildWithWhereClause() {
        String expected = "SELECT " + formattedFields + " " +
                          "FROM " + formattedTable + " " +
                          "WHERE \"f1\" = NULL " +
                          "AND \"f2\" BETWEEN 100 AND 200 " +
                          "OR \"f3\" < 'f4'";

        String query = SelectBuilder.builder()
                .select(fields)
                .from(table)
                .where("f1").eq(null)
                .and("f2").between(100, 200)
                .or("f3").lessThan("f4")
                .build();

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void shouldBuildWithOrderAndLimitWithoutWhere() {
        String expected = "SELECT " + formattedFields + " " +
                          "FROM " + formattedTable + " " +
                          "ORDER BY \"f1\" LIMIT 100";

        String query = SelectBuilder.builder()
                .select(fields)
                .from(table)
                .orderBy("f1")
                .limit(100)
                .build();

        assertThat(query).isEqualTo(expected);
    }

    @Test
    void shouldBuildWithWhereAndOrderByAndLimit() {
        String expected = "SELECT " + formattedFields + " " +
                          "FROM " + formattedTable + " " +
                          "WHERE \"f1\" = NULL " +
                          "AND \"f2\" BETWEEN 100 AND 200 " +
                          "OR \"f3\" != 'smth' " +
                          "ORDER BY \"f1\" " +
                          "LIMIT 100";

        String query = SelectBuilder.builder()
                .select(fields)
                .from(table)
                .where("f1").eq(null)
                .and("f2").between(100, 200)
                .or("f3").notEq("smth")
                .orderBy("f1")
                .limit(100)
                .build();

        assertThat(query).isEqualTo(expected);
    }
}

package org.example.finlog.unit.query_builder;

import org.example.finlog.query_builder.dsl.InsertBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
}

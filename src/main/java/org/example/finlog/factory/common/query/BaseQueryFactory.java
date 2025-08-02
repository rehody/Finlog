package org.example.finlog.factory.common.query;

import org.example.finlog.query_builder.builder.InsertQueryBuilder;
import org.example.finlog.query_builder.builder.SelectQueryBuilder;
import org.example.finlog.query_builder.builder.UpdateQueryBuilder;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import static org.example.finlog.query_builder.util.RawExpression.raw;

public abstract class BaseQueryFactory {


//    *** SELECT SECTION ***

    public static String getByField(String table, String field, Object value) {
        return SelectQueryBuilder.builder()
                .select()
                .from(table)
                .where(field).eq(value)
                .and("deleted").eq(false)
                .build();
    }

    public static String getSingleField(String table, String field, UUID id) {
        return SelectQueryBuilder.builder()
                .select(field)
                .from(table)
                .where("id").eq(id)
                .and("deleted").eq(false)
                .build();
    }


//    *** INSERT SECTION ***

    public static String save(String table, String[] fields, Object[] values) {
        return InsertQueryBuilder.builder()
                .insertInto(table)
                .fields(fields)
                .values(values)
                .build();
    }


//    *** UPDATE SECTION ***

    public static String delete(String table, UUID id, Long version) {
        String[] fields = {"deleted", "deleted_at", "version"};
        Object[] values = {
                true,
                raw("NOW()"),
                raw("version + 1")
        };
        return buildUpdate(table, id, version, fields, values);
    }

    public static String update(String table, UUID id, Long version, String[] fields, Object[] values) {
        String[] metadataFields = {"version", "updated_at"};
        Object[] metadataValues = {raw("version + 1"), raw("NOW()")};

        String[] fieldsWithMetadata = combine(fields, metadataFields, String[]::new);
        Object[] valuesWithMetadata = combine(values, metadataValues, Object[]::new);

        return buildUpdate(table, id, version, fieldsWithMetadata, valuesWithMetadata);
    }

    private static String buildUpdate(String table, UUID id, Long version, String[] fields, Object[] values) {
        return UpdateQueryBuilder.builder()
                .update(table)
                .set(fields)
                .values(values)
                .where("id").eq(id)
                .and("deleted").eq(false)
                .and("version").eq(version)
                .build();
    }

    private static <T> T[] combine(T[] arr1, T[] arr2, IntFunction<T[]> generator) {
        return Stream.concat(
                Arrays.stream(arr1),
                Arrays.stream(arr2)
        ).toArray(generator);
    }
}

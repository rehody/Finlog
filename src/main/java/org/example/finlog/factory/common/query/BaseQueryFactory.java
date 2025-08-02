package org.example.finlog.factory.common.query;

import org.example.finlog.query_builder.builder.InsertQueryBuilder;
import org.example.finlog.query_builder.builder.SelectQueryBuilder;

import java.util.UUID;

public abstract class BaseQueryFactory {

    public static String save(String table, String[] fields, Object[] values) {
        return InsertQueryBuilder.builder()
                .insertInto(table)
                .fields(fields)
                .values(values)
                .build();
    }

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
}

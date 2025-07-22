package org.example.finlog.util;

public record QueryResponse(String sql, Object[] params) {
}

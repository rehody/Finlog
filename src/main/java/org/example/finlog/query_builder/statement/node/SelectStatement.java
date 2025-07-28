package org.example.finlog.query_builder.statement.node;

import lombok.Getter;
import lombok.Setter;
import org.example.finlog.query_builder.statement.expression.LogicalExpression;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SelectStatement implements QueryNode {
    private String[] fields;
    private String table;
    private List<LogicalExpression> where = new ArrayList<>();
    private String orderBy;
    private Integer limit;
}

package org.example.finlog.query_builder.ast.node;

import lombok.Getter;
import lombok.Setter;
import org.example.finlog.query_builder.ast.expression.Expression;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SelectStatement implements QueryNode {
    private String[] fields;
    private String table;
    private List<Expression> where = new ArrayList<>();
    private String orderBy;
    private Integer limit;
}

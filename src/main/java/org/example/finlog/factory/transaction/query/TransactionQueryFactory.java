package org.example.finlog.factory.transaction.query;

import org.example.finlog.factory.common.query.BaseQueryFactory;
import org.example.finlog.util.TableName;

public abstract class TransactionQueryFactory extends BaseQueryFactory {
    protected static final String TABLE = TableName.TRANSACTION;
}

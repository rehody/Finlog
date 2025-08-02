package org.example.finlog.factory.user.query;

import org.example.finlog.factory.common.query.BaseQueryFactory;
import org.example.finlog.util.TableName;

public abstract class UserQueryFactory extends BaseQueryFactory {
    protected final static String TABLE = TableName.USER;
}

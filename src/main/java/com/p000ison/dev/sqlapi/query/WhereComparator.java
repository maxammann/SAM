package com.p000ison.dev.sqlapi.query;

/**
 *
 */
public interface WhereComparator {

    WhereQuery or();

    WhereQuery and();

    SelectQuery select();
}

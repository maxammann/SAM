package com.p000ison.dev.sqlapi.query;

/**
 * Represents a CompareOperator
 */
public enum CompareOperator {
    EQUALS("="), GREATER_THAN(">"), LESS_THAN("<");

    private String sign;

    private CompareOperator(String sign)
    {
        this.sign = sign;
    }

    @Override
    public String toString()
    {
        return sign;
    }
}

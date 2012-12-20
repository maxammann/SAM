package com.p000ison.dev.sqlapi;

import java.util.Collection;
import java.util.List;

/**
 * Represents a PreparedQuery
 */
public interface PreparedQuery<T extends TableObject> {

    void set(int index, Object value);

    <C extends Collection<T>> C getResults(C collection);

    List<T> getResults();
}

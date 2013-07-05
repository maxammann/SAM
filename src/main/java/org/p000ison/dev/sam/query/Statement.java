package org.p000ison.dev.sam.query;

/**
 *
 */
public interface Statement {

	String getQuery();

	PreparedQuery prepare();
}

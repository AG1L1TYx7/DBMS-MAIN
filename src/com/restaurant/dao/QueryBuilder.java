package com.restaurant.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Fluent SQL Query Builder for constructing type-safe queries.
 * Helps prevent SQL injection and improves query readability.
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public class QueryBuilder {

    private final StringBuilder sql;
    private final List<Object> parameters;
    private boolean hasWhere;
    private boolean hasOrderBy;
    private boolean hasGroupBy;

    private QueryBuilder() {
        this.sql = new StringBuilder();
        this.parameters = new ArrayList<>();
        this.hasWhere = false;
        this.hasOrderBy = false;
        this.hasGroupBy = false;
    }

    // ==================== Static Factory Methods ====================

    /**
     * Creates a SELECT query builder.
     *
     * @param columns the columns to select (use * for all)
     * @return the QueryBuilder
     */
    public static QueryBuilder select(String... columns) {
        QueryBuilder builder = new QueryBuilder();
        builder.sql.append("SELECT ");
        if (columns.length == 0) {
            builder.sql.append("*");
        } else {
            builder.sql.append(String.join(", ", columns));
        }
        return builder;
    }

    /**
     * Creates a SELECT DISTINCT query builder.
     *
     * @param columns the columns to select
     * @return the QueryBuilder
     */
    public static QueryBuilder selectDistinct(String... columns) {
        QueryBuilder builder = new QueryBuilder();
        builder.sql.append("SELECT DISTINCT ");
        if (columns.length == 0) {
            builder.sql.append("*");
        } else {
            builder.sql.append(String.join(", ", columns));
        }
        return builder;
    }

    /**
     * Creates a SELECT COUNT query builder.
     *
     * @param column the column to count (use * for all rows)
     * @return the QueryBuilder
     */
    public static QueryBuilder selectCount(String column) {
        QueryBuilder builder = new QueryBuilder();
        builder.sql.append("SELECT COUNT(").append(column).append(")");
        return builder;
    }

    /**
     * Creates an INSERT query builder.
     *
     * @param table the table name
     * @return the QueryBuilder
     */
    public static QueryBuilder insertInto(String table) {
        QueryBuilder builder = new QueryBuilder();
        builder.sql.append("INSERT INTO ").append(table);
        return builder;
    }

    /**
     * Creates an UPDATE query builder.
     *
     * @param table the table name
     * @return the QueryBuilder
     */
    public static QueryBuilder update(String table) {
        QueryBuilder builder = new QueryBuilder();
        builder.sql.append("UPDATE ").append(table);
        return builder;
    }

    /**
     * Creates a DELETE query builder.
     *
     * @param table the table name
     * @return the QueryBuilder
     */
    public static QueryBuilder deleteFrom(String table) {
        QueryBuilder builder = new QueryBuilder();
        builder.sql.append("DELETE FROM ").append(table);
        return builder;
    }

    // ==================== Query Building Methods ====================

    /**
     * Adds a FROM clause.
     *
     * @param table the table name
     * @return this QueryBuilder
     */
    public QueryBuilder from(String table) {
        sql.append(" FROM ").append(table);
        return this;
    }

    /**
     * Adds a JOIN clause.
     *
     * @param table the table to join
     * @param condition the join condition
     * @return this QueryBuilder
     */
    public QueryBuilder join(String table, String condition) {
        sql.append(" JOIN ").append(table).append(" ON ").append(condition);
        return this;
    }

    /**
     * Adds a LEFT JOIN clause.
     *
     * @param table the table to join
     * @param condition the join condition
     * @return this QueryBuilder
     */
    public QueryBuilder leftJoin(String table, String condition) {
        sql.append(" LEFT JOIN ").append(table).append(" ON ").append(condition);
        return this;
    }

    /**
     * Adds a RIGHT JOIN clause.
     *
     * @param table the table to join
     * @param condition the join condition
     * @return this QueryBuilder
     */
    public QueryBuilder rightJoin(String table, String condition) {
        sql.append(" RIGHT JOIN ").append(table).append(" ON ").append(condition);
        return this;
    }

    /**
     * Adds columns and values for INSERT.
     *
     * @param columns the column names
     * @return this QueryBuilder
     */
    public QueryBuilder columns(String... columns) {
        sql.append(" (").append(String.join(", ", columns)).append(")");
        return this;
    }

    /**
     * Adds VALUES placeholders for INSERT.
     *
     * @param values the values to insert
     * @return this QueryBuilder
     */
    public QueryBuilder values(Object... values) {
        StringJoiner joiner = new StringJoiner(", ", " VALUES (", ")");
        for (Object value : values) {
            joiner.add("?");
            parameters.add(value);
        }
        sql.append(joiner);
        return this;
    }

    /**
     * Adds a SET clause for UPDATE.
     *
     * @param column the column name
     * @param value the new value
     * @return this QueryBuilder
     */
    public QueryBuilder set(String column, Object value) {
        if (!sql.toString().contains(" SET ")) {
            sql.append(" SET ");
        } else {
            sql.append(", ");
        }
        sql.append(column).append(" = ?");
        parameters.add(value);
        return this;
    }

    /**
     * Adds a WHERE clause.
     *
     * @param condition the condition
     * @return this QueryBuilder
     */
    public QueryBuilder where(String condition) {
        if (!hasWhere) {
            sql.append(" WHERE ").append(condition);
            hasWhere = true;
        } else {
            sql.append(" AND ").append(condition);
        }
        return this;
    }

    /**
     * Adds a WHERE clause with a parameter.
     *
     * @param column the column name
     * @param operator the comparison operator (=, <, >, <=, >=, !=, LIKE)
     * @param value the value to compare
     * @return this QueryBuilder
     */
    public QueryBuilder where(String column, String operator, Object value) {
        where(column + " " + operator + " ?");
        parameters.add(value);
        return this;
    }

    /**
     * Adds a WHERE column = value clause.
     *
     * @param column the column name
     * @param value the value
     * @return this QueryBuilder
     */
    public QueryBuilder whereEquals(String column, Object value) {
        return where(column, "=", value);
    }

    /**
     * Adds a WHERE column LIKE value clause.
     *
     * @param column the column name
     * @param pattern the LIKE pattern
     * @return this QueryBuilder
     */
    public QueryBuilder whereLike(String column, String pattern) {
        return where(column, "LIKE", pattern);
    }

    /**
     * Adds a WHERE column IN (...) clause.
     *
     * @param column the column name
     * @param values the values
     * @return this QueryBuilder
     */
    public QueryBuilder whereIn(String column, List<?> values) {
        if (values == null || values.isEmpty()) {
            where("1 = 0"); // Always false - empty IN clause
            return this;
        }
        
        StringJoiner placeholders = new StringJoiner(", ", "(", ")");
        for (Object value : values) {
            placeholders.add("?");
            parameters.add(value);
        }
        where(column + " IN " + placeholders);
        return this;
    }

    /**
     * Adds a WHERE column BETWEEN value1 AND value2 clause.
     *
     * @param column the column name
     * @param start the start value
     * @param end the end value
     * @return this QueryBuilder
     */
    public QueryBuilder whereBetween(String column, Object start, Object end) {
        where(column + " BETWEEN ? AND ?");
        parameters.add(start);
        parameters.add(end);
        return this;
    }

    /**
     * Adds a WHERE column IS NULL clause.
     *
     * @param column the column name
     * @return this QueryBuilder
     */
    public QueryBuilder whereNull(String column) {
        return where(column + " IS NULL");
    }

    /**
     * Adds a WHERE column IS NOT NULL clause.
     *
     * @param column the column name
     * @return this QueryBuilder
     */
    public QueryBuilder whereNotNull(String column) {
        return where(column + " IS NOT NULL");
    }

    /**
     * Adds an AND clause.
     *
     * @param condition the condition
     * @return this QueryBuilder
     */
    public QueryBuilder and(String condition) {
        sql.append(" AND ").append(condition);
        return this;
    }

    /**
     * Adds an OR clause.
     *
     * @param condition the condition
     * @return this QueryBuilder
     */
    public QueryBuilder or(String condition) {
        sql.append(" OR ").append(condition);
        return this;
    }

    /**
     * Adds a GROUP BY clause.
     *
     * @param columns the columns to group by
     * @return this QueryBuilder
     */
    public QueryBuilder groupBy(String... columns) {
        if (!hasGroupBy) {
            sql.append(" GROUP BY ").append(String.join(", ", columns));
            hasGroupBy = true;
        }
        return this;
    }

    /**
     * Adds a HAVING clause.
     *
     * @param condition the HAVING condition
     * @return this QueryBuilder
     */
    public QueryBuilder having(String condition) {
        sql.append(" HAVING ").append(condition);
        return this;
    }

    /**
     * Adds an ORDER BY clause.
     *
     * @param column the column to order by
     * @return this QueryBuilder
     */
    public QueryBuilder orderBy(String column) {
        if (!hasOrderBy) {
            sql.append(" ORDER BY ").append(column);
            hasOrderBy = true;
        } else {
            sql.append(", ").append(column);
        }
        return this;
    }

    /**
     * Adds an ORDER BY column ASC clause.
     *
     * @param column the column
     * @return this QueryBuilder
     */
    public QueryBuilder orderByAsc(String column) {
        return orderBy(column + " ASC");
    }

    /**
     * Adds an ORDER BY column DESC clause.
     *
     * @param column the column
     * @return this QueryBuilder
     */
    public QueryBuilder orderByDesc(String column) {
        return orderBy(column + " DESC");
    }

    /**
     * Adds a LIMIT clause.
     *
     * @param limit the maximum number of rows
     * @return this QueryBuilder
     */
    public QueryBuilder limit(int limit) {
        sql.append(" LIMIT ?");
        parameters.add(limit);
        return this;
    }

    /**
     * Adds a LIMIT with OFFSET clause.
     *
     * @param limit the maximum number of rows
     * @param offset the starting offset
     * @return this QueryBuilder
     */
    public QueryBuilder limit(int limit, int offset) {
        sql.append(" LIMIT ? OFFSET ?");
        parameters.add(limit);
        parameters.add(offset);
        return this;
    }

    /**
     * Adds a FOR UPDATE clause (row locking).
     *
     * @return this QueryBuilder
     */
    public QueryBuilder forUpdate() {
        sql.append(" FOR UPDATE");
        return this;
    }

    /**
     * Adds a parameter manually.
     *
     * @param value the parameter value
     * @return this QueryBuilder
     */
    public QueryBuilder addParameter(Object value) {
        parameters.add(value);
        return this;
    }

    // ==================== Build Methods ====================

    /**
     * Builds the SQL query string.
     *
     * @return the SQL query
     */
    public String build() {
        return sql.toString();
    }

    /**
     * Gets the parameters list.
     *
     * @return the parameters
     */
    public List<Object> getParameters() {
        return new ArrayList<>(parameters);
    }

    /**
     * Gets the parameters as an array.
     *
     * @return the parameters array
     */
    public Object[] getParametersArray() {
        return parameters.toArray();
    }

    /**
     * Returns the built query for debugging.
     *
     * @return string representation of the query
     */
    @Override
    public String toString() {
        return String.format("Query: %s | Parameters: %s", sql, parameters);
    }
}

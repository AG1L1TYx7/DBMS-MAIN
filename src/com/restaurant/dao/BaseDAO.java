package com.restaurant.dao;

import com.restaurant.config.HikariConnectionPool;
import com.restaurant.exception.DatabaseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base DAO class providing common database operations.
 * All DAO implementations should extend this class to leverage:
 * - Connection pooling via HikariCP
 * - Consistent error handling
 * - Query execution templates
 * - Transaction support
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public abstract class BaseDAO {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final HikariConnectionPool connectionPool;

    protected BaseDAO() {
        this.connectionPool = HikariConnectionPool.getInstance();
    }

    /**
     * Gets a connection from the pool.
     *
     * @return a database connection
     * @throws SQLException if connection cannot be obtained
     */
    protected Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    /**
     * Executes a query and maps results to a list of objects.
     *
     * @param sql the SQL query
     * @param mapper the function to map ResultSet rows to objects
     * @param params the query parameters
     * @param <T> the type of objects to return
     * @return list of mapped objects
     * @throws SQLException if query execution fails
     */
    protected <T> List<T> executeQuery(String sql, ResultSetMapper<T> mapper, Object... params) 
            throws SQLException {
        List<T> results = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setParameters(stmt, params);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        }
        
        return results;
    }

    /**
     * Executes a query and returns a single optional result.
     *
     * @param sql the SQL query
     * @param mapper the function to map ResultSet to object
     * @param params the query parameters
     * @param <T> the type of object to return
     * @return Optional containing the result, or empty if not found
     * @throws SQLException if query execution fails
     */
    protected <T> Optional<T> executeQueryForObject(String sql, ResultSetMapper<T> mapper, Object... params) 
            throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setParameters(stmt, params);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.map(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Executes an update/insert/delete statement.
     *
     * @param sql the SQL statement
     * @param params the statement parameters
     * @return number of affected rows
     * @throws SQLException if execution fails
     */
    protected int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes an insert and returns the generated key.
     *
     * @param sql the SQL insert statement
     * @param params the statement parameters
     * @return the generated key, or -1 if none generated
     * @throws SQLException if execution fails
     */
    protected int executeInsertAndGetKey(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            setParameters(stmt, params);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    /**
     * Executes a batch of statements.
     *
     * @param sql the SQL statement template
     * @param batchParams list of parameter arrays for each batch item
     * @return array of affected row counts
     * @throws SQLException if execution fails
     */
    protected int[] executeBatch(String sql, List<Object[]> batchParams) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            try {
                for (Object[] params : batchParams) {
                    setParameters(stmt, params);
                    stmt.addBatch();
                }
                
                int[] results = stmt.executeBatch();
                conn.commit();
                return results;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Executes multiple statements in a transaction.
     *
     * @param operation the transactional operation to execute
     * @param <T> the return type
     * @return the result of the operation
     * @throws SQLException if execution fails
     */
    protected <T> T executeInTransaction(TransactionalOperation<T> operation) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            T result = operation.execute(conn);
            
            conn.commit();
            return result;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warn("Transaction rolled back due to: {}", e.getMessage());
                } catch (SQLException rollbackEx) {
                    logger.error("Rollback failed", rollbackEx);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    logger.error("Error closing connection", closeEx);
                }
            }
        }
    }

    /**
     * Executes a count query.
     *
     * @param sql the SQL count query
     * @param params the query parameters
     * @return the count result
     * @throws SQLException if execution fails
     */
    protected long executeCount(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setParameters(stmt, params);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }

    /**
     * Checks if a record exists.
     *
     * @param sql the SQL query (should return at least one row if exists)
     * @param params the query parameters
     * @return true if exists, false otherwise
     * @throws SQLException if execution fails
     */
    protected boolean exists(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setParameters(stmt, params);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Sets prepared statement parameters with proper type handling.
     *
     * @param stmt the prepared statement
     * @param params the parameters to set
     * @throws SQLException if parameter setting fails
     */
    protected void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            int paramIndex = i + 1;
            
            if (param == null) {
                stmt.setNull(paramIndex, java.sql.Types.NULL);
            } else if (param instanceof String s) {
                stmt.setString(paramIndex, s);
            } else if (param instanceof Integer n) {
                stmt.setInt(paramIndex, n);
            } else if (param instanceof Long l) {
                stmt.setLong(paramIndex, l);
            } else if (param instanceof Double d) {
                stmt.setDouble(paramIndex, d);
            } else if (param instanceof Boolean b) {
                stmt.setBoolean(paramIndex, b);
            } else if (param instanceof java.math.BigDecimal bd) {
                stmt.setBigDecimal(paramIndex, bd);
            } else if (param instanceof java.time.LocalDate ld) {
                stmt.setDate(paramIndex, java.sql.Date.valueOf(ld));
            } else if (param instanceof java.time.LocalTime lt) {
                stmt.setTime(paramIndex, java.sql.Time.valueOf(lt));
            } else if (param instanceof java.time.LocalDateTime ldt) {
                stmt.setTimestamp(paramIndex, java.sql.Timestamp.valueOf(ldt));
            } else if (param instanceof java.sql.Date d) {
                stmt.setDate(paramIndex, d);
            } else if (param instanceof java.sql.Time t) {
                stmt.setTime(paramIndex, t);
            } else if (param instanceof java.sql.Timestamp ts) {
                stmt.setTimestamp(paramIndex, ts);
            } else if (param instanceof Enum<?> e) {
                stmt.setString(paramIndex, e.name());
            } else {
                stmt.setObject(paramIndex, param);
            }
        }
    }

    /**
     * Safely closes a ResultSet.
     *
     * @param rs the ResultSet to close
     */
    protected void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.debug("Error closing ResultSet", e);
            }
        }
    }

    /**
     * Safely closes a PreparedStatement.
     *
     * @param stmt the statement to close
     */
    protected void closeQuietly(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.debug("Error closing PreparedStatement", e);
            }
        }
    }

    /**
     * Safely closes a Connection.
     *
     * @param conn the connection to close
     */
    protected void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.debug("Error closing Connection", e);
            }
        }
    }

    /**
     * Functional interface for mapping ResultSet rows to objects.
     *
     * @param <T> the type of object to map to
     */
    @FunctionalInterface
    public interface ResultSetMapper<T> {
        /**
         * Maps a ResultSet row to an object.
         *
         * @param rs the ResultSet positioned at the current row
         * @return the mapped object
         * @throws SQLException if mapping fails
         */
        T map(ResultSet rs) throws SQLException;
    }

    /**
     * Functional interface for transactional operations.
     *
     * @param <T> the return type of the operation
     */
    @FunctionalInterface
    public interface TransactionalOperation<T> {
        /**
         * Executes the transactional operation.
         *
         * @param conn the connection to use (already in transaction mode)
         * @return the result of the operation
         * @throws SQLException if operation fails
         */
        T execute(Connection conn) throws SQLException;
    }
}

package com.restaurant.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * HikariCP Connection Pool Configuration.
 * Provides high-performance database connection pooling.
 * 
 * This replaces direct DriverManager connections with pooled connections
 * for significantly better performance in multi-threaded environments.
 *
 * @author Restaurant Management System
 * @version 1.0
 * @since 2024-12-05
 */
public class HikariConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(HikariConnectionPool.class);

    // Database connection parameters
    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "restaurant_db");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "A9851040557@123a");

    // Pool configuration
    private static final int MAXIMUM_POOL_SIZE = 20;
    private static final int MINIMUM_IDLE = 5;
    private static final long CONNECTION_TIMEOUT_MS = 30000;      // 30 seconds
    private static final long IDLE_TIMEOUT_MS = 600000;           // 10 minutes
    private static final long MAX_LIFETIME_MS = 1800000;          // 30 minutes
    private static final long LEAK_DETECTION_THRESHOLD_MS = 60000; // 1 minute

    private static volatile HikariConnectionPool instance;
    private final HikariDataSource dataSource;

    /**
     * Private constructor - initializes HikariCP.
     */
    private HikariConnectionPool() {
        HikariConfig config = new HikariConfig();

        // JDBC URL
        String jdbcUrl = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true&cachePrepStmts=true&prepStmtCacheSize=250&prepStmtCacheSqlLimit=2048&useServerPrepStmts=true",
            DB_HOST, DB_PORT, DB_NAME
        );
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);

        // Pool sizing
        config.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        config.setMinimumIdle(MINIMUM_IDLE);

        // Timeouts
        config.setConnectionTimeout(CONNECTION_TIMEOUT_MS);
        config.setIdleTimeout(IDLE_TIMEOUT_MS);
        config.setMaxLifetime(MAX_LIFETIME_MS);
        config.setLeakDetectionThreshold(LEAK_DETECTION_THRESHOLD_MS);

        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);

        // Pool name for monitoring
        config.setPoolName("RestaurantHikariPool");

        // MySQL-specific optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // Driver class
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        this.dataSource = new HikariDataSource(config);
        logger.info("HikariCP connection pool initialized with max size: {}", MAXIMUM_POOL_SIZE);
    }

    /**
     * Gets the singleton instance (thread-safe double-checked locking).
     *
     * @return the HikariConnectionPool instance
     */
    public static HikariConnectionPool getInstance() {
        if (instance == null) {
            synchronized (HikariConnectionPool.class) {
                if (instance == null) {
                    instance = new HikariConnectionPool();
                }
            }
        }
        return instance;
    }

    /**
     * Gets a connection from the pool.
     *
     * @return a pooled database connection
     * @throws SQLException if unable to get a connection
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Gets pool statistics for monitoring.
     *
     * @return PoolStats containing current pool metrics
     */
    public PoolStats getPoolStats() {
        return new PoolStats(
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }

    /**
     * Checks if the pool is healthy.
     *
     * @return true if pool is running and has available connections
     */
    public boolean isHealthy() {
        try {
            return !dataSource.isClosed() && 
                   dataSource.getHikariPoolMXBean().getTotalConnections() > 0;
        } catch (Exception e) {
            logger.error("Error checking pool health", e);
            return false;
        }
    }

    /**
     * Gracefully shuts down the connection pool.
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Shutting down HikariCP connection pool...");
            dataSource.close();
            logger.info("HikariCP connection pool shutdown complete");
        }
    }

    /**
     * Gets the underlying HikariDataSource (for advanced usage).
     *
     * @return the HikariDataSource
     */
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Pool statistics record.
     */
    public record PoolStats(
        int activeConnections,
        int idleConnections,
        int totalConnections,
        int threadsAwaitingConnection
    ) {
        @Override
        public String toString() {
            return String.format(
                "PoolStats{active=%d, idle=%d, total=%d, waiting=%d}",
                activeConnections, idleConnections, totalConnections, threadsAwaitingConnection
            );
        }
    }
}

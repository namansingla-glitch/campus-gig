package com.gigmarket.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Connection pool shared by the whole application. */
public final class Database {

    private static final Logger LOG = Logger.getLogger(Database.class.getName());
    private static volatile HikariDataSource dataSource;

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return pool().getConnection();
    }

    private static HikariDataSource pool() {
        HikariDataSource ds = dataSource;
        if (ds == null) {
            synchronized (Database.class) {
                ds = dataSource;
                if (ds == null) {
                    try {
                        dataSource = ds = create();
                    } catch (RuntimeException e) {
                        throw new DataAccessException("Could not connect to the database. "
                                + "Check GIG_DB_URL / GIG_DB_USER / GIG_DB_PASSWORD.", e);
                    }
                }
            }
        }
        return ds;
    }

    private static HikariDataSource create() {
        HikariConfig cfg = new HikariConfig();
        cfg.setPoolName("campusgig-pool");
        cfg.setDriverClassName(AppConfig.dbDriver());
        cfg.setJdbcUrl(AppConfig.dbUrl());
        cfg.setUsername(AppConfig.dbUser());
        cfg.setPassword(AppConfig.dbPassword());
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setConnectionTimeout(10_000);
        return new HikariDataSource(cfg);
    }

    /** Called at startup so configuration problems show up in the log immediately. */
    public static void warmUp() {
        try (Connection ignored = getConnection()) {
            LOG.info("Database connection OK");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Database is not reachable yet - pages that need it will fail until it is. "
                    + "Make sure MySQL is running, schema.sql was loaded and the credentials are set.", e);
        }
    }

    public static void shutdown() {
        HikariDataSource ds = dataSource;
        if (ds != null) {
            ds.close();
            dataSource = null;
        }
    }
}

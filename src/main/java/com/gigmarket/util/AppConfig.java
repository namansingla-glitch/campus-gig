package com.gigmarket.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Central place for configuration. Values are looked up in this order:
 * environment variable, JVM system property, db.properties on the classpath, built-in default.
 * Nothing secret is hard-coded here.
 */
public final class AppConfig {

    private static final Properties FILE = new Properties();

    static {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                FILE.load(in);
            }
        } catch (IOException ignored) {
            // A missing or unreadable file simply means "use env vars / defaults".
        }
    }

    private AppConfig() {
    }

    private static String get(String envName, String propName, String fallback) {
        String value = System.getenv(envName);
        if (value == null || value.isEmpty()) {
            value = System.getProperty(propName);
        }
        if (value == null || value.isEmpty()) {
            value = FILE.getProperty(propName);
        }
        return (value == null || value.isEmpty()) ? fallback : value;
    }

    public static String dbUrl() {
        return get("GIG_DB_URL", "db.url",
                "jdbc:mysql://localhost:3306/gig_marketplace?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8");
    }

    public static String dbUser() {
        return get("GIG_DB_USER", "db.user", "root");
    }

    public static String dbPassword() {
        return get("GIG_DB_PASSWORD", "db.password", "");
    }

    public static String dbDriver() {
        return get("GIG_DB_DRIVER", "db.driver", "com.mysql.cj.jdbc.Driver");
    }

    /** Directory where uploaded portfolios are kept. Defaults to ~/campusgig-uploads. */
    public static Path uploadDir() {
        String configured = get("GIG_UPLOAD_DIR", "upload.dir", null);
        if (configured != null) {
            return Paths.get(configured).toAbsolutePath().normalize();
        }
        return Paths.get(System.getProperty("user.home"), "campusgig-uploads").toAbsolutePath().normalize();
    }
}

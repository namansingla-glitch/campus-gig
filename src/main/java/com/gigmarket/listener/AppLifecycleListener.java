package com.gigmarket.listener;

import com.gigmarket.util.AppConfig;
import com.gigmarket.util.Database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Prepares the upload folder and database pool at startup and cleans up on shutdown. */
public class AppLifecycleListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppLifecycleListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            Files.createDirectories(AppConfig.uploadDir());
            LOG.info("Uploads are stored in " + AppConfig.uploadDir());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Cannot create the upload folder " + AppConfig.uploadDir(), e);
        }
        Database.warmUp();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Database.shutdown();
        ClassLoader mine = getClass().getClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == mine) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Could not deregister JDBC driver " + driver, e);
                }
            }
        }
    }
}

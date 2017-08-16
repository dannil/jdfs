package com.github.dannil.jdfs;

import java.util.Collection;
import java.util.Objects;

import com.github.dannil.jdfs.accesser.LocalFileAccesser;
import com.github.dannil.jdfs.model.FileModel;
import com.github.dannil.jdfs.rest.RemoteDatabase;
import com.github.dannil.jdfs.rest.controller.FileController;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class App {

    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private String[] args;

    public App(String[] args) {
        this.args = args;
    }

    private String getArgument(String[] args, String argument) {
        // TODO Not actually tested yet, make sure if works in the future
        for (int i = 1; i < args.length - 1; i++) {
            if (Objects.equals(args[i], argument)) {
                return args[i + 1];
            }
        }
        return null;
    }

    public void startLocalServices() throws Exception {
        LOGGER.info("Starting local services...");

        // TODO Implement check for changed files while application was turned off and
        // update local database with these changes

        RemoteDatabase remoteDb = new RemoteDatabase();
        Collection<FileModel> remoteFiles = remoteDb.getFiles();
        System.out.println("REMOTE: " + remoteFiles);
        for (FileModel f : remoteFiles) {
            System.out.println("F1: " + f);
        }

        LocalFileAccesser localFileAccesser = LocalFileAccesser.getInstance();
        Collection<FileModel> localFiles = localFileAccesser.getModelFiles();

        for (FileModel f : localFiles) {
            System.out.println("F2: " + f);
        }

        localFileAccesser.indexLocalDatabase();

        // for (java.io.File f : localFileAccesser.getDeletedFiles()) {
        // // System.out.println(f);
        // }
        LOGGER.info("Local services started");
    }

    public void startExternalServices() throws Exception {
        Thread one = new Thread() {
            @Override
            public void run() {
                RemoteDatabase db = new RemoteDatabase();
                Collection<FileModel> files = db.getFiles();
                for (FileModel f : files) {
                    System.out.println(f);
                }
            }
        };

        LOGGER.info("Starting external services...");

        // Disable logging for Jetty
        // System.setProperty("org.eclipse.jetty.util.log.class",
        // "org.eclipse.jetty.util.log.StdErrLog");
        // System.setProperty("org.eclipse.jetty.LEVEL", "OFF");

        // Get the port to be used
        String portArg = getArgument(this.args, "-port");
        int defaultPort = 8080;
        int selectedPort = (portArg != null ? Integer.parseInt(portArg) : defaultPort);
        LOGGER.info("Using port " + selectedPort + " for HTTP interface");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server server = new Server(selectedPort);
        server.setHandler(context);

        ServletHolder servlet = context.addServlet(ServletContainer.class, "/*");
        servlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        servlet.setInitParameter("jersey.config.server.provider.classnames", FileController.class.getCanonicalName());

        try {
            server.start();
            one.start();
            LOGGER.info("Startup complete!");
            server.join();
        } finally {
            server.destroy();
        }
    }

    public static void main(String[] args) throws Exception {

        App app = new App(args);
        app.startLocalServices();
        app.startExternalServices();

    }

}

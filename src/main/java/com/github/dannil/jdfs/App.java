package com.github.dannil.jdfs;

import java.util.Objects;

import com.github.dannil.jdfs.accesser.FileAccesser;
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
        FileAccesser accesser = FileAccesser.getInstance();
        accesser.indexLocalDatabase();

        // TEST CODE
        // Random r = new Random();
        // File f = new File("path/to/file" + r.nextInt(1000), ZonedDateTime.now());

        // FileDBA dba = new FileDBA();
        // dba.save(f);

        // List<File> files = dba.getAll();
        // for (File f1 : files) {
        // System.out.println(f1);
        // }
        //
        // System.out.println();
        //
        // System.out.println(dba.getById(4));
        LOGGER.info("Local services started");
    }

    public void startExternalServices() throws Exception {
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
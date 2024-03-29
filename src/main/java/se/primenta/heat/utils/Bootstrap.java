package se.primenta.heat.utils;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class Bootstrap {

	private static final int DEFAULT_PORT = 8080;
	private static final String CONTEXT_PATH = "/";
	private static final String CONFIG_LOCATION = "se.primenta.heat.config";
	private static final String MAPPING_URL = "/*";
	private static final String DEFAULT_PROFILE = "dev";

	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	public static void main(String[] args) throws Exception {
		new Bootstrap().startJetty(getPortFromArgs(args));
	}

	private static int getPortFromArgs(String[] args) {
		if (args.length > 0) {
			try {
				return Integer.valueOf(args[0]);
			} catch (NumberFormatException ignore) {
			}
		}
		return DEFAULT_PORT;
	}

	private void startJetty(int port) throws Exception {
		Server server = new Server(port);
		server.setHandler(getServletContextHandler(getContext()));
		logger.info("Starting the embedded server");
		server.start();
		server.join();
	}

	private static ServletContextHandler getServletContextHandler(
			WebApplicationContext context) throws IOException {
		ServletContextHandler contextHandler = new ServletContextHandler();
		contextHandler.setErrorHandler(null);
		contextHandler.setContextPath(CONTEXT_PATH);
		contextHandler.addServlet(new ServletHolder(new DispatcherServlet(
				context)), MAPPING_URL);
		contextHandler.addEventListener(new ContextLoaderListener(context));
		contextHandler.setResourceBase(new ClassPathResource("webapp").getURI()
				.toString());
		return contextHandler;
	}

	private static WebApplicationContext getContext() {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setConfigLocation(CONFIG_LOCATION);
		context.getEnvironment().setDefaultProfiles(DEFAULT_PROFILE);
		return context;
	}

}

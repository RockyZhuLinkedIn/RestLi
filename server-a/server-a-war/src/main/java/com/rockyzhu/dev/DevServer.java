package com.rockyzhu.dev;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * The DevServer class contains the main() method to enable running the servlet in debug mode.
 *
 * @author Rocky Zhu
 */
public class DevServer {

  private static final String WEB_APP_PATH = "file:/Users/hozhu/work/components/RestLi/server-a/server-a-war/src/main/webapp";
  private static final String WEB_XML_PATH = WEB_APP_PATH + "/WEB-INF/web.xml";
  private static final String CONTEXT_PATH = "/server-a";

  public static void main(String[] args) throws Exception {
    Server server = new Server();

    WebAppContext webApp = new WebAppContext(WEB_APP_PATH, CONTEXT_PATH);
    webApp.setDescriptor(WEB_XML_PATH);
    webApp.setParentLoaderPriority(true);

    HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(new HttpConfiguration());
    ServerConnector connector = new ServerConnector(server, httpConnectionFactory);
    connector.setHost("0.0.0.0");
    connector.setPort(7071);

    server.setHandler(webApp);
    server.addConnector(connector);

    server.start();
    server.join();
  }
}

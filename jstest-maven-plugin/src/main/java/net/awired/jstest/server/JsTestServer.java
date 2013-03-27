/**
 * JsTest Maven Plugin
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.awired.jstest.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class JsTestServer {

    private final Log log;

    private Server server = new Server();
    private int port;

    public JsTestServer(Log log, int wantedPort, boolean findFreePort) {
        this.log = log;
        this.port = wantedPort;
        if (findFreePort) {
            while (!available(this.port)) {
                this.port++;
            }
        }
    }

    public void startServer(Handler handler) throws Exception {
        SelectChannelConnector connector = new SelectChannelConnector();
        server.addConnector(connector);
        connector.setPort(port);
        server.setHandler(handler);
        log.info("Starting JsTest server on port " + port);
        server.start();
//        server.join();
    }

    public void join() {
        log.debug("joining JsTest server");
        try {
            server.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void close() {
        if (server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                // nothing to do
            }
            server.destroy();
        }
    }

    public static boolean available(int port) {
        if (port < 0 || port > 0xFFFF) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

}

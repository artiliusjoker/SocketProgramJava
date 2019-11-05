package com.project.server;

import java.io.IOException;

interface Server {
    public void startServer(int initPort);
    public void stopServer() throws IOException;
}

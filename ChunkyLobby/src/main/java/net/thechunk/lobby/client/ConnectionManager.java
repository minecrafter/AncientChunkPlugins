package net.thechunk.lobby.client;

import com.google.common.io.ByteStreams;
import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionManager {
    private InetSocketAddress isa;

    protected ConnectionManager(InetSocketAddress isa) throws IOException {
        this.isa = isa;
    }

    public String getResponse(String request) throws IOException {
        try (Socket socket = new Socket(isa.getAddress(), isa.getPort());
             InputStream is = socket.getInputStream();
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            out.println(request);
            ByteStreams.copy(is, baos);
            return new String(baos.toByteArray());
        }
    }

    public void send(String request) throws IOException {
        try (Socket socket = new Socket(isa.getAddress(), isa.getPort());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(request);
        }
    }
}

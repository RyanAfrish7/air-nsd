package aryanware.air;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ryanafrish7 on 5/10/16.
 */

public class AirHostInterface {

    ServerSocket serverSocket;
    RequestHandler defaultHandler;
    Map<String, RequestHandler> requestHandlerMap = new HashMap<>();

    AirHostInterface(RequestHandler reqHandler) throws IOException {
        serverSocket = new ServerSocket(AirProtocol.HOST_PORT);
        defaultHandler = reqHandler;
    }

    void listen() throws IOException {
        System.out.println("Listening on port " + AirProtocol.HOST_PORT);
        while (true) {
            new AirConnection(serverSocket.accept());
        }
    }

    void setRequestHandler(String requestIntent, RequestHandler reqHandler) {
        requestHandlerMap.put(requestIntent, reqHandler);
    }

    interface RequestHandler {
        void handle(AirPacket req, AirPacket res, AirConnection conn);
    }

    class AirConnection implements Runnable {

        boolean waitForNextRequest = true;

        InputStream rawIn;
        OutputStream rawOut;

        Socket socket;
        Thread thread;

        AirConnection(Socket socket) throws IOException {
            this.socket = socket;

            rawIn = socket.getInputStream();
            rawOut = socket.getOutputStream();

            thread = new Thread(this);
            thread.start();
        }

        public void close() {
            try {
                waitForNextRequest = false;
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (waitForNextRequest) {
                    AirPacket request = AirPacket.readFromStream(rawIn);
                    AirPacket response = new AirPacket(AirPacket.Type.RESPONSE);
                    RequestHandler handler = requestHandlerMap.get(request.getIntent());
                    if (handler != null)
                        handler.handle(request, response, this);
                    else
                        defaultHandler.handle(request, response, this);
                    response.send(rawOut);
                }
            } catch (ProtocolViolationException e) {
                e.printStackTrace();
            }
        }
    }

}
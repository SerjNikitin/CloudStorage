package service.impl;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import lombok.Getter;
import model.AbstractCommand;
import service.NetWorkService;

import java.io.IOException;
import java.net.Socket;

@Getter
public class NetworkServiceImpl implements NetWorkService {
    private static NetworkServiceImpl instance;
    private static final int SERVER_PORT = 8080;
    private static final String HOST = "localhost";

    private static ObjectEncoderOutputStream os;
    private static ObjectDecoderInputStream is;
    private static Socket socket;

    private NetworkServiceImpl() {
    }

    public static NetworkServiceImpl getInstance() {
        if (instance == null) {
            instance = new NetworkServiceImpl();
            initializeSocket();
            initializeIOStreams();
        }
        return instance;
    }

    private static void initializeIOStreams() {
        try {
            is = new ObjectDecoderInputStream(socket.getInputStream());
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeSocket() {
        try {
            socket = new Socket(HOST, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void sendCommand(AbstractCommand command) {
        try {
            os.writeObject(command);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractCommand readCommandResult() {
        try {
            return (AbstractCommand) is.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeConnection() {
        try {
            os.close();
            is.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




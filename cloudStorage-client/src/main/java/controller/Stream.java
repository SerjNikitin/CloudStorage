package controller;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import lombok.Getter;

import java.net.Socket;

@Getter
public class Stream {

    private  ObjectEncoderOutputStream os;
    private  ObjectDecoderInputStream is;
    private  Socket socket;


    public Stream() {
        try {
            socket = new Socket("localhost", 8080);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public static Stream getInstance() {
//        try {
//            socket = new Socket("localhost", 8080);
//            os = new ObjectEncoderOutputStream(socket.getOutputStream());
//            is = new ObjectDecoderInputStream(socket.getInputStream());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

//    private static NetworkSettings INSTANCE;
//
//    public static NetworkSettings getINSTANCE() {
//        if (INSTANCE == null) {
//            INSTANCE = new NetworkSettings();
//            try {
//                socket = new Socket("localhost", 8080);
//                os = new ObjectEncoderOutputStream(socket.getOutputStream());
//                is = new ObjectDecoderInputStream(socket.getInputStream());
//                user = new User();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return INSTANCE;
//    }


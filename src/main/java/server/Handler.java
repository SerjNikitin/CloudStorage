package server;

import java.io.*;
import java.net.Socket;

public class Handler implements Runnable {
    private final String serverDir = "D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\server\\saveFiles";
    private final byte[] buffer;
    private final DataOutputStream out;
    private final DataInputStream in;

    public Handler(Socket accept) throws IOException {
        buffer = new byte[8 * 1024];
        in = new DataInputStream(accept.getInputStream());
        out = new DataOutputStream(accept.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                String name = in.readUTF();
                long size = in.readLong();
                System.out.println("File: " + name + ", size: " + size);
                try (FileOutputStream fos = new FileOutputStream(serverDir + "\\" + name)) {
                    int readCheck = 0;

                    while ((readCheck = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, readCheck);
                    }

                }
                out.writeUTF("Файл: " + name + " полностью передался");
                out.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
//
//    public void outputFile() {
//        File file = new File("D:\\учеба\\JAVA\\client.CloudStorage\\client.CloudStorage\\src\\main\\java\\server\\saveFiles\\photo.jpg");
//        try {
//            FileInputStream fis = new FileInputStream(file);
//
//            int read = 0;
//            try {
//                while ((read = fis.read(buffer)) != -1) {
//                    out.write(buffer, 0, read);
//                    out.flush();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void inputFile() {
//        try {
//            FileOutputStream fos = new FileOutputStream(
//                    "D:\\учеба\\JAVA\\client.CloudStorage\\client.CloudStorage\\src\\main\\java\\server\\saveFiles\\photo.jpg");
//            int readCheck = 0;
//            try {
//                while ((readCheck = in.read(buffer)) != -1) {
//                    fos.write(buffer, 0, readCheck);
//                    fos.flush();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
}
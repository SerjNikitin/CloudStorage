package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Server {
    public Server() {
        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new MassageHandler()
                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind(8080).sync();
            log.debug("Server started...");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
//    private final String serverDir = "D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\server\\saveFiles";
//    private final byte[] buffer;
//    private final DataOutputStream out;
//    private final DataInputStream in;
//
//    public Handler(Socket accept) throws IOException {
//        buffer = new byte[8 * 1024];
//        in = new DataInputStream(accept.getInputStream());
//        out = new DataOutputStream(accept.getOutputStream());
//    }
//
//    @Override
//    public void run() {
//        try {
//            while (true) {
//                String name = in.readUTF();
//                long size = in.readLong();
//                System.out.println("File: " + name + ", size: " + size);
//                try (FileOutputStream fos = new FileOutputStream(serverDir + "\\" + name)) {
//                    int readCheck = 0;
//                    while ((readCheck = in.read(buffer)) != -1) {
//                        fos.write(buffer, 0, readCheck);
//                    }
//                }
//                out.writeUTF("Файл: " + name + " отправлен");
//                out.flush();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

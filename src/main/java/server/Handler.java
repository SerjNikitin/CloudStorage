//package server;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.channel.ChannelPipeline;
//import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.FileOutputStream;
//import java.io.InputStream;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Slf4j
//public class Handler extends ChannelInboundHandlerAdapter {
//
//    private ObjectDecoderInputStream in;
//    private Path path = Paths.get("saveFiles").toAbsolutePath();
//
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        log.debug("Client is connected");
//
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        log.debug("Client is disconnected");
//        ctx.flush();
//        ctx.close();
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//
//        ChannelPipeline pipeline = ctx.pipeline();
//        Channel channel = pipeline.channel();
//        in=new ObjectDecoderInputStream((InputStream) channel);
//        String name = in.readUTF();
//        long size = in.readLong();
//        Object object = in.readObject();
//        FileOutputStream fos=new FileOutputStream("D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\server\\saveFiles" +"\\"+name);
//
//        ByteBuf buf= (ByteBuf) msg;
//
//        byte[] array = ((ByteBuf) msg).array();
//
//        while (buf.isReadable()){
//            fos.write(array,0, (int) size);
//            buf.readByte();
//
////            ByteBuf byteBuf = buf.readBytes(buffer);
//
////            fos.write(buf.array(),0, buf.g);
////            byte b = buf.readByte();
////            buffer.put(b);
//        }
////        log.debug("received: {}", buffer);
//
////        для отправки
////        ByteBuf response = ctx.alloc().buffer();
////        response.writeByte()
//
//    }
//}

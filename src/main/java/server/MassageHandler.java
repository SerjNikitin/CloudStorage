package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class MassageHandler extends SimpleChannelInboundHandler<AbstractCommand> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand command) {

        log.debug("massage: {}", command);
        ctx.writeAndFlush(command);
        switch (command.getType()) {
            case FILE_MESSAGE:
                FileMassage massage = (FileMassage) command;
                try (FileOutputStream fos = new FileOutputStream(
                        "D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\server\\saveFiles" + "\\"
                                + massage.getName())) {
                    fos.write(massage.getArr());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case FILE_REQUEST:
                FileRequest request = (FileRequest)command;
//                FileMassage massage1 = (FileMassage) command;
                try (FileOutputStream fos = new FileOutputStream(
                        "D:\\учеба\\JAVA\\CloudStorage\\lesson1\\src\\main\\java\\client\\saveFiles" + "\\"
                                + request.getName())) {
                    fos.write(request.getArr());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}

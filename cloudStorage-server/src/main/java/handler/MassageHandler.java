package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class MassageHandler extends SimpleChannelInboundHandler<AbstractCommand> {

    private Path currentPath;

    public MassageHandler() throws IOException {
        currentPath = Paths.get("serverDir");
        if (!Files.exists(currentPath)) {
            Files.createDirectory(currentPath);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ListResponse(currentPath));
        ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand command) throws IOException {

        log.debug("massage: {}", command);
        switch (command.getType()) {

            case FILE_MESSAGE:
                FileMassage message = (FileMassage) command;
                Files.write(currentPath.resolve(message.getName()), message.getArr());
                ctx.writeAndFlush(new ListResponse(currentPath));
                break;

            case PATH_UP:
                if (currentPath.getParent() != null) {
                    currentPath = currentPath.getParent();
                }
                ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
                ctx.writeAndFlush(new ListResponse(currentPath));
                break;

            case LIST_REQUEST:
                ctx.writeAndFlush(new ListResponse(currentPath));
                break;

            case PATH_IN_REQUEST:
                PathInRequest request = (PathInRequest) command;
                Path resolve = currentPath.resolve(request.getPath());
                if (Files.isDirectory(resolve)) {
                    currentPath = resolve;
                    ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
                    ctx.writeAndFlush(new ListResponse(currentPath));
                    break;
                }

            case FILE_REQUEST:
                FileRequest fileRequest = (FileRequest) command;
                FileMassage msg = new FileMassage(currentPath.resolve(fileRequest.getName()));
                ctx.writeAndFlush(msg);
                break;

            case DELETE_REQUEST:
                DeleteRequest deleteRequest = (DeleteRequest) command;
                Path pathInDel = currentPath.resolve(deleteRequest.getName());
                boolean isDelete = pathInDel.toFile().delete();
                if (isDelete) {
                    ctx.writeAndFlush(new ListResponse(currentPath));
                }
                break;
        }
    }
}

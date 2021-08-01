package handler;

import dataBase.RequestDB;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
public class MassageHandler extends SimpleChannelInboundHandler<AbstractCommand> {

    private Path currentPath;

//    public MassageHandler() throws IOException {
//        currentPath = Paths.get("serverDir");
//        if (!Files.exists(currentPath)) {
//            Files.createDirectory(currentPath);
//        }
//    }

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

            case AUTHORIZATION:
                Authorization authRequest = (Authorization) command;
                User user = new User();
                user.setLogin(authRequest.getLogin());
                user.setPassword(authRequest.getPassword());
                Optional<User> resSet = new RequestDB().findUser(user.getLogin(), user.getPassword());
                try {
                    currentPath = Paths.get(user.getLogin());
                    if (!Files.exists(currentPath)) {
                        Files.createDirectory(currentPath);
                    }
                    ctx.writeAndFlush(new AuthenticationResponse(user));
                } catch (IOException e) {
                    log.error("Error: {}", e.getClass());
                }
                break;
            case REGISTRATION:
                Registration regMSG = (Registration) command;
                User newUser = new User(regMSG.getName(), regMSG.getLogin(), regMSG.getPassword());
                new RequestDB().createUser(regMSG.getName(), regMSG.getLogin(), regMSG.getPassword());
                ctx.writeAndFlush(new Message("Registration successful"));
                if (!Files.exists(currentPath)) {
                    Files.createDirectory(currentPath);
                }
                break;
        }
    }
}
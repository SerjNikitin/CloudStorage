package handler;

import dataBase.RequestDB;
import domaine.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import domaine.*;
import domaine.abstarctCommandImpl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
public class MassageHandler extends SimpleChannelInboundHandler<AbstractCommand> {

    private Path currentPath;

//    public MassageHandler() throws IOException {
//        if (!Files.exists(currentPath)) {
//            currentPath = Paths.get("./");
//            Files.createDirectory(currentPath);
//        }
//    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ListResponse(currentPath));
        ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand command) {

        log.debug("massage: {}", command);
        switch (command.getType()) {

            case LIST_REQUEST:
                listRequest(ctx);
                break;

            case FILE_REQUEST:
                fileRequest(ctx, command);
                break;

            case FILE_MESSAGE:
                fileMassage(ctx, command);
                break;

            case PATH_UP_REQUEST:
                pathUpRequest(ctx);
                break;

            case PATH_IN_REQUEST:
                pathInRequest(ctx, command);
                break;

            case DELETE_REQUEST:
                deleteRequest(ctx, command);
                break;

            case AUTH_REQUEST:
                authRequest(ctx, command);
                break;

            case REGISTRATION_REQUEST:
                registrationRequest(ctx, command);
                break;
        }
    }

    private void registrationRequest(ChannelHandlerContext ctx, AbstractCommand command) {
        RegistrationRequest regMassage = (RegistrationRequest) command;
        User newUser = new User(regMassage.getName(), regMassage.getLogin(), regMassage.getPassword());
        new RequestDB().createUser(newUser.getName(), newUser.getLogin(), newUser.getPassword());
        ctx.writeAndFlush(new SimpleMessage("Registration successful"));
    }

    private void authRequest(ChannelHandlerContext ctx, AbstractCommand command) {
        AuthorizationRequest authRequest = (AuthorizationRequest) command;
        User user = new User();
        user.setLogin(authRequest.getLogin());
        user.setPassword(authRequest.getPassword());

        Optional<User> resSet = new RequestDB().findUser(user.getLogin(), user.getPassword());
        try {
            user.setName(resSet.get().getName());
            currentPath = Paths.get(user.getLogin());
            if (!Files.exists(currentPath)) {
                Files.createDirectory(currentPath);
            }
            ctx.writeAndFlush(new AuthenticationResponse(user));
        } catch (IOException e) {
            log.error("Error: {}", e.getClass());
        }
    }

    private void deleteRequest(ChannelHandlerContext ctx, AbstractCommand command) {
        DeleteRequest request = (DeleteRequest) command;
        Path delPath = currentPath.resolve(request.getName());
        boolean isDeleted = delPath.toFile().delete();
        try {
            ctx.writeAndFlush(new ListResponse(currentPath));
        } catch (IOException e) {
            ctx.writeAndFlush(new SimpleMessage("Sending error in block: DELETE_REQUEST"));
        }
        if (isDeleted) {
            ctx.writeAndFlush(new SimpleMessage("File " + request.getName() + " deleted successful"));
        } else {
            ctx.writeAndFlush(new SimpleMessage("File " + request.getName() + " deleting error"));
        }
    }

    private void pathInRequest(ChannelHandlerContext ctx, AbstractCommand command) {
        try {
            PathInRequest request = (PathInRequest) command;
            Path newPath = currentPath.resolve(request.getPath());
            if (Files.isDirectory(newPath)) {
                currentPath = newPath;
                ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
                ctx.writeAndFlush(new ListResponse(currentPath));
            }
        } catch (Exception e) {
            ctx.writeAndFlush(new SimpleMessage("Sending error in block: PATH_IN_REQUEST"));
        }
    }

    private void pathUpRequest(ChannelHandlerContext ctx) {
        try {
            if (currentPath.getParent() != null) {
                currentPath = currentPath.getParent();
                ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
                ctx.writeAndFlush(new ListResponse(currentPath));
            }
        } catch (Exception e) {
            ctx.writeAndFlush(new SimpleMessage("Sending error in block: PATCH_UP"));
        }
    }

    private void fileMassage(ChannelHandlerContext ctx, AbstractCommand command) {
        FileMessage message = (FileMessage) command;
        try {
            Files.write(currentPath.resolve(message.getName()), message.getArr());
            ctx.writeAndFlush(new ListResponse(currentPath));
            ctx.writeAndFlush(new SimpleMessage("File sending successful"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fileRequest(ChannelHandlerContext ctx, AbstractCommand command) {
        FileRequest fileRequest = (FileRequest) command;
        try {
            FileMessage msg = new FileMessage(currentPath.resolve(fileRequest.getName()));
            ctx.writeAndFlush(msg);
        } catch (Exception e) {
            ctx.writeAndFlush(new SimpleMessage("Sending error in block: FILE_REQUEST"));
        }
    }

    private void listRequest(ChannelHandlerContext ctx) {
        try {
            ctx.writeAndFlush(new SimpleMessage("Server file list refreshing"));
            ctx.writeAndFlush(new ListResponse(currentPath));
            ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
        } catch (IOException e) {
            ctx.writeAndFlush(new SimpleMessage("Sending error in block: LIST_MESSAGE"));
        }
        ctx.writeAndFlush(new SimpleMessage("Server file list refreshed"));
    }
}
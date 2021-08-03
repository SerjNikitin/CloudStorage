package handler;

import dataBase.RequestDB;
import model.User;
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

            case LIST_REQUEST:
                try {
                    ctx.writeAndFlush(new SimpleMessage("Server file list refreshing"));
                    ctx.writeAndFlush(new ListResponse(currentPath));
                    ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
                } catch (IOException e) {
                    ctx.writeAndFlush(new SimpleMessage("Sending error in block: LIST_MESSAGE"));
                }
                ctx.writeAndFlush(new SimpleMessage("Server file list refreshed"));
                break;

            case FILE_REQUEST:
                FileRequest fileRequest = (FileRequest) command;
                try {
                    FileMessage msg = new FileMessage(currentPath.resolve(fileRequest.getName()));
                    ctx.writeAndFlush(msg);
                }catch (Exception e){
                    ctx.writeAndFlush(new SimpleMessage("Sending error in block: FILE_REQUEST"));
                }
                break;

            case FILE_MESSAGE:
                FileMessage message = (FileMessage) command;
                Files.write(currentPath.resolve(message.getName()), message.getArr());
                ctx.writeAndFlush(new ListResponse(currentPath));
                ctx.writeAndFlush(new SimpleMessage("File sending successful"));
                break;

            case PATH_UP_REQUEST:
                try {
                    if (currentPath.getParent() != null) {
                        currentPath = currentPath.getParent();
                        ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
                        ctx.writeAndFlush(new ListResponse(currentPath));
                    }
                } catch (Exception e) {
                    ctx.writeAndFlush(new SimpleMessage("Sending error in block: PATCH_UP"));
                }
                break;

            case PATH_IN_REQUEST:
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
                break;

            case DELETE_REQUEST:
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
                break;

            case AUTH_REQUEST:
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
                break;

            case REGISTRATION_REQUEST:
                RegistrationRequest regMassage = (RegistrationRequest) command;
                User newUser = new User(regMassage.getName(), regMassage.getLogin(), regMassage.getPassword());
                new RequestDB().createUser(newUser.getName(), newUser.getLogin(), newUser.getPassword());
                ctx.writeAndFlush(new SimpleMessage("Registration successful"));
                break;
        }
    }
}
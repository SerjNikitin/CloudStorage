package handler;

import dataBase.RequestDB;
import model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<AbstractAuth> {
    private RequestDB requestDB;
    private Path currentPath;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractAuth command) throws Exception {
        log.debug("massage: {}", command);
        switch (command.getTape()) {

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
                new RequestDB().createUser(newUser.getName(), newUser.getLogin(), newUser.getPassword());
                ctx.writeAndFlush(new Message("Registration successful"));
                break;
        }
    }
}

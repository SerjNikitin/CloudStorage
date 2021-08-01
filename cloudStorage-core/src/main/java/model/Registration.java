package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Registration extends AbstractCommand {
    private String name;
    private String login;
    private String password;

    @Override
    public CommandType getType() {
        return CommandType.REGISTRATION;
    }
}

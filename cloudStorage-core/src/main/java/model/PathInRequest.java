package model;

import lombok.Getter;

@Getter
public class PathInRequest extends AbstractCommand {
    private final String path;

    public PathInRequest(String path) {
        this.path = path;
    }

    @Override
    public CommandType getType() {
        return CommandType.PATH_IN_REQUEST;
    }
}

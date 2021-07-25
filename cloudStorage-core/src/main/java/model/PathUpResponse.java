package model;

import lombok.Getter;

@Getter
public class PathUpResponse extends AbstractCommand{

    private final String path;

    public PathUpResponse(String path) {
        this.path = path;
    }

    @Override
    public CommandType getType() {
        return CommandType.PATH_RESPONSE;
    }
}

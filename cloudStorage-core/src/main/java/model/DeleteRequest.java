package model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeleteRequest extends AbstractCommand{
    private String name;

    @Override
    public CommandType getType() {
        return CommandType.DELETE_REQUEST;
    }
}

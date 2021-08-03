package model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimpleMessage extends AbstractCommand {
    private final String content;

    public String toString() {
        return this.content;
    }
    public CommandType getType() {
        return CommandType.SIMPLE_MESSAGE;
    }
}
package domaine.abstarctCommandImpl;

import lombok.AllArgsConstructor;
import domaine.AbstractCommand;

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
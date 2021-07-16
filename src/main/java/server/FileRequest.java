package server;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class FileRequest extends AbstractCommand {

    private final String name;
    private final Long size;
    private final byte[] arr;

    public FileRequest(Path path) throws IOException {
        name = path.getFileName().toString();
        size = Files.size(path);
        arr = Files.readAllBytes(path);
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_REQUEST;
    }
}

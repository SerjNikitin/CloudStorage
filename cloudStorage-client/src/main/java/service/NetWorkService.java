package service;

import model.AbstractCommand;

public interface NetWorkService {

    void sendCommand(AbstractCommand abstractCommand);

    AbstractCommand readCommandResult();

    void closeConnection();
}

package model;

import java.io.Serializable;

public abstract class AbstractAuth implements Serializable {
    public abstract AuthType getTape();
}

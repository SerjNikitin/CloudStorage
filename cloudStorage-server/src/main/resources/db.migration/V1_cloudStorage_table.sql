CREATE TABLE `cloud_storage`.`users`
(
    `name`     VARCHAR(45) NOT NULL,
    `login`    VARCHAR(45) NOT NULL,
    `password` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`password`),
    UNIQUE INDEX `login_UNIQUE` (`login` ASC) VISIBLE
);

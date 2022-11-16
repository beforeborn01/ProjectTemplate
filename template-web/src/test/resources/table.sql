CREATE TABLE `user`
(
    `id`          VARCHAR(64)  NOT NULL,
    `name`        VARCHAR(45)  NOT NULL,
    `age`         VARCHAR(45)  NOT NULL,
    `creator`     VARCHAR(45)  NOT NULL,
    `updator`     VARCHAR(45)  NOT NULL,
    `create_time` TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time` TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`id`)
);

INSERT INTO `user` (`id`, `name`, `age`, `creator`, `updator`, `create_time`, `update_time`) VALUES ('1', 'zhangsan', '30', 'zhangsan', 'zhangsan', '2022-11-16 17:46:39.123', '2022-11-16 17:46:39.123');
INSERT INTO `user` (`id`, `name`, `age`, `creator`, `updator`, `create_time`, `update_time`) VALUES ('2', 'lisi', '40', 'lisi', 'lisi', '2022-11-16 17:46:39.123', '2022-11-16 17:46:39.123');
INSERT INTO `user` (`id`, `name`, `age`, `creator`, `updator`, `create_time`, `update_time`) VALUES ('3', 'zhangfei', '50', 'zhangfei', 'zhangfei', '2022-11-16 17:46:39.123', '2022-11-16 17:46:39.123');


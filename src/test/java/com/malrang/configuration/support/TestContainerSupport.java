package com.malrang.configuration.support;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

public abstract class TestContainerSupport {

    private static final String MYSQL_IMAGE = "mysql:8.0";

    private static final JdbcDatabaseContainer MYSQL;

    static {
        MYSQL = new MySQLContainer<>(MYSQL_IMAGE)
                .withDatabaseName("test_db")  // 데이터베이스 이름 설정
                .withUsername("root")          // 사용자 이름 설정
                .withPassword("test");         // 비밀번호 설정
        MYSQL.start();
    }
}

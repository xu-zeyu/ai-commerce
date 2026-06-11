package com.jinHan.shop.admin;

import io.github.cdimascio.dotenv.Dotenv;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * jinHan-shop 管理后台模块启动类
 */
@SpringBootApplication(scanBasePackages = {"com.jinHan.shop", "com.aicommerce"})
@MapperScan({
        "com.jinHan.shop",
        "com.aicommerce.auth"
})
public class AdminApplication {
    public static void main(String[] args) {
        // 本地开发时读取 .env，容器环境下缺失文件也允许启动。
        Dotenv dotenv = Dotenv.configure()
                .directory("jinHan-shop/jinHan-shop-admin-web")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        dotenv.entries().forEach(entry -> {
            if (System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
        SpringApplication.run(AdminApplication.class, args);
    }
}

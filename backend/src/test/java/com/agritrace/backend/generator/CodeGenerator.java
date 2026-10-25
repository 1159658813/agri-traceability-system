package com.agritrace.backend.generator;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Path;
import java.util.Collections;

/**
 * 本地一次性生成数据库基础类。生成结果先放 target/mp-generated，检查后再按需复制。
 * 运行配置需要 MP_GEN_DB_PASSWORD 环境变量，严禁写死密码后提交到 GitHub。
 */
public class CodeGenerator {

    private static final String[] TABLES = {
            "sys_user", "planting_base", "product_category", "agri_product",
            "trace_batch", "farm_record", "harvest_record", "quality_inspection",
            "circulation_record", "trace_attachment", "origin_profile", "batch_audit",
            "completeness_rule", "completeness_result"
    };

    public static void main(String[] args) {
        String password = System.getenv("MP_GEN_DB_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("请在 IDEA Run Configuration 中设置 MP_GEN_DB_PASSWORD");
        }

        String user = System.getenv().getOrDefault("MP_GEN_DB_USER", "root");
        String url = System.getenv().getOrDefault("MP_GEN_DB_URL",
                "jdbc:mysql://127.0.0.1:3306/agri_traceability?serverTimezone=Asia/Shanghai");

        // 在 IDEA 中将 Working directory 指向 backend，确保输出路径可预测。
        Path output = Path.of(System.getProperty("user.dir"), "target", "mp-generated");
        Path javaDir = output.resolve(Path.of("src", "main", "java"));
        Path xmlDir = output.resolve(Path.of("src", "main", "resources", "mapper"));

        FastAutoGenerator.create(url, user, password)
                .globalConfig(builder -> builder
                        .author("agri-traceability")
                        .disableOpenDir()
                        .outputDir(javaDir.toString()))
                .packageConfig(builder -> builder
                        .parent("com.agritrace.backend")
                        .entity("entity")
                        .mapper("dao")
                        .service("service")
                        .serviceImpl("service.impl")
                        .controller("controller")
                        .xml("mapper")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, xmlDir.toString())))
                .strategyConfig(builder -> builder
                        .addInclude(TABLES)
                        .entityBuilder()
                        .idType(IdType.AUTO)
                        .enableTableFieldAnnotation()
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        .mapperBuilder()
                        .enableMapperAnnotation()
                        .controllerBuilder()
                        .enableRestStyle())
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

        System.out.println("生成结束，请先检查：" + output.toAbsolutePath());
        System.out.println("不要整体覆盖已有的 PlantingBase / Controller / Service。");
    }
}

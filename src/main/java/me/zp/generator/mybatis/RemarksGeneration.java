package me.zp.generator.mybatis;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 描述：
 * 2021/5/27 9:13.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
public class RemarksGeneration {

    private final Map<String, Map<String, String>> remarks;
    private final Map<String, String> tableNames;

    public RemarksGeneration(Map<String, String> tableNames, Map<String, Map<String, String>> remarks) {
        this.remarks = remarks;
        this.tableNames = tableNames;
    }

    public void remark(String entityPath, Set<Class<?>> aClass) throws IOException {
        for (Class<?> aCla : aClass) {
            Map<String, String> chl = new HashMap<>(16);
            remark(entityPath, aCla, chl);
            remarks.put(aCla.getSimpleName(), chl);
            tableNames.put(aCla.getSimpleName(), tabName(entityPath, aCla));
        }
    }

    private void remark(String entityPath, Class<?> aCla, Map<String, String> chl) throws IOException {
        String targetPath = entityPath + aCla.getSimpleName() + ".java";
        File file = new File(targetPath);
        if (!file.exists()) {
            return;
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            List<String> stringList = IOUtils.readLines(inputStream, "utf-8");
            boolean flag = false;
            boolean flag1 = false;
            String remark = "";
            for (String string : stringList) {
                if (flag && (string.contains("private") || string.contains("protected"))) {
                    String substring = string.substring(string.lastIndexOf(" "), string.length() - 1);
                    chl.put(substring.trim(), remark.replace("*", "").replace(" ", ""));
                    remark = null;
                }
                if (flag && flag1) {
                    remark = string;
                    flag1 = false;
                    continue;
                }
                if (flag && string.contains("/**")) {
                    flag1 = true;
                    continue;
                }
                if (string.contains("public class") || string.contains("public abstract class")) {
                    flag = true;
                    continue;
                }
            }
            Class<?> superclass = aCla.getSuperclass();
            remark(entityPath, superclass, chl);
        }
    }

    private String tabName(String entityPath, Class<?> aCla) throws IOException {
        String targetPath = entityPath + aCla.getSimpleName() + ".java";
        File file = new File(targetPath);
        if (!file.exists()) {
            return "";
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            List<String> stringList = IOUtils.readLines(inputStream, "utf-8");
            boolean flag = false;
            boolean flag1 = false;
            String tableName = "";
            for (String string : stringList) {
                string = string.trim();
                if (flag && string.contains("*/")) {
                    break;
                }
                if (flag && string.contains("@")) {
                    continue;
                }
                if (flag) {
                    String replace = string.replace(" ", "").replace("*", "");
                    if (replace.length() > 2) {
                        tableName = replace;
                    }
                    break;
                }
                if (string.contains("/**")) {
                    flag = true;
                }
            }
            return tableName.replace("实体类", "表").replace("类", "表");
        }
    }

}

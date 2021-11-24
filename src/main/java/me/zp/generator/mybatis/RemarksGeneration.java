package me.zp.generator.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：
 * 2021/5/27 9:13.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
@Slf4j
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
            //文章名称
            remarks.put(aCla.getSimpleName(), chl);
            //表名称
            TableRemark tableRemark = new RegularTableName(entityPath, aCla);
            tableNames.put(aCla.getSimpleName(), tableRemark.getTableName());
        }
    }

    private void remark(String entityPath, Class<?> aCla, Map<String, String> chl) throws IOException {
        String targetPath = entityPath + aCla.getSimpleName() + ".java";
        File file = new File(targetPath);
        if (!file.exists()) {
            return;
        }
        //字段说明
        FieldsRemark fieldsRemark = new RegularFieldsRemark(file);
        chl.putAll(fieldsRemark.getRemark());
        Class<?> superclass = aCla.getSuperclass();
        remark(entityPath, superclass, chl);
    }


    interface FieldsRemark {

        /**
         * 字段文章说明
         *
         * @return
         */
        Map<String, String> getRemark();
    }

    interface TableRemark {

        /**
         * 获取表名称
         *
         * @return
         */
        String getTableName() throws IOException;
    }


    /**
     * 正则方式匹配注释
     */
    private static class RegularFieldsRemark implements FieldsRemark {

        private final String rex_con_ann = "/\\*\\*\\r\\n\\s*\\*\\s*(.*)\\r\\n.*\\r\\n.*\\r\\n\\s*private\\s.*\\s(.*);";
        private final String rex = "/\\*\\*\\r\\n\\s*\\*\\s*(.*)\\r\\n.*\\r\\n\\s*private\\s.*\\s(.*);";

        private String content;

        public RegularFieldsRemark(File file) {
            try (InputStream inputStream = new FileInputStream(file)) {
                this.content = IOUtils.toString(inputStream);
            } catch (IOException e) {

            }
        }

        @Override
        public Map<String, String> getRemark() {
            Map<String, String> chl = new HashMap<>(16);
            List<String> matchConAnn = match(rex_con_ann);
            for (String str : matchConAnn) {
                String remark = str.replaceAll(rex_con_ann, "$1");
                String field = str.replaceAll(rex_con_ann, "$2");
                chl.put(field.trim(), remark.trim());
            }

            List<String> match = match(rex);
            for (String str : match) {
                String remark = str.replaceAll(rex, "$1");
                String field = str.replaceAll(rex, "$2");
                chl.put(field.trim(), remark.trim());
            }
            return chl;
        }

        private List<String> match(String regex) {
            List<String> matchData = new ArrayList<>();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                matchData.add(matcher.group());
            }
            return matchData;
        }

    }

    /**
     * 循环遍历匹配注释
     */
    private static class ForEachFieldsRemark implements FieldsRemark {
        private List<String> stringList;
        private String targetPath;

        public ForEachFieldsRemark(File file) {
            this.targetPath = file.getPath();
            try (InputStream inputStream = new FileInputStream(file)) {
                stringList = IOUtils.readLines(inputStream, "utf-8");
            } catch (IOException e) {

            }
        }

        @Override
        public Map<String, String> getRemark() {
            Map<String, String> chl = new HashMap<>(16);
            try {
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

            } catch (Exception e) {
                System.err.println("remark analysis error path:" + targetPath);
            }
            return chl;
        }

    }

    /**
     * 循环遍历匹配表明
     */
    private static class ForEachTableName implements TableRemark {

        /**
         * 实体地址
         */
        private final String entityPath;

        /**
         * 类
         */
        private final Class<?> aCla;

        public ForEachTableName(String entityPath, Class<?> aCla) {
            this.entityPath = entityPath;
            this.aCla = aCla;
        }

        @Override
        public String getTableName() throws IOException {
            return tabName(entityPath, aCla);
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


    private static class RegularTableName implements TableRemark {

        private final String rex_con_ann = "\\/\\*\\*(.*\\n)*public";

        /**
         * 实体地址
         */
        private final String entityPath;

        /**
         * 类
         */
        private final Class<?> aCla;

        public RegularTableName(String entityPath, Class<?> aCla) {
            this.entityPath = entityPath;
            this.aCla = aCla;
        }

        @Override
        public String getTableName() throws IOException {
            String targetPath = entityPath + aCla.getSimpleName() + ".java";
            File file = new File(targetPath);
            if (!file.exists()) {
                return "";
            }
            try (InputStream inputStream = new FileInputStream(file)) {
                String stringList = IOUtils.toString(inputStream, "utf-8");
                Pattern pattern = Pattern.compile(rex_con_ann);
                Matcher matcher = pattern.matcher(stringList);
                if (matcher.find()) {
                    String group = matcher.group();
                    return group
                            .replaceAll("\\@.*", "")
                            .replace("public", "")
                            .replace("\\/", "")
                            .replace("\\*", "")
                            .replace("\t", "")
                            .replace("\r", "")
                            .replace("\n", "")
                            .replace("类", "表");

                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return "";
        }
    }
}

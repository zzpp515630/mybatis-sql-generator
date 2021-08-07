package me.zp.generator.mybatis;

import lombok.extern.slf4j.Slf4j;
import me.zp.generator.model.CommonField;
import org.apache.commons.io.IOUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 * 2021/5/27 9:16.
 *
 * @author zhangpeng2
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class SqlFileGeneration {

    private final List<CommonField> newTableMap;
    private final List<CommonField> addTableMap;

    public SqlFileGeneration(List<CommonField> newTableMap, List<CommonField> addTableMap) {
        this.newTableMap = newTableMap;
        this.addTableMap = addTableMap;
    }

    public void generateSqlFile(String sourcePath) throws IOException {
        File file = new File(sourcePath + "/sql/");
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            log.info("sql file mkdirs is {}", mkdirs);
        }

        List<String> table = new ArrayList<>();

        for (CommonField commonField : newTableMap) {
            List<String> tableSql = commonField.createTableSql();
            tableSql.add(0, "#" + commonField.getRemarks());
            table.addAll(tableSql);
            table.add("\r");
        }
        for (CommonField commonField : addTableMap) {
            List<String> addFields = commonField.addFieldsSql();
            addFields.add(0, "#" + commonField.getRemarks());
            table.addAll(addFields);
            table.add("\r");
        }
        if (CollectionUtils.isEmpty(table)) {
            return;
        }
        String format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(ZonedDateTime.now());
        IOUtils.writeLines(table, null, new FileOutputStream(file.getAbsoluteFile() + "/" + format + ".sql"), "utf-8");
    }


}

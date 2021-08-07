package me.zp.generator.optimization;

import me.zp.generator.mybatis.ReverseGeneration;
import me.zp.generator.mapper.OptimizationMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 优化处理器
 *
 * @author zhang peng
 */
//@Component
public class OptimizationHandler {


    /**
     * 源码地址
     */
    private static final String SOURCE_PATH;

    private static File file;

    public static List<String> strings = Collections.synchronizedList(new ArrayList<>());

    static {
        String path = ReverseGeneration.class.getResource("/").getPath();
        SOURCE_PATH = path.substring(0, path.indexOf("target"));
//        file = new File(SOURCE_PATH + "/optimization");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
    }


    private String header = "| id |  select_type  |   table             | partitions |   type    |      possible_keys  |      key               | key_len  | ref  | rows  | filtered  |      extra    |";
    private String row = "|%-3s |%-14s |%-20s |%-11s |%-10s |%-20s |%-14s |%-9s |%-5s |%-6s |%-10s |%-20s |";


    //    @Resource
    private OptimizationMapper optimizationMapper;

//    @PostConstruct
//    public void initOptimization() {
////        new Thread(new Runnable() {
////            @SneakyThrows
////            @Override
////            public void run() {
////                running();
////            }
////        }).start();
//    }

    private void running() throws InterruptedException {
        while (true) {
            try {
                for (int i = 0; i < strings.size(); i++) {
                    String s = strings.get(i);
                    strings.remove(i);
                    i--;
                    System.out.println(s);
                    try {
                        List<OptimizationVo> explain = optimizationMapper.findExplain(s);
                        boolean falg = false;
                        for (OptimizationVo optimizationVo : explain) {
                            if (null == optimizationVo) {
                                continue;
                            }
                            if (optimizationVo.getType().equals("ALL")) {
                                //此sql需要优化
                                falg = true;
                                break;
                            }
                        }
                        if (falg) {
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file + "/sql.sql", true)));
                            bufferedWriter.write("sql:" + s);
                            bufferedWriter.newLine();
                            bufferedWriter.write(header);
                            bufferedWriter.newLine();
                            for (OptimizationVo optimizationVo : explain) {
                                String format = String.format(row,
                                        optimizationVo.getId().toString(),
                                        optimizationVo.getSelectType(),
                                        optimizationVo.getTable(),
                                        optimizationVo.getPartitions(),
                                        optimizationVo.getType(),
                                        optimizationVo.getPossibleKeys(),
                                        optimizationVo.getKey(),
                                        optimizationVo.getKeyLen(),
                                        optimizationVo.getRef(),
                                        optimizationVo.getRows(),
                                        optimizationVo.getFiltered(),
                                        optimizationVo.getExtra());

                                bufferedWriter.write(format);
                                bufferedWriter.newLine();
                            }
                            bufferedWriter.write("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                            bufferedWriter.newLine();
                            bufferedWriter.newLine();
                            bufferedWriter.newLine();
                            bufferedWriter.close();
                        }
//                        System.out.println(explain);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(5000L);
        }
    }

}

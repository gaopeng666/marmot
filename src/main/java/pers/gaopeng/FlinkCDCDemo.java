package pers.gaopeng;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

public class FlinkCDCDemo {
    public static void main(String[] args) {

        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().inStreamingMode().useBlinkPlanner().build();

        TableEnvironment tableEnv = TableEnvironment.create(environmentSettings);

        // 连接mysql数据源创建cdc source表
        tableEnv.executeSql("CREATE TABLE mysql_binlog (" +
                " `record_id` INT," +
                " `user_name` STRING," +
                " `url` STRING" +
                ") WITH (" +
                " 'connector' = 'mysql-cdc'," +
                " 'hostname' = 'localhost'," +
                " 'port' = '3306'," +
                " 'username' = 'root'," +
                " 'password' = '1209.ccc'," +
                " 'database-name' = 'test'," +
                " 'table-name' = 'click'," +
                " 'scan.incremental.snapshot.enabled' = 'false'," +
                " 'scan.startup.mode' = 'initial'" +
                ")");

//        // 创建mysql源表
//        tableEnv.executeSql("create table mysql_table_source(" +
//                "record_id INT" +
//                ",user_name String" +
//                ",url String" +
//                ",primary key (record_id) not enforced" +
//                ")WITH (" +
//                "'connector' = 'jdbc'," +
//                "'url' = 'jdbc:mysql://localhost:3306/test?useSSL=false'," +
//                "'username' = 'root'," +
//                "'password' = '1209.ccc'," +
//                "'table-name' = 'click'," +
//                "'sink.buffer-flush.max-rows' = '1'" +
//                ")");


        // 创建mysql输出表
        tableEnv.executeSql("create table mysql_table_sink(" +
                "record_id INT" +
                ",user_name String" +
                ",url String" +
                ",primary key (record_id) not enforced" +
                ")WITH (" +
                "'connector' = 'jdbc'," +
                "'url' = 'jdbc:mysql://localhost:3306/test?useSSL=false'," +
                "'username' = 'root'," +
                "'password' = '1209.ccc'," +
                "'table-name' = 'click_sink'," +
                "'sink.buffer-flush.max-rows' = '1'" +
                ")");

//        // 表结果输出到控制台
//        tableEnv.executeSql("create table print_table_sink(" +
//                "record_id INT" +
//                ",`user` String" +
//                ",`url` String" +
//                ")WITH (" +
//                "'connector' = 'print'" +
//                ")");

        tableEnv.executeSql("insert into mysql_table_sink select `record_id`, `user_name`, `url` from mysql_binlog");
    }
}

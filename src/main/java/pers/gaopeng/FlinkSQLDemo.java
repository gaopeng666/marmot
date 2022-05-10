package pers.gaopeng;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import static org.apache.flink.table.api.Expressions.$;

public class FlinkSQLDemo {
    public static void main(String[] args) throws Exception {
        // 通过steam运行时环境创建table环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        DataStreamSource<Event> clickSource = env.addSource(new ClickSource());

        // 直接创建table环境
        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().inStreamingMode().useBlinkPlanner().build();
        TableEnvironment tableEnvironment = TableEnvironment.create(environmentSettings);

        // 通过DataStream转换为table
        Table table1 = tableEnv.fromDataStream(clickSource);
        // 通过查询执行sql生成table
        Table table2 = tableEnv.sqlQuery("select user,url from " + table1);

        // 将DataStream注册成为临时view，在sql中才可以使用
        tableEnv.createTemporaryView("view_from_source", clickSource);
        // 将table注册成为临时view，在sql中才可以使用
        tableEnv.createTemporaryView("view_from_table", table1);
        //还可以通过创建表的方式连接到外部数据源
        tableEnv.executeSql("create table mysql_table(" +
                "record_id INT" +
                ",user_name String" +
                ",url String" +
                ")WITH (" +
                "'connector' = 'jdbc'," +
                "'url' = 'jdbc:mysql://localhost:3306/test?useSSL=false'," +
                "'username' = 'root'," +
                "'password' = '1209.ccc'," +
                "'table-name' = 'click'," +
                "'sink.buffer-flush.max-rows' = '1'" +
                ")");

        tableEnv.executeSql("create table print_table(" +
                "`user` String" +
                ",`url_count` BIGINT" +
                ")WITH (" +
                "'connector' = 'print'" +
                ")");

        // 通过查询执行sql生成table
        Table table3 = tableEnv.sqlQuery("select user,url from mysql_table");
        // 根据table生成新的table
        Table table4 = tableEnv.from("mysql_table");
        // 通过table api 生成table 和上面sqlQuery效果相同
        Table table5 = table4.select($("user_name")).where($("user_name").isEqual("Bob"));


        // 将table数据写出到刚才注册的mysql表
        table5.executeInsert("mysql_table");
        // 执行插入sql语句写出数据到mysql
        tableEnv.executeSql("insert into mysql_table select `id`, `user`, `url` from view_from_source");
        // 执行插入sql语句写出数据到控制台
        tableEnv.executeSql("insert into print_table select `user`, count(`url`) from view_from_source group by `user`");


        // 将table转换成DataStream
        DataStream<Row> newDataStream = tableEnv.toDataStream(table5);
        // 如果table 包含update操作例如使用聚合函数 只能使用变更日志流或者回撤流
        DataStream<Row> newDataStream2 = tableEnv.toChangelogStream(table5);

    }
}

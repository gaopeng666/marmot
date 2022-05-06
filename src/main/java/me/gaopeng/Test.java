package me.gaopeng;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class Test {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        DataStreamSource<String> streamSource = env.fromElements("bob", "alis");


        // table 生成方式
        Table table1 = tableEnv.fromDataStream(streamSource);

        Table table2 = tableEnv.sqlQuery("select * from sink_table");

        tableEnv.executeSql("create table sink_table");

        tableEnv.createTemporaryView("view_table_from_source", streamSource);

        tableEnv.createTemporaryView("view_table_from_table", table1);

        Table table3 = tableEnv.from("sink_table");


        table1.executeInsert("sink_table");


    }
}

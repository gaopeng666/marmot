package test;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class Doris {

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 50; i++) {

            Thread t1 = new Thread(new Com());

            t1.start();
        }

    }


    public static class Com implements Runnable {

        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String CONNECTION_URL = "jdbc:mysql://172.16.150.19:6033/ecs_analyze?rewriteBatchedStatements=true";
        String driverClassName = "com.mysql.cj.jdbc.Driver";    //启动驱动
        String url = "jdbc:mysql://172.16.150.19:6033/ecs_analyze";    //设置连接路径
        String username = "root";

        public Com() {
            try {
//            com.cloudera.impala.jdbc41.Driver v = null;
                Class.forName(driverClassName);
                con = DriverManager.getConnection(url, "root", "Trendy@123");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {


            while (true) {
                try {
                    Random rand = new Random();

                    int entry_time_start = rand.nextInt(700) + 1;

                    int entry_time_end = entry_time_start + 30;

//                String sql = "select a.tid,count(*),sum(std_mileage),avg(diff_mileage),avg(gps_mileage),avg(meter_mileage),avg(ecu_mileage),avg(total_fuel_cons),avg(diff_fuel_cons) ,avg(std_fuel_cons) from kudu_via_city_pdi3 as a where entry_time between "+entry_time_start+" and "+entry_time_end+" and exists (select null from car2 as b where a.tid=b.tid) group by a.tid order by sum(std_mileage) asc limit 1 offset 0";


                    /*java.util.Date date = new Date();
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE, i); //把日期往后增加一天,整数  往后推,负数往前移动
                    date = calendar.getTime(); //这个时间就是日期往后推一天的结果*/
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


                    Date dstart = sdf.parse("2021-11-05");

                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(dstart);
                    calendar.add(calendar.DATE, rand.nextInt(1000)); //把日期往后增加一天,整数  往后推,负数往前移动
                    dstart = calendar.getTime(); //这个时间就是日期往后推一天的结果

                    calendar = new GregorianCalendar();
                    calendar.setTime(dstart);
                    calendar.add(calendar.DATE, 30);

                    Date dend = calendar.getTime();

                    String sstart = sdf.format(dstart);
                    String send = sdf.format(dend);

                    String sql = "select * from ods_c_temperature_di where device id =";


                    Date d1 = new Date();
                    ps = con.prepareStatement(sql);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        System.out.println(Thread.currentThread() + "===>" + d1 + "-----" + new java.util.Date() + "----" + rs.getString(2));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        rs.close();
                        ps.close();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    }

}
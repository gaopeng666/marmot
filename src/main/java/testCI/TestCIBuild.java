package testCI;

import com.google.common.collect.Lists;

import java.util.ArrayList;

public class TestCIBuild {
    public static void main(String[] args) {
        ArrayList<Object> list = Lists.newArrayList();
        list.add(1);
        list.add("");
        list.add(1.11);
        Object[] array = list.toArray();
        String s = array[1].toString();
    }
}

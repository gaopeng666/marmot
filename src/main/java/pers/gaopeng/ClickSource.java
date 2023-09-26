package pers.gaopeng;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

public class ClickSource implements SourceFunction<Event> {

    private Boolean running = true;

    @Override
    public void run(SourceContext<Event> ctx) throws Exception {

        Random random = new Random();

        String[] users = {"Mary", "Alice", "Bob", "Cary"};
        String[] urls = {"./home", "./cart", "./fav", "./prod?id=100"};
        Integer id = 1;

        while(running){
            String user = users[random.nextInt(users.length)];
            String url = urls[random.nextInt(urls.length)];
            Long timestamp = System.currentTimeMillis();
            ctx.collect(new Event(user,url,id,timestamp));

            id += 1;
            Thread.sleep(1000L);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}

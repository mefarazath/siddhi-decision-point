package org.wso2.sample.siddhi.decision.point.internal;

import org.wso2.sample.siddhi.decision.point.io.Constants;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;

import java.util.stream.Stream;

public class Test {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("-------------- SiddhiDecisionPointComponent ACTIVATION STARTED ----------------");

        String siddhiApp = "@App:name(\"ReceiveAndCount\") " +
                "" +
                "define stream StockEventStream (symbol string, string app, price float, volume long); " +
                " " +
                "@info(name = 'query1') " +
                "from StockEventStream#window.time(60 sec)  " +
                "select symbol, sum(price) as price, sum(volume) as volume " +
                "group by symbol, app " +
                "insert into AggregateStockStream ;";

        String siddhiApp2 = "@App:name('" + Constants.APP_NAME + "') \n" +
                "                \n" +
                "define stream StockEventStream (user string, application string, logins long); \n" +
                "\n" +
                "@info(name = 'query1')\n" +
                "from StockEventStream#window.time(3 sec)\n" +
                "select user, application, sum(logins) as price group by user , application\n" +
                "insert into AggregateStockStream ;";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp2);

        System.out.println(siddhiAppRuntime.getName());
        System.out.println("Sending Events to Siddhi App!....");

        //Start SiddhiApp runtime
        siddhiAppRuntime.start();

        siddhiAppRuntime.addCallback("AggregateStockStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
//
//                System.out.println("Events recieved : " + events.length);
//                Stream.of(events).forEach(System.out::println);
//                EventPrinter.print(events);
            }
        });

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long l, Event[] events, Event[] expiredEvents) {

                System.out.println("New In Events: " + events.length);
                Stream.of(events).forEach(System.out::println);

                System.out.println("Expired Events: " + expiredEvents.length);
                Stream.of(expiredEvents).forEach(System.out::println);
                if (expiredEvents.length > 1) {
                    System.exit(-1);
                }

                System.out.println();
            }
        });

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("StockEventStream");

        int count = 0;
        while (true) {
            inputHandler.send(new Object[]{"IBM", "app1", Integer.toUnsignedLong(count)});
            inputHandler.send(new Object[]{"WSO2", "app1", Integer.toUnsignedLong(count)});
            inputHandler.send(new Object[]{"GOOG", "app1", Integer.toUnsignedLong(count)});
            count++;
        }
    }
}

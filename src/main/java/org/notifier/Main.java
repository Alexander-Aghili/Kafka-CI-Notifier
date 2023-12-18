package org.notifier;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.jetbrains.annotations.NotNull;
import org.notifier.testAnalysis.Datapoint;
import org.notifier.testAnalysis.Test;
import org.notifier.testExtractor.*;

import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/*
 *  TODO: For project
 *  - Figure out trends
 *  - Figure out how to discover trends
 *  - Notification system
 *  - Storage System
 *  - UI
 */
public class Main {

    public static String readInputStreamAsString(InputStream in)
            throws IOException {

        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            byte b = (byte)result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        String jsonData = GetTestsJson.getTestJson();
        ArrayList<Test> tests = GetTestLinks.getTestsFromJsonData(jsonData);
        ArrayList<Test> temp = new ArrayList<>();
        for (int i = 0; i < 55; i++) {
            temp.add(tests.get(i));
        }
        tests = temp;
        // Create a ThreadPoolExecutor with a maximum of 500 threads and infinite keep-alive time
        ExecutorService executor = new ThreadPoolExecutor(
                tests.size(),  // corePoolSize
                tests.size(), // maximumPoolSize
                Long.MAX_VALUE, // keepAliveTime set to Long.MAX_VALUE for infinite time
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
        System.out.println(Runtime.getRuntime().availableProcessors());
//        ExecutorService executor = Executors.newFixedThreadPool(tests.size(), );

        List<Callable<InputStream>> runList = new ArrayList<>();
        System.out.println(tests.size());

        for (Test test : tests) {
            String link = test.getDataLink();
            System.out.println(test.getUILink());

            runList.add(new Request(link));

        }

        List<Future<InputStream>> futures = executor.invokeAll(runList);

        executor.shutdown();

        for (int i = 0; i < tests.size(); i++) {
            InputStream input = futures.get(i).get(Integer.MAX_VALUE, TimeUnit.SECONDS);
            String json = readInputStreamAsString(input);
            Test test = new Test(json);
            test.calculateWeightedValue();
            System.out.println(test.getWeightedValue() + ": " + test.getUILink());
        }

        Collections.sort(tests);
        System.out.println(tests.get(0).getUILink());
    }
}
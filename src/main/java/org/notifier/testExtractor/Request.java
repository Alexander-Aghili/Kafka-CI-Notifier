package org.notifier.testExtractor;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

public class Request implements Callable<InputStream> {

    private String url;

    public Request(String url) {
        this.url = url;
    }

    @Override
    public InputStream call() throws Exception {
        URL u = new URL(url);
        u.openConnection().setConnectTimeout(Integer.MAX_VALUE);
        u.openConnection().setReadTimeout(Integer.MAX_VALUE);
        return u.openStream();
    }

}
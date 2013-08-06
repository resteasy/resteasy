package org.jboss.resteasy.test.async;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.TimeoutHandler;

public class MyTimeoutHandler implements TimeoutHandler
{

    private int function = 0;

    public MyTimeoutHandler(int function) {
        super();
        this.function = function;
    }

    @Override
    public void handleTimeout(AsyncResponse asyncResponse) {
        switch (function) {
            case 1:
                asyncResponse.setTimeout(200, TimeUnit.MILLISECONDS);
                break;
            case 2:
                asyncResponse.cancel();
                break;
            case 3:
                asyncResponse.resume(Resource.RESUMED);
                break;
        }
    }

}

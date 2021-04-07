package opensrcerer;

import opensrcerer.requests.BTJRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Designed for build tests.
 */
public class Tester {

    /**
     * Logger for this class.
     */
    private final Logger lgr = LoggerFactory.getLogger(Tester.class);

    /**
     * Number of calls that the BTJ Client has successfully received.
     */
    private final AtomicInteger callsReceived = new AtomicInteger();

    /**
     * A List that stores Request creation Methods to invoke reflectively.
     */
    private final List<Method> requestMethods = new ArrayList<>();

    /**
     * Number of calls that the BTJ Client has made.
     */
    private static final int callsmade = 6;

    @Test
    public void test() {
        String token = System.getenv("BYTESTOBITS_TOKEN"); // Retrieve bytestobits token from envs

        try {
            BTJ btj = BTJ.getBTJ(token); // Initialize BTJ with Token

            // Asynchronous callbacks
            for (int index = 0; index < 2; ++index) {
                getRandomRequest(btj).queue(randomText -> callsReceived.incrementAndGet()); // Async callback
            }
            // Asynchronous future calls
            for (int index = 0; index < 2; ++index) {
                getRandomRequest(btj).submit().thenAccept(madLib -> callsReceived.incrementAndGet());
            }
            // Synchronous calls
            for (int index = 0; index < 2; ++index) {
                getRandomRequest(btj).complete();
                callsReceived.incrementAndGet();
            }

            Thread.sleep(10000); // Wait an adequate amount of time for async requests to finish
        } catch (Exception ex) {
            lgr.error("Some issue occurred:", ex);
        }

        lgr.debug("Calls made: {}, Calls Received: {}", callsmade, callsReceived.get());
        assertEquals(callsmade, callsReceived.get());
    }

    /**
     * Retrieve a random BTJRequest from a BTJ instance.
     * @param btj The BTJ instance to pull requests from.
     * @return A random BTJRequest. If unable to get a random request, uses a preset one.
     */
    @NotNull
    private BTJRequest<?> getRandomRequest(BTJ btj) {
        BTJRequest<?> request = btj.getText();

        if (requestMethods.isEmpty()) {
            for (Method m : btj.getClass().getDeclaredMethods()) {
                switch (m.getName()) {
                    case "getInfo", "getWord", "getText", "getMadLib", "getMeme" -> requestMethods.add(m);
                    case "getLyrics", "getRedditPosts" -> {
                        if (m.getParameterCount() == 1) {
                            requestMethods.add(m); // Only add method overrides with one argument
                        }
                    }
                }
            }
        }

        try {
            // Pick random method
            Method requestMethodToInvoke = requestMethods.get(ThreadLocalRandom.current().nextInt(0, requestMethods.size()));
            if (requestMethodToInvoke.getParameterCount() == 1) {
                request = (BTJRequest<?>) requestMethodToInvoke.invoke(btj, "ledzeppelin"); // Used for both song lyrics and Reddit Posts
            } else {
                request = (BTJRequest<?>) requestMethodToInvoke.invoke(btj); // For other methods that do not take arguments
            }
        } catch (Exception ex) {
            lgr.error("Could not invoke target:", ex);
        }

        return request;
    }
}

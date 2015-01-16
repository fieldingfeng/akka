package org.akka;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class ThreadPi {

    static double calculatePiFor(int slice, int nrOfIterations) {
        double acc = 0.0;
        for (int i = slice * nrOfIterations; i <= ((slice + 1) * nrOfIterations - 1); i++) {
            acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
        }
        return acc;
    }

    static double calculatePi() {
        double acc = 0.0;
        
        for (int i = 0; i <= 1000000000; i++) {
            acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
        }
        return acc;    	
    }
    
    private static long piTest(final int numThreads) throws InterruptedException {

        final int numMessages = 100000;
        final int step = 10000;

        final ExecutorService test = Executors.newFixedThreadPool(numThreads);
        final AtomicInteger latch = new AtomicInteger(numMessages);
        final AtomicReference<Double> result = new AtomicReference<>(0.0);
        final AtomicLong timSum = new AtomicLong(0);

        final long tim = System.currentTimeMillis();
        for ( int i= 0; i< numMessages; i++) {
            final int finalI = i;
            while ( ((ThreadPoolExecutor)test).getQueue().size() > 40000 ) {
                LockSupport.parkNanos(100);
            }
            test.execute(new Runnable() {
                public void run() {
                    double res = calculatePiFor(finalI, step);
                    Double expect;
                    boolean success;
                    do {
                        expect = result.get();
                        success = result.compareAndSet(expect,expect+res);
                    } while( !success );
                    int lc = latch.decrementAndGet();
                    if (lc == 0 ) {
                        long l = System.currentTimeMillis() - tim;
                        timSum.set(timSum.get()+l);
                        System.out.println("pi: " + result.get() + " t:" + l + " finI " + finalI);
                        test.shutdown();
                    }
                }
            });
        }
        while (latch.get() > 0 ) {
            LockSupport.parkNanos(1000*500); // don't care as 0,5 ms are not significant per run
        }
        return timSum.get();
    }

    public static void main( String arg[] ) throws Exception {
    	long res = piTest(4);
    	System.out.println(res);
    }
}

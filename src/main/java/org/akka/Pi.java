package org.akka;

public class Pi {
    double calculatePiFor(int slice, int nrOfIterations) {
        double acc = 0.0;
        for (int i = slice * nrOfIterations; i <= ((slice + 1) * nrOfIterations - 1); i++) {
            acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
        }
        return acc;
    }

    public static void main(String[] argv) {
    	Pi pi = new Pi();
    	long begin = System.currentTimeMillis();
    	System.out.println(pi.calculatePiFor(0, 1000000000));
    	System.out.println(System.currentTimeMillis() - begin);

    }
}

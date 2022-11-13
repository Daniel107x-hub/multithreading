package com.daniel107x.coordination;

import java.math.BigInteger;

public class ThreadInterruption {

    public static void main(String[] args) {
        /**
         * Stop a thread by calling the interrupt method
         *
         * Once called, in that thread a InterruptedException will be thrown
         */
        Thread thread = new Thread(new BlockingTask());
        thread.start();
        thread.interrupt();

        /**
         * Stop a thread that is running a long time consuming task
         *
         * If we try to interrupt the method that is running a long task, we need to find the place where the time is
         * being spent and in there, we need to check if the thread has been interrupted or not
         */
        thread = new Thread(new LongComputationTask(new BigInteger("200000"), new BigInteger("10000000")));
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Interrupting thread");
        thread.interrupt();

        /**
         * Avoid a task to block the stop of the application (Daemon thread)
         *
         * Even if the long calculation has not finished, the fact that the main thread ended will make the entire application stop
         */
        thread = new Thread(new LongComputationTaskWithoutInterrupted(new BigInteger("200000"), new BigInteger("10000000")));
        thread.setDaemon(true);
        thread.start();
        thread.interrupt();
        System.out.println("Application finished");
    }

    private static class BlockingTask implements Runnable{
        @Override
        public void run() {
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                System.out.println("Exiting blocking thread...");
            }
        }
    }

    private static class LongComputationTask implements Runnable{
        private final BigInteger base;
        private final BigInteger power;

        public LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + power + " = " + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power){
            BigInteger result  = BigInteger.ONE;
            for(BigInteger i = BigInteger.ZERO ; i.compareTo(power) != 0 ; i = i.add(BigInteger.ONE)){
                if(Thread.currentThread().isInterrupted()){
                    System.out.println("Prematurely interrupted computation");
                    return BigInteger.ZERO;
                }
                result = result.multiply(base);
            }
            return result;
        }
    }

    private static class LongComputationTaskWithoutInterrupted implements Runnable{
        private final BigInteger base;
        private final BigInteger power;

        public LongComputationTaskWithoutInterrupted(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + power + " = " + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power){
            BigInteger result  = BigInteger.ONE;
            for(BigInteger i = BigInteger.ZERO ; i.compareTo(power) != 0 ; i = i.add(BigInteger.ONE)){
                result = result.multiply(base);
            }
            return result;
        }
    }
}

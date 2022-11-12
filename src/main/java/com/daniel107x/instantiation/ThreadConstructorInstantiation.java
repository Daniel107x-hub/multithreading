package com.daniel107x.instantiation;

public class ThreadConstructorInstantiation {
    public static void main(String[] args) {
        /**
         * All threads related properties are in Thread class
         */

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                // Code to be run in a new thread
            }
        });

        // It can also be written as a lambda
        thread = new Thread(()->{
            // Code to be executed in a new thread
            System.out.println("Current thread: "+ Thread.currentThread().getName());
            System.out.println("Current priority: "+ Thread.currentThread().getPriority());

            // Throw exception to test the uncaught exception handler
            throw new RuntimeException("Intentional Exception");
        });

        // Set thread name
        thread.setName("New Worker Thread");

        //Set thread priority
        thread.setPriority(Thread.MAX_PRIORITY);

        // Get the name of the running thread
        System.out.println("Current thread: "+ Thread.currentThread().getName() + " before starting new thread");

        // Create new thread and start
        thread.start();
        System.out.println("Current thread: "+ Thread.currentThread().getName() + " after starting new thread");

        // Set a default handler for uncaught exceptions within the thread
        thread.setUncaughtExceptionHandler((Thread t, Throwable e)->{
            System.out.println("A critical exception happened in the thread: " + t.getName() + " error is: " + e.getMessage());
        });

        try {
            Thread.sleep(10000); //Asks the OS not to schedule this thread until this time passes
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}

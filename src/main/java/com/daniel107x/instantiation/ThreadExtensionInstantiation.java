package com.daniel107x.instantiation;

public class ThreadExtensionInstantiation {
    public static void main(String[] args) {
        Thread thread = new ThreadedClass();
        thread.setName("Threaded app");
        thread.start();
    }

    public static class ThreadedClass extends Thread{
        @Override
        public void run(){
            System.out.println("Hello from thread: " + this.getName());
        }
    }
}


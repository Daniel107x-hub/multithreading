package com.daniel107x.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * El CountDownLatch es una herramienta de sincronizacion que ayuda a mantener uno o mas hilos bloqueados hasta que
 * un conjunto de operaciones siendo realizadas por otros hilos sean terminadas
 *
 * El objeto es inicializado con una cuenta y el metodo await() bloquea la ejecucion del hilo actual hasta que la cuenta llega
 * a cero debido a las invocaciones en countDown(), tras lo cual todos los hilos son liberados y las llamadas
 * a await() subsecuentes retornaran inmediatamente.
 *
 * La cuenta no puede ser reseteada
 */
public class SimpleCountDownLatchExample {
    private static final int WORKER_THREADS = 5;

    public static void main(String[] args) throws InterruptedException {
        SimpleCountDownLatchObject latch = new SimpleCountDownLatchObject(WORKER_THREADS);
        List<Thread> workers = new ArrayList<>();
        for(int i = 0 ; i < WORKER_THREADS ; i++){
            Thread executable = new Thread(()->{
                System.out.println("Thread: " + Thread.currentThread().getName() + " executing job...");
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("Thread: " + Thread.currentThread().getName() + " finished executing job.");
                System.out.println("Thread: " + Thread.currentThread().getName() + " will wait for other threads.");
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            executable.setName("Thread-" + i);
            workers.add(executable);
        }
        for(Thread thread : workers) thread.start();
        for(Thread thread : workers) thread.join();
    }

    public static class SimpleCountDownLatch {
        private int count = 0;
        private Lock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();

        public SimpleCountDownLatch(int count) {
            if(count < 0) throw new IllegalArgumentException("Count cannot be negative");
            this.count = count;
        }

        /**
         * Causes the current thread to wait until the latch has counted down to zero.
         * If the current count is already zero, then this method returns immediately.
         * @throws InterruptedException
         */
        public void await() throws InterruptedException{
            lock.lock();
            try{
                while(this.count != 0) condition.await();
            }finally{
                lock.unlock();
            }
            System.out.println("Thread: " + Thread.currentThread().getName() + " finished awaiting");
        }

        /**
         * Decrements the count of the latch, releasing all waiting threads when the count reaches to zero.
         * If the current cound is already zero, then nothing happens.
         */
        public void countDown(){
            lock.lock();
            if(this.count != 0){
                this.count--;
                if(this.count == 0){
                    System.out.println("Count is finally " + this.count);
                    condition.signalAll();
                }
            }
            lock.unlock();
        }

        /**
         * Returns the current count
         * @return
         */
        public int getCount(){
            return this.count;
        }
    }

    public static class SimpleCountDownLatchObject {
        private int count = 0;

        public SimpleCountDownLatchObject(int count) {
            if(count < 0) throw new IllegalArgumentException("Count cannot be negative");
            this.count = count;
        }

        /**
         * Causes the current thread to wait until the latch has counted down to zero.
         * If the current count is already zero, then this method returns immediately.
         * @throws InterruptedException
         */
        public void await() throws InterruptedException{
            synchronized (this){
                while(this.count > 0) this.wait();
            }
            System.out.println("Thread: " + Thread.currentThread().getName() + " finished awaiting");
        }

        /**
         * Decrements the count of the latch, releasing all waiting threads when the count reaches to zero.
         * If the current cound is already zero, then nothing happens.
         */
        public void countDown(){
            synchronized (this){
                if(this.count > 0){
                    this.count--;
                    if(this.count == 0){
                        this.notifyAll();
                        System.out.println("Count is finally " + this.count);
                    }
                }
            }
        }

        /**
         * Returns the current count
         * @return
         */
        public int getCount(){
            return this.count;
        }
    }


}
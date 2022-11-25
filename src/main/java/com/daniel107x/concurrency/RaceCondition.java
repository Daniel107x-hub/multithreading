package com.daniel107x.concurrency;

public class RaceCondition {

    public static void main(String[] args) throws InterruptedException{
        InventoryCounter inventoryCounter = new InventoryCounter();
        Thread incrementingThread = new IncrementingThread(inventoryCounter);
        Thread decrementingThread = new DecrementingThread(inventoryCounter);
        incrementingThread.start();
        decrementingThread.start();
        incrementingThread.join();
        decrementingThread.join();
        System.out.println("There are " + inventoryCounter.items + " items in the inventory");
    }

    private static class IncrementingThread extends Thread{
        private InventoryCounter inventoryCounter;

        public IncrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for(int i = 0 ; i < 10000 ; i++) {
                this.inventoryCounter.increment();
            }
        }
    }

    private static class DecrementingThread extends Thread{
        private InventoryCounter inventoryCounter;

        public DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for(int i = 0 ; i < 10000 ; i++) {
                this.inventoryCounter.decrement();
            }
        }
    }

    private static class InventoryCounter{
        private Object lockingObject = new Object();
        private int items = 0;

        /*
        public synchronized void increment(){
            items++;
        }

        public synchronized void decrement(){
            items--;
        }

        public synchronized int getItems(){
            return items;
        }
        */

        public void increment(){
            synchronized (this.lockingObject){
                this.items++;
            }
        }

        public void decrement(){
            synchronized (this.lockingObject){
                this.items--;
            }
        }

        public int getItems(){
            synchronized (this.lockingObject){
                return this.items;
            }
        }

    }
}

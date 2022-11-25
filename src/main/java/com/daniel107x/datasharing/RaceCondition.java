package com.daniel107x.datasharing;

public class RaceCondition {

    public static void main(String[] args) throws InterruptedException{
        InventoryCounter inventoryCounter = new InventoryCounter();
        Thread incrementingThread = new IncrementingThread(inventoryCounter);
        Thread decrementingThread = new DecrementingThread(inventoryCounter);
        /*
         El tener a ambos hilos de esta manera hara que ejecuten operaciones sobre el mismo objeto compartido (Inventory)
         Esto provocara que se generen condiciones de carrera donde ambos hilos modifican los datos a la vez, por
         lo cual, el resultado puede ser incorrecto.

         -Inventory counter es un objeto compartido
         -Items++ e Items-- suceden al mismo tiempo y no son operaciones atomicas


         */
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
        private int items = 0;

        public void increment(){
            items++;
        }

        public void decrement(){
            items--;
        }

        public int getItems(){
            return items;
        }

    }
}

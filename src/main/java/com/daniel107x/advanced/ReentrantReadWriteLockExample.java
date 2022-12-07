package com.daniel107x.advanced;

import javax.swing.text.Highlighter;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockExample{
    private static final int MAX_PRICE = 1000;
    public static void main(String[] args) throws InterruptedException {
        InventoryDatabase db = new InventoryDatabase();
        Random random = new Random();
        for(int i = 0 ; i < 100000 ; i++){
            db.addItem(random.nextInt(MAX_PRICE));
        }

        Thread writer = new Thread(()->{
            while(true){
                db.addItem(random.nextInt(MAX_PRICE));
                db.removeItem(random.nextInt(MAX_PRICE));
                try{
                    Thread.sleep(10);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        writer.setDaemon(true);
        writer.start();

        int readerThreads = 7;
        List<Thread> readers = new ArrayList<>();
        for(int i = 0 ; i < readerThreads ; i++){
            Thread reader = new Thread(()->{
                for(int j = 0 ; j < 100000 ; j++){
                    int upperBoundPrice = random.nextInt(MAX_PRICE);
                    int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(MAX_PRICE) : 0;
                    db.getNumberOfItemsInPriceRange(lowerBoundPrice, upperBoundPrice);
                }
            });
            reader.setDaemon(true);
            readers.add(reader);
        }

        long startReadingTime = System.currentTimeMillis();
        readers.forEach(Thread::start);
        for (Thread reader : readers) {
            reader.join();
        }
        long endReadingTime =  System.currentTimeMillis();
        System.out.println(String.format("Reading took %d ms", endReadingTime - startReadingTime));

    }

    public static class InventoryDatabase{
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private ReentrantLock lock = new ReentrantLock();
        private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        private Lock readLock = rwLock.readLock();
        private Lock writeLock = rwLock.writeLock();

        public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound){
//            lock.lock();
            readLock.lock();
            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
                Integer toKey = priceToCountMap.floorKey(upperBound);
                if (fromKey == null || toKey == null || fromKey > toKey) {
                    return 0;
                }
                NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);
                int sum = 0;
                for (int numberOfItemsForPrice : rangeOfPrices.values()) sum += numberOfItemsForPrice;
                return sum;
            }finally{
//                lock.unlock();
                readLock.unlock();
            }
        }

        public void addItem(int price){
//            lock.lock();
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceToCountMap.get(price);
                if (numberOfItemsForPrice == null) priceToCountMap.put(price, 1);
                else priceToCountMap.put(price, numberOfItemsForPrice + 1);
            }finally{
//                lock.unlock();
                writeLock.unlock();
            }
        }

        public void removeItem(int price){
//            lock.lock();
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceToCountMap.get(price);
                if (numberOfItemsForPrice == null || numberOfItemsForPrice == 1) priceToCountMap.remove(price);
                else priceToCountMap.put(price, numberOfItemsForPrice - 1);
            }finally{
//                lock.unlock();
                writeLock.unlock();
            }
        }
    }
}

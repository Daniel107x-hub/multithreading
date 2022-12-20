package com.daniel107x.intercommunication;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class MatrixMultiplicationExample {
    private static final int N = 10;
    private static final String INPUT_FILE = "./out/matrices";
    private static final String OUT_FILE = "./out/results";
    public static void main(String[] args) throws IOException {
        ThreadSafeQueue queue = new ThreadSafeQueue();
        File inputFile=  new File(INPUT_FILE);
        File outputFile = new File(OUT_FILE);

        MatricesReaderProducer matricesReaderProducer = new MatricesReaderProducer(new FileReader(inputFile), queue);
        MatricesMultiplierConsumer matricesMultiplierConsumer = new MatricesMultiplierConsumer(queue, new FileWriter(outputFile));
        matricesMultiplierConsumer.start();
        matricesReaderProducer.start();
    }

    private static class MatricesMultiplierConsumer extends Thread{
        private ThreadSafeQueue threadSafeQueue;
        private FileWriter fileWriter;

        public MatricesMultiplierConsumer(ThreadSafeQueue threadSafeQueue, FileWriter fileWriter) {
            this.threadSafeQueue = threadSafeQueue;
            this.fileWriter = fileWriter;
        }

        private float[][] multiplyMatrices(float[][] matrix1, float[][] matrix2){
            float[][] result = new float[N][N];
            for(int r = 0 ; r < N ; r++){
                for(int c = 0 ; c < N ; c++){
                    for(int k = 0 ; k < N ; k ++){
                        result[r][c] += matrix1[r][k] * matrix2[k][c];
                    }
                }
            }
            return result;
        }

        @Override
        public void run(){
            while(true){
                MatricesPair matricesPair = threadSafeQueue.remove();
                if(matricesPair == null){
                    System.out.println("No more matrices to remove, consumer terminating...");
                    break;
                }
                float[][] result = multiplyMatrices(matricesPair.getMatrix1(), matricesPair.getMatrix2());
                try {
                    saveMatrixToFile(fileWriter, result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try{
                fileWriter.flush();
                fileWriter.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        private void saveMatrixToFile(FileWriter fileWriter, float[][] result) throws IOException{
            for(int r = 0 ; r < N ; r++){
                StringJoiner stringJoiner = new StringJoiner(",");
                for(int c = 0 ; c  < N ; c++){
                    stringJoiner.add(String.format("%.2f", result[r][c]));
                }
                fileWriter.write(stringJoiner.toString());
                fileWriter.write('\n');
            }
            fileWriter.write('\n');
        }
    }

    private static class MatricesReaderProducer extends Thread{
        private Scanner scanner;
        private ThreadSafeQueue threadSafeQueue;

        public MatricesReaderProducer(FileReader fileReader, ThreadSafeQueue threadSafeQueue){
            this.scanner = new Scanner(fileReader);
            this.threadSafeQueue = threadSafeQueue;
        }

        private float[][] readMatrix(){
            float[][] matrix = new float[N][N];
            for(int r = 0 ; r < N ; r++){
                if(!scanner.hasNext()) return null;
                String[] line = scanner.nextLine().split(",");
                for(int c = 0 ; c < N ; c++){
                    matrix[r][c] = Float.valueOf(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }

        @Override
        public void run(){
            while(true){
                float[][] matrix1 = readMatrix();
                float[][] matrix2 = readMatrix();
                if(matrix1 == null || matrix2 == null){
                    threadSafeQueue.terminate();
                    System.out.println("No more matrices to read, producer terminating...");
                    return;
                }
                MatricesPair matricesPair = new MatricesPair(matrix1, matrix2);
                threadSafeQueue.add(matricesPair);
            }
        }


    }

    private static class ThreadSafeQueue{
        private Queue<MatricesPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminate = false;
        private static final int MAX_CAPACITY = 5;

        public synchronized void add(MatricesPair matricesPair){
            while(queue.size() == MAX_CAPACITY){
                try{
                    wait();
                }catch(InterruptedException e){}
            }
            queue.add(matricesPair);
            isEmpty = false;
            notify();
        }

        public synchronized  MatricesPair remove(){
            MatricesPair matricesPair = null;
            while(isEmpty && !isTerminate){
                try{
                    wait();
                }catch(InterruptedException e){}
            }
            if(queue.size() == 1){
                isEmpty = true;
            }
            if(queue.size() == 0 && isTerminate){
                return null;
            }

            System.out.println("Queue size: " + queue.size());
            matricesPair = queue.remove();
            if(queue.size() == MAX_CAPACITY - 1){
                notifyAll();
            }
            return matricesPair;
        }

        public synchronized void terminate(){
            isTerminate = true;
            notifyAll();
        }
    }

    public static class MatricesPair{
        public float[][] matrix1;
        public float[][] matrix2;

        public MatricesPair(float[][] matrix1, float[][] matrix2) {
            this.matrix1 = matrix1;
            this.matrix2 = matrix2;
        }

        public float[][] getMatrix1() {
            return matrix1;
        }

        public void setMatrix1(float[][] matrix1) {
            this.matrix1 = matrix1;
        }

        public float[][] getMatrix2() {
            return matrix2;
        }

        public void setMatrix2(float[][] matrix2) {
            this.matrix2 = matrix2;
        }
    }
}

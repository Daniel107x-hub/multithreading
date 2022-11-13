package com.daniel107x.coordination;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadJoin {
    public static void main(String[] args) throws InterruptedException {
        List<Long> inputNumbers = Arrays.asList(0L, 3344000000L, 334L, 2342L, 43455L, 23L, 53451L);

        List<Thread> threads = new ArrayList<>();
        for(Long input : inputNumbers) threads.add(new FactorialThread(input));
        for(Thread thread : threads) thread.start();

        // Se genera una condicion de carrera ya que el hilo principal esta buscando resultados probablemente
        // antes de que los hilos terminen de computar

        for(Thread thread : threads){
            thread.join(2000); // Espera que el hilo actual retorne al hilo principal
        } // Depues de este loop, todos los hilos habran terminado

        for(Thread thread : threads){
            if(thread.isAlive()) thread.interrupt(); // Interrumpe hilos que sigan vivos aun despues del tiempo de join
        }

        // Si tenemos un numero muy grande, es posible que un hilo en particular bloquee toda la aplicacion, por esto
        // es conveniente proporcionar un tiempo al metodo join, el cual indicara el tiempo a esperar a que dicho hilo
        // retorne. Si no sucede en el tiempo establecido, se forzara la detencion de dicho hilo

        for(int i = 0 ; i < inputNumbers.size() ; i++){
            FactorialThread thread = (FactorialThread) threads.get(i);
            if(thread.isFinished()) System.out.println("Factorial of " + inputNumbers.get(i) + " is: " + thread.getResult());
            else System.out.println("Calculation for " + inputNumbers.get(i) + " is in progress...");
        }

        // Debido al hilo que podria no haber terminado aun, puede que la aplicacion no se finalice. Es necesario
        // validar dentro del hilo si el hilo ha sido interrumpido para evitar el bloqueo de la aplicacion
    }

    public static class FactorialThread extends Thread{
        private long inputNumber;
        private BigInteger result = BigInteger.ONE;
        private boolean isFinished = false;

        public FactorialThread(long inputNumber){
            this.inputNumber = inputNumber;
        }

        @Override
        public void run() {
            this.result = factorial(inputNumber);
            this.isFinished = true;
        }

        public BigInteger factorial(long inputNumber){
            BigInteger tmpResult = BigInteger.ONE;
            for(long i = inputNumber ; i > 0 ; i--){
                if(Thread.currentThread().isInterrupted()){
                    System.out.println("Forcefully stopped thread calculating factorial for: " + inputNumber);
                    return BigInteger.ZERO;
                }
                tmpResult = tmpResult.multiply(new BigInteger(Long.toString(i)));
            }
            return tmpResult;
        }

        public BigInteger getResult() {
            return result;
        }

        public boolean isFinished() {
            return isFinished;
        }
    }
}

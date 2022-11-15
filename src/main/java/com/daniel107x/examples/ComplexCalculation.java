package com.daniel107x.examples;

import java.math.BigInteger;

public class ComplexCalculation {
    public static void main(String[] args) {

    }

    public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2){
        BigInteger result;
        /*
            Calculate result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */
        PowerCalculatingThread calculation1 = new PowerCalculatingThread(base1,power1);
        PowerCalculatingThread calculation2 = new PowerCalculatingThread(base2, power2);

        calculation1.start();
        calculation2.start();


        try {
            calculation1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            calculation2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return calculation1.getResult().add(calculation2.getResult());

    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private final BigInteger base;
        private final BigInteger power;

        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
           /*
           Implement the calculation of result = base ^ power
           */
            BigInteger tmpResult = base;
            for(BigInteger i = power; i.compareTo(BigInteger.ONE) > 0; i = i.subtract(BigInteger.ONE)){
                tmpResult = tmpResult.multiply(base);
            }
            result = tmpResult;
        }

        public BigInteger getResult() { return result; }
    }


}

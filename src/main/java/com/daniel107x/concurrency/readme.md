# Concurrency problem
1. Dos hilos o mas pueden compartir el mismo recurso
2. Todos los hilos estan leyendo o modificando el recurso compartido al mismo tiempo
3. Las operaciones ejecutadas no son atomicas

```java
void aggregateFunction(){
    operation1();
    operation2();
    operation3();
}
```

Definimos una seccion critica como un conjunto de operaciones sobre un recurso que deberian de ser atomicas para
evitar los problemas de concurrencia tales como las condiciones de carrera. En el ejemplo anterior, la seccion critica
son los 3 metodos que se ejecutan.

```java
void aggregateFunction(){
    enter critical section
    operation1();
    operation2();
    operation3();
    finish critical section
}
```

La idea, seria que si un hilo esta ejecutando la seccion critical, otro hilo tratando de ejecutar la misma seccion
sera detenido hasta que el primer hilo salga de dicha seccion, entonces el siguiente hilo puede entrar y
ejecutar la seccion critica.

### Soluciones
Java proporciona la plabara reservada **Synchronized** que actua como mecanismo de bloqueo para evitar que una seccion
de codigo sea ejecutada por multiples hilos a la vez.
Modos de uso:
1. Declaracion de metodos como synchronized
    ```java
    public class ClassWithCriticalSections{
        public synchronized method1(){
            ...
        }
        
        public synchronized method2(){
            ...
        }
    }
    ```
    La palabra **syncrhonized** actua por objeto (***Monitor***). Es decir, si un hilo esta ejecutando algun metodo en el objeto, ningun
    otro hilo podra acceder a ningun metodo de dicho objeto 
   
2. Definir la seccion critica el codigo y restringir el acceso solo a dicha seccion haciendo uso de un locking object
    ```java
    public class ClassWithCriticalSections{
        Object lockingObject = new Object();
        Object lockingObject2 = new Object();
   
        public void method1(){
            synchronized(lockingObject){
                // Critical section
            }
        }
   
        public void method1(){
            synchronized(lockingObject2){
                // Critical section
            }
        }
    }
    ```
    Esto permitira que solo un hilo a la vez ejecute la seccion protegida por el lock
   

>Los bloques **synchronized** son ***reentrantes***, esto quiere decir que si un hilo ya esta en una seccion critica,
> podra acceder a otra seccion critica sin problema. Un hilo no puede prevenirse a si mismo de entrar a una seccion critica

### Lista de operaciones atomicas
1. Asignacion/lectura de referencias
    ```java
    Object a = new Object();
    Object b = new Object();
    a = b; //atomic
    ```
   
2. Asignaciones/lectura a tipos de datos primitivos excepto **long** y **double**
    - int
    - short
    - byte
    - float
    - char
    - boolean
    
Una solucion al problema con double o long es usar la palabra reservada **volatile**, la cual hace que dichas operaciones
sean seguras
```java
volatile double x = 1.9;
volatile double y = 9.0;

x = y; //atomic
```

3. Clases en **java.util.concurrent.atomic**    

## Condiciones de carrera
1. Multiples hilos accesana  u recurso compartido
2. Al menos un hilo esta modificando el recurso
3. El timing de la programacion de los hilos puede causar resultados incorrectos
4. El problema son operaciones noa tomicas sobre los recursos compartidos

### Soluciones:
- Identificar donde se sucita la condicion de carrera
- Proteccion de la seccion critica con un bloque synchronized

## Data races
```java
public class SharedClass{
    int x = 0;
    int y = 0;
    
    public void increment(){
        x++;
        y++;
    }
    
    public void checkDataRace(){
        if(y > x){
            throw new Exception("This is not meant to be possible");
        }
    }
}
```
x >= y siempre se cumplira ya que, o x fue alterado primero, o en la ultima iteracion los valores son iguales. Sin embargo
a la hora de ejecutar el codigo podemos ver que se dan multiples data races.

Esto se debe a multiples razones:
1. El compilador y el CPU pueden ejecutar el codigo desordenado para optimizar el rendimiento y el uso de recursos
2. Esto se realiza manteniendo la correctitus del codigo
3. La ejecucion fuera de orden es una caracteristica importante que permite optimizar la ejecucion del codigo
4. El compilador reordena las instrucciones para mejorar
   - Branch prediction: Loops optimizados, sentencias condicionales
   - Vectorizacion: Ejecucion paralela de instrucciones
   - Prefetching instructions: Mejor rendimiento de cache
5. El CPU reordena la ejecucion para mejor utilizacion del hardware

### Soluciones
Establecer un orden de ejecucion a traves de los siguientes metodos
1. *Synchronization* de metodos que modifican variables compartidas
2. Declaracion de variables compartidas como volatiles (*volatile*)  
   La palabra volatile permite que, todo el codigo anterior al acceso a una variable volatil se ejecute antes,
   asi como todo el codigo que se encontraba despues de este acceso, se ejecutara despues. De esta forma
   podemos asegurar el orden de ejecucion del acceso a variables volatiles
   
## Locking strategies & Deadlocks
- Coarse grained: Un lock que bloquea a toda una clase y sus miembros
   - Es mas sencillo de implementar, solo hay un lock
   - Puede ser contraproducente, puede que varias tareas en la clase no esten relacionadas y no se puedan ejecutar en paralelo
   
   ```java
   public class SharedClass{
       private DBCOnnection dbConnection;
       private FileResource fileResource;
       ...
      public synchronized Item getItemFromDB(){
           ...
      }
      
      public synchronized String readFile(){
           ...
      }
   }
   ```

- Fine grained: Un lock para cada uno de los objetos/miembros/recursos de la clase
   - Mayor posibilidad de tener ejecucion concurrente/paralela
   - Mas complejo de mantener, un lock por recurso
   - Podemos causar un deadlock

   ```java
      public class SharedClass{
         private DBCOnnection dbConnection;
         private FileResource fileResource;
         ...
         public Item getItemFromDB(){
            synchronized (dbConnection){
                ...
            }
         }
         
         public String readFile(){
             synchronized (fileResource){
                ...
             }
         }
      }
   ```
  
Ejemplo de un posible deadlock  
- Thread 1
   1. lock(A)
   2. lock(B)
   3. deleteItem(A)
   4. addItem(B)
   5. releaseLock(B)
   6. releaseLock(A)
  

- Thread 2
   1. lock(B)
   2. lock(A)
   3. deleteItem(B)
   4. addItem(A)
   5. releaseLock(A)
   6. releaseLock(B)
   
Y supongamos que los hilos son programados de la siguiente manera:
1. Hilo 1 -> Bloquea recurso A
2. Hilo 2 -> Bloquea recurso B
3. Hilo 1 -> Intenta bloquear recurso B -> Imposible ya que el hilo 2 tiene ese recurso
4. Hilo 2 -> Intenta bloquear recurso A -> Imposible ya que el hilo 1 tiene ese recurso  
...
   
Hemos caido en un deadlock ya que ningun hilo puede avanzar, porque depende de un recurso que posee otro hilo

### Deadlock conditions
1. **Exclusion mutua**: Solo un hilo puede tener acceso exclusivo a un recurso en determinado momento
2. **Hold and wait**: Al menos un hilo mantiene un recurso y esta esperando por otro recurso
3. **Not-preemtive allocation**: Un recurso solo es liberado cuando el hilo ha terminado de utilizarlo
4. **Espera circular**: Una cadena de al menos dos hilos, cada uno mantiene un recurso y espera por otro recurso

Debemos evitar cualquiera de estas condiciones, para evitar un deadlock

## Deadlock solution
1. **Evitar colas circulares**: Mantener un orden estricto en como se adquieren los locks  
   Del ejemplo anterior: 
   
      - Thread 1
         1. **lock(A)**
         2. **lock(B)**
         3. deleteItem(A)
         4. addItem(B)
         5. releaseLock(B)
         6. releaseLock(A)
   
      - Thread 2
         1. **lock(A)**
         2. **lock(B)**
         3. deleteItem(B)
         4. addItem(A)
         5. releaseLock(B)
         6. releaseLock(A)
2. 
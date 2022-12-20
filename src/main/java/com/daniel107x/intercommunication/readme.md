# Semaphores
- Un semaforo es una autoridad que puede restringir el numero de usuarios a un recurso en particular o un
grupo de recursos, a diferencia de los **locks**, que solo permiten un usuario por recurso.
- El semaforo puede restringir cualquier numero de usuarios a un recurso

```java
import java.util.concurrent.Semaphore;

public class SomeClass {
    private static final int NUMBER_OF_PERMITS = 10;
    
    public void semaphoreMethod() {
        Semaphore semaphore = new Semaphore(NUMBER_OF_PERMITS);
        /**
         * Si el semanforo no tiene mas autorizaciones para proporcionar, el hilo se bloqueara hasta que algun
         * espacio sea liberado por otro hilo
         */
        // Se puede adquirir un espacio
        semaphore.acquire(); // Number of permits decreased in 1
        // Tambien se puede adquirir mas de un espacio a la vez
        semaphore.acquire(5);
        useResource();
        // Se libera un espacio
        semaphore.release();
        // O se libera mas de un espacio
        semaphore.release(5);
    }
}
```

Podemos pensar en los locks como un semaforo que solo puede proporcionar una autorizacion, sin embargo, hay algunas
diferencias:

1. Un semaforo no mantiene la nocion de un owner thread  
2. Varios hilos pueden obtener una autorizacion
3. Un hilo puede obtener varias autorizaciones
4. El semaforo binario (Inicializado con 1) no es reentrante

```java
import java.util.concurrent.Semaphore;

class someClass {
    Semaphore semaphore = new Semaphore(1);
    void semaphoreDeadlock() {
        semaphore.acquire();
        semaphore.acquire(); // EL hilo se bloqueara hasta que se libere el semaforo anterior
    }
}
```

5. Un semaforo puede se rliberado por cualquier hilo, incluso si el hilo no lo creo. Esto implica que a
diferencia de los Lock, si otro hilo libera un espacio en el semaforo, puede haber dos hilos a la vez accediendo a un recurso compartido
   
## Producer consumer

```java
import java.util.concurrent.Semaphore;

class ProducerConsumerExample {
    Semaphore full = new Semaphore(0);
    Semaphore empty = new Semaphore(1);
    Item item = null;

    void consumer() {
        while (true) {
            full.acquire();
            consume(item);
            empty.release();
        }
    }
    
    void producer(){
        while(true){
            empty.acquire();
            produce(item);
            full.release();
        }
    }
}
```

La ejecucion sera la siguiente:
1. Al incio, el consumidor no podra adquirir el semaforo de *full* porque la cuenta inicial esta en 0, por lo que se bloquea
2. El producer adquiere el semaforo *empty*, con lo cual procede a producir un item y a liberar un espacio en el semaforo *full*
3. EN el siguiente ciclo, tratara de adquirir un espacio en el semafoto *empty*, pero no podra porque ya se adquirio previamente, entonces
suspendera el hilo
4. El consumer ahora puede adquirir el espacio en el semaforo *full*, asi que procede a consumir un item y a liberar el semaforo *empty*, 
con lo cual el producer puede producir un nuevo item y repetir el ciclo
   
### Implementacion de una queue

```java
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

class QueueSemaphore {
    private static final int CAPACITY = 10;
    Semaphore full = new Semaphore(0);
    Semaphore empty = new Semaphore(CAPACITY);
    Queue queue = new ArrayDeque();
    Lock lock = new ReentrantLock();
    
    void producer(){
        Item item = produce();
        empty.acquire();
        lock.lock();
        queue.offer(item);
        lock.release();
        full.release();
    }
    
    void consumer(){
        full.acquire();
        lock.lock();
        Item item = queue.poll();
        lock.unlock();
        empty.release();
    }
}
```

## Condition variables
El semaforo es una especie de comunicacion de una condicion entre un hilo y otro.
- Al llamar al metodo acquire en un semaforo, es equivalente a verificar la condicion "hay espacios > 0?"
- Si la condicion no se cumple, entonces el hilo A se suspende hasta que otro hilo cambie el estado del semaforo
- Cuando el hilo B llama al metodo release, el hilo A se despierta
- Entonces el hilo A verifica "hay espacios > 0?", y si si, continua a la siguiente instruccion

Una variable condicionantes es una especie de comunicacion entre hilos, ejemplo:
1. El hilo A puede verificar una condicion, y si no se cumple, suspenderse
2. El hilo B cambia el estado de la condicion y despierta al hilo A
3. El hilo A verifica la condicion de nuevo, si esta vez se cumple, salta a la siguiente linea
de ejecucion, si no, puede elegir volverse a suspender
   
Una variable de condicion siempre esta asociada a un lock, este lock asegura atomicidad en la verificacion y modificacion de las
variables relacionadas a la condicion

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class SomeClass {
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    String username = null, password = null;
    
    void auth() {
        lock.lock();
        try{
            while(username == null && password == null) condition.await(); // Libera el lock y suspende el hilo, cuando despierta trata de adquirir el lock de nuevo
        }finally{
            lock.unlock();
        }
        doStuff();
    }
    
    void ui(){
        try{
            username = userTextBox.getText();
            password = passwordTextBox.getText();
            condition.signal();
        }finally{
            lock.unlock();
        }
    }
}
```

1. El metodo **signal()** despierta a **UN SOLO HILO** esperando la variable de condicion
2. Un hilo que se despierta tiene que recuperar el lock asociado a la variable de condicion
3. Si ningun hilo esta esperando la variable de condicion, el metodo **signal()** no hace nada
4. El metodo **signalAll()** envia la senal a todos los hilos que estan esperando a la variable de condicion, y dicho hilo no necesita saber nada
acerca de dichos hilos
   
#Additional signaling methods
La clase Object de java proporciona los siguientes metodos:
- ```public final void wait() throws InterruptedException```
- ```public final void notify()```
- ```public final void notifyAll()```
Dado que cada clase en Java hereda de la clase Objeto, cualquier objeto puede ser utilizado como variable de condicion y lock 
haciendo uso de la palabra reservada **synchronized**
  
##wait()
El metodo wait ocasiona que el hilo actual se suspenda hasta que otro hilo lo despierte.
-En el estado **wait**, no consume CPU
Formas de despertar un hilo:
1. **notify()**: Despierta un **unico** hilo esperando a dicho objeto
2. **notifyAll()**: Despierta a **todos** los hilos esperando a un objeto

Para llamar a los metodos **wait()**, **notify()** y **notifyAll()** necesitamos adquirir el monitor de dicho
objeto usando la palabra synchronized

```java
public class MySharedClass{
    private boolean isComplete = false;
    
    public void waitUntilComplete(){
        synchronized (this){
            while (this.isComplete == false){
                this.wait();
            }
        }
    }
    
    public void complete(){
        synchronized (this){
            isComplete = true;
            this.notify();
        }
    }
}
```
| Object Signaling | Condition Variable |
|------------------|--------------------|
|synchronized(object)|lock.lock()|
|}|lock.unlock()|
|object.await()|condition.await()|
|object.notify()|condition.signal()|
|object.notifyAll()|condition.signalAll()|

#Matrix multiplication Pipeline
- Producer thread: Leera matrices de archivos y publicara a una Thread Safe Queue
- Consumer thread: Leera datos de la cola y los multiplicara y almacenara en un archivo

##Backpressure
Matodo implementado en la queue para evitar que el numero de datos crezca de manera infinita, manteniendo
un tamano maximo en la queue y sincronizando la cola y los hilos para que sepan cuando
pueden consumir/producir mas datos
>Cuando queramos usar una queue para desacoplar componentes multihilo, hay que aplicar back-pressure y limitar
> el tamano de la queue
# Reentrant locks
El lock reentrante funciona de manera similar a usar la palabra reservada *synchronized*, con la diferencia de que requiere un bloqueo y desbloqueo explicitos.

**Synchronized:**
```java
Object lockObject = new Object();
Resource resource = new Resource();
public void someMethod(){
    synchronized(lockObject){
        resource.use();
    }
}
```

**Reentrant lock:**
```java
Lock lockObject = new ReentrantLock();
Resource resource = new Resource();
public void someMethod(){
        lockObject.lock();
        resource.use();
        lockObject.unlock(); // Podemos olvidar desbloquear el objeto, o que una excepcion se arroje antes de desbloquear el objeto
}
```

En este caso usamos el objeto **ReentrantLock** que implementa la interfaz Lock, en lugar de usar un objeto generico.
El patron a seguir cuando se usan locks reentrantes es:

```java
import java.util.concurrent.locks.ReentrantLock;

public class SomeClass {
    ReentrantLock lockObject = new ReentrantLock();
    Resource resource = new Resource();
    
    public void someMethod(){
        lockObject.lock();
        try{
            use(resource);
            return value;
        }finally{ // Permite ejecutar algo despues de retornar una respuesta
            lockObject.unlock();
        }
    }
}
```
Este lock provee de mayor control y mayor numero de operaciones.
1. **getQueuedThreads()**: Lista de hilos en espera de adquirir el lock
2. **getOwner()**: Hilo que tiene el lock
3. **isHeldByCurrentThread()**: Retorna si el hilo actual tiene el lock o no
4. **isLocked()**: Retorna si el objeto esta bloqueado o no

Por defecto el ReentrantLock no garantiza justicia a la hora de la distribucion de un lock. Que quiere decir esto?  
Que puede que el lock sea asignado al mismo hilo bloqueando y desbloqueando el recurso, en lugar de distribuirlo 
uniformemente entre todos los hilos que estan tratando de adquirir el lock. 
Para evitar esto, es necesario pasar un argumento en el constructor de este objeto

```java
import java.util.concurrent.locks.ReentrantLock;

public class SomeClass {
    ReentrantLock lock = new ReentrantLock(true);
}
```
Hay que tomar en cuenta que esto puede disminuir el thoughput del lock

## Interruptibilidad
SUpongamos que tenemos un hilo que esta tratando de adquirir un lock. Sin embargo, este lock tarda en liberarse y por lo tanto el hilo se suspende hasta que el
lock se libere. Si quisieramos terminar este hilo seria imposible ya que interrumpir el hilo no haria nada ya que este esta suspendido
debido al lock

```java
import java.util.concurrent.locks.ReentrantLock;

public class SomeClass extends Thread{
    ReentrantLock lock = new ReentrantLock();
    Resource resource = new Resource();
    
    @Override
    public void run(){
        while(true){
            lock.lock(); //Try to achieve lock or suspend
            // ...
            if(Thread.currentThread().isInterrupted()){
                cleanUpAndExit();
            }
        }
    }
}
```

Para solucionar esto, podemos habilitar la interruptibilidad a la hora de crear un lock:

```java
import java.util.concurrent.locks.ReentrantLock;

public class SomeClass extends Thread {
    ReentrantLock lock = new ReentrantLock();
    Resource resource = new Resource();

    @Override
    public void run() {
        while (true) {
            try {
                lock.lockInterruptibly(); //Try to achieve lock or suspend with posibility to interrupt
                // ...
            }catch(InterruptedException e){
                cleanUpAndExit();
            }
        }
    }
}
```

Casos de uso:
- Watchdog para la deteccion de deadlocks y recuperacion
- Despertar hilos para realizar limpieza y cerrar una aplicacion

## TryLock
Haciendo uso de este mecanismo, en caso de que un lock no se pueda adquirir, el hilo no se bloquea, y podemos realizar
otras operaciones si el lock no se pudo adquirir

**Sin try lock**:
```java
import java.util.concurrent.locks.ReentrantLock;

public class SomeClass{
    ReentrantLock lock = new ReentrantLock();
    Resource resource = new Resource();
    
    public void someMethod(){
        try {
            lock.lock(); // Si el lock no se puede adquirir, espera hasta que este disponible
            resource.use();
            // ...
        }finally{
            lock.unlock();
        }
        
    }
}
```

**Usando tryLock**:
```java
import java.util.concurrent.locks.ReentrantLock;

public class SomeClass{
    ReentrantLock lock = new ReentrantLock();
    Resource resource = new Resource();
    
    public void someMethod(){
        if(lock.tryLock()){
            try {
                resource.use();
            }finally{
                lock.unlock();
            }
        }else{
            // ... Do something if unable to achieve the lock
        }
    }
}
```

Casos de uso:
-Aplicaciones de tiempo real donde suspender un hilo es inaceptable
    -Video/Image processing
    -High speed low latency trading systems
    -User interface applications
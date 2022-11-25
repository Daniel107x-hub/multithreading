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

## Soluciones
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
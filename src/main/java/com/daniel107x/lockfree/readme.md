# Lock free techniques
## Problemas y limitaciones
- Deadlocks: 
    - Generalmente son irreparables
    - Pueden detener completamente una aplicacion
    
- Seccion critica lenta:
    - Multiples hilos pueden usar el mismo lock
    - Un hilo mantiene el lock por mucho tiempo
    - Dicho hilo alentara a otros hilos
    - Todos los hilos son tan lentos como el hilo mas lento
    
- Inversion de prioridad
    - Dos hilos compartiendo el mismo lock
        - Un hilo con baja prioridad (Document saver)
        - Un hilo con alta prioridad (UI)
    - Si el hilo de baja prioridad ha sido programado y adquiere el lock, el hilo de alta prioridad podria no
    ser capaz de obtener el lock
    - El hilo de alta priodidad puede que no continue ya que el hilo de baja prioridad no ha sido programado para
    liberar el lock
      
- Kill tolerance (Un hilo no liberando el lock)
    - Un hilo muere, se interrumpe, o se olvida de liberar el lock
    - Todos los hilos esperaran por un deadlock
    - Se necesita escribir codigo mas complejo para manejar esta situacion
    
- Rendimiento
    - Baja en el rendimiento debido a contenciones por un lock
        - El hilo A adquiere el lock
        - El hilo B intenta adquirirlo pero es bloqueado
        - El hilo B es programado para finalizar (Scheduled out) (Context switch)
        - El hilo B es programado de nuevo (Context switch)
    - El consumo adicional puede no ser notable para la mayoria de las aplicaciones
    - Para aplicaciones sensibles  ala latencia, esto puede ser importante
    

### Por que necesitabamos locks?
- Multiples hilos accediendo a recursos compartidos
- Al menos un hilo esta modificando los recursos compartidos
- Falta de operaciones atomicas
    - Una sola operacion puede ser una o mas operaciones de hardware
    ```java
    count++;
    // Implica 3 operaciones:
    // Leer count
    // Calcular el nuevo valor
    // Almacenar el valor de vuelta
    ```
    
### Operaciones atomicas
1. Lectura y asignacion de tipos primitivos
2. Lectura y asignacion de referencias
3. Lectura y asignacion de long y double volatiles

Para evitar condiciones de carrera podemos leer/escribir en tipos de datos volatiles, primitivos o referencias

## AtomicX classes
Clases ubicadas en el paquete **java.util.concurrent.atomic**
- Interiormente utiliza la clase **Unsafe** que provee acceso a metodos nativos de bajo nivel
- Utiliza una implementacion especifica a la plataforma de operaciones atomicas
  
1. AtomicBoolean
2. AtomicInteger
3. AtomicIntegerArray
4. AtomicIntegerFieldUpdater<T>
5. AtomicLong
6. AtomicLongArray
7. AtomicLongFieldUpdater<T>
8. AtomicMarkableReference<V>
9. AtomicReference<V>
10. AtomicReferenceArray<E>
11. AtomicReferenceFieldUpdater<T,V>
12. AtomicStampedReference<V>
13. DoubleAccumulator
14. DoubleAdder
15. LongAccumulator
16. LongAdder
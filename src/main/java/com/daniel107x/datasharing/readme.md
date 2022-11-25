# Data sharing between threads
Each thread has its own stack memory, while the heap, files, code, etc. is shared between all threads

## Objects stored on the heap
1. Objects
2. Class members
3. Static variables

## Objects stored on the stack
1. Local primitive types
2. Local references

Los recursos son algo que representa datos o estados
- Variables
- Estructuras de datos
- Archivos o manejadores de conexiones
- Colas de mensajes o trabajo
- Cualquier objeto

Recordemos que todo almacenado en el heap sera compartido por los hilos.

Casos de uso donde podriamos necesitar compartir recursos
1. Text editor
    - UI Thread
    - Save thread
2. Cola de trabajo  
   Un dispatcher se encarga de insertar trabajos en la cola de trabajo mientras que una serie de worker threads se encargaran
   de procesar las solicitudes. La cola de trabajo es compartida y debe de estar en el heap.
3. Database microservice  
    Cada request a un servicio abstraccion de una base de datos puede ser procesado por un hilo
   
##Cual es el problema de compartir recursos?
La modificacion de recursos compartidos sin control genera condiciones de carrera

### Operaciones atomicas
Operaciones o conjunto de operaciones que parecen al sistema como si hubieran ocurrido a la vez, son indivisibles
y se ejecutan completamente, o no se ejecutan. No hay estados intermedios y no hay forma de interrumpirlas en medio.

```java
var++; 

/*
    No es una operacion atomicas ya que se compone de:
    1. Obtener el valor actual de var
    2. Crear una nueva avriable que almacene el valor de var + 1
    3. Actualizar el valor de var a var + 1
 */
```
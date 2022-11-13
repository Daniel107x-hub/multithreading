# Thread coordination
>Una vez iniciado un hilo, este se ejecuta independientemente  

>El orden de ejecucion de los hilos esta fuera de nuestro control

## Termination
Recordemos que los hilos consumen recursos de memoria y kernel, asi como tiempo y espacio en las caches.
1. Usualmente deseamos terminar un hilo cuando este ya finalizo su trabajo pero la aplicacion se sigue ejecutando. 
2. Tambien podemos querer terminarlo, si el hilo esta teniendo un comportamiento incorrecto.
3. Por defecto las aplicaciones no se terminaran si hay al menos un hilo en ejecucion

Cada hilo tiene un metodo **#interrupt()** que permite enviar una senal de interrupcion desde un hilo hacia
otro.
Los escenarios donde podriamos necesitar utilizarlo son:
1. El hilo esta ejecutando un metodo que arroja una **InterruptedException**
2. El hilo que intentamos interrumpir maneja la senal d einterrupcion explicitamente

## Daemon threads
Son hilos que se ejecutan en segundo plano y no evitan que la aplicacion se termine si el hilo principal es finalizado

Escenarios donde deseamos ejecutar tareas como hilos daemon:
1. Tareas en segundo plano que no deben de bloquear la finalizacion de nuestra aplicacion i.e. File saving thread en un editor de texto
2. El codigo en un worker thread no esta en nuestro control y no queremos que bloquee la finalizacion de nuestra aplicacion i.e. Worker thread que usa una libreria externa

## Que sucede si un hilo depende de otro? - Join method
Una forma de programar este comportamiento seria que periodicamente el hilo B verifique si el hilo A
ya tiene el resultado disponible. Sin embargo, esto seria extremadamente ineficiente ya que el hilo B consumiria
cliclos de reloj y alentaria el proceso del hilo A.

En realidad lo que deseamos es que el hilo B se ponga en estado **sleep** mientras el hilo A hace su trabajo y una vez que
este termine, el hilo B despierte y tome el resultado del hilo A.

Esto se logra haciendo uso del metodo ***join()***


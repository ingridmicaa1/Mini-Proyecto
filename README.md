# Mini Proyecto: Higher Order Functions en Criptografía

## Estructura y Funcionamiento

1. **Validación Funcional:**
   * `validarMensaje(mensaje: String): Option[String]`: Evita excepciones `NullPointerException` encapsulando entradas nulas con `Option(mensaje)` y filtrando cadenas vacías.
   * `validarClave(clave: Int): Either[String, Int]`: Valida que la clave numérica esté dentro de un rango seguro (`-255` a `255`), retornando `Left` con el mensaje descriptivo en caso de error o `Right` con la clave válida.

2. **Cifrado:**
   * **Cifrado César:** Desplaza letras mayúsculas del alfabeto inglés (`A-Z`, 26 caracteres) aplicando aritmética modular:
     $$E(x, k) = (pos(x) + k) \bmod 26$$
   * **Cifrado XOR:** Aplica el operador lógico exclusivo a nivel de bits (`^`) entre el valor entero ASCII del carácter y la clave:
     $$E(x, k) = ASCII(x) \oplus k$$


3. **Código**


       def validarMensaje(mensaje: String): Option[String] = {
       Option(mensaje).filter(m => m.trim.nonEmpty)
       }


       def validarClave(clave: Int): Either[String, Int] = {
       if (clave < -255 || clave > 255) {
       Left("Clave inválida: debe estar entre -255 y 255")
       } else {
       Right(clave)
       }
       }

       val cifradoCesar: (Char, Int) => Char = (c: Char, k: Int) => {
       if (c >= 'A' && c <= 'Z') {
       val pos = c - 'A'

       val nuevaPos = ((pos + k) % 26 + 26) % 26
       (nuevaPos + 'A').toChar
       } else {

       throw new IllegalArgumentException(s"Carácter no soportado para César: '$c' (solo letras A-Z)")
       }
       }

       val cifradoXOR: (Char, Int) => Char = (c: Char, k: Int) => {
       if (k < 0 || k > 255) {
       throw new IllegalArgumentException(s"Clave XOR fuera de rango ASCII estándar (0-255): $k")
       } else {
       (c.toInt ^ k).toChar
       }
  

       def procesarMensaje(
                       mensaje: String,
                       clave: Int,
                       funcionCifrado: (Char, Int) => Char
                     ): Try[String] = {

       val resultado: Try[String] = Try {

       val textoValido = validarMensaje(mensaje) match {
        case Some(txt) => txt
        case None => throw new IllegalArgumentException("El mensaje no puede ser nulo ni estar vacío")
       }


       val claveValida = validarClave(clave) match {
        case Right(k) => k
        case Left(error) => throw new IllegalArgumentException(error)
       }

       val textoTransformado = textoValido.map(c => funcionCifrado(c, claveValida))
       textoTransformado
       }
        resultado
       }

       def main(args: Array[String]): Unit = {

       println("--- 1. Caso Cifrado César ---")
       val msgCesar = "FUNCIONAL"
       val claveCesar = 3

       val cifrado1 = procesarMensaje(msgCesar, claveCesar, cifradoCesar)
       cifrado1 match {
       case Success(textoCifrado) =>
        println(s"Original: $msgCesar | Clave: $claveCesar -> Cifrado:$textoCifrado")

       // Caso Cesar:
        procesarMensaje(textoCifrado, -claveCesar, cifradoCesar) match {

          case Success(textoDescifrado) =>
            println(s"Descifrado con clave -3: $textoDescifrado")

          case Failure(exception) =>
            println(s"Error al descifrar: ${exception.getMessage}")
        }
       case Failure(exception) =>
        println(s"Error en cifrado César: ${exception.getMessage}")
       }

       // Caso XOR:
       println("\n--- 2. Caso Cifrado XOR ---")
       val msgXOR = "scala"
       val claveXOR = 5


       val cifrado2 = procesarMensaje(msgXOR, claveXOR, (c, k) => (c.toInt ^ k).toChar)
       cifrado2 match {
       case Success(textoCifrado) =>
        println(s"Original: $msgXOR | Clave: $claveXOR -> Cifrado:$textoCifrado")


        procesarMensaje(textoCifrado, claveXOR, cifradoXOR) match {
          case Success(textoRecuperado) =>
            println(s"Recuperado aplicando XOR nuevamente con k=$claveXOR: $textoRecuperado")
          case Failure(exception) =>
            println(s"Error al recuperar XOR: ${exception.getMessage}")
        }
       case Failure(exception) =>
        println(s"Error en cifrado XOR: ${exception.getMessage}")
        }

       // Errores
        println("\n--- 3. Pruebas de Manejo de Errores ---")

       // Cadena vacía
       val pruebaVacia = procesarMensaje("", 3, cifradoCesar)
       pruebaVacia match {
       case Success(resultado) => println(s"Resultado: $resultado")
       case Failure(exception) => println(s"[Prueba Cadena Vacía] Capturado -> ${exception.getMessage}")
       }

       // Cadena nula null

       val pruebaNula = procesarMensaje(null, 3, cifradoCesar)
       pruebaNula match {
       case Success(resultado) => println(s"Resultado: $resultado")
       case Failure(exception) => println(s"[Prueba Null] Capturado -> ${exception.getMessage}")
       }

       //  Clave muy grande
       val pruebaClaveGrande = procesarMensaje("HOLA", 500, cifradoCesar)
       pruebaClaveGrande match {
       case Success(respuesta) => println(s"Resultado: $respuesta")
       case Failure(exception) => println(s"[Prueba Clave Inválida] Capturado -> ${exception.getMessage}")
       }

       //  Caracteres numeros y letras
       val pruebaCaracterInvalido = procesarMensaje("Scala123", 3, cifradoCesar)
       pruebaCaracterInvalido match {
       case Success(resultado) => println(s"Resultado: $resultado")
       case Failure(exception) => println(s"Prueba Carácter No Soportado -> ${exception.getMessage}")
       }
       }


## Resultados de Ejecución

Al ejecutar `Criptografia`, se obtienen los siguientes resultados:

<img width="1845" height="1078" alt="image" src="https://github.com/user-attachments/assets/f199312c-395b-4256-a60f-aac7dbb47b16" />


--- 3.1. Pruebas de Manejo de Errores ---

[Prueba Cadena Vacía] Capturado -> El mensaje no puede ser nulo ni estar vacío
[Prueba Null] Capturado -> El mensaje no puede ser nulo ni estar vacío
[Prueba Clave Inválida] Capturado -> Clave inválida: debe estar entre -255 y 255
[Prueba Carácter No Soportado] Capturado -> Carácter no soportado para César: 'c' (solo letras A-Z)

## Preguntas Para Reflexionar
1. **¿Por qué el método XOR es su propia función inversa utilizando el mismo operador ^, mientras que el Cesar requiere invertir el signo de la clave?**

Porque el operador XOR (`^`) trabaja comparando bit por bit (0 y 1): si un bit cambia al aplicar la clave una vez, al volver a aplicar exactamente la misma clave se vuelve a invertir y regresa a su valor original (como encender y apagar un mismo interruptor). En cambio, el Cifrado César suma posiciones hacia adelante en el alfabeto; para volver a la letra original hay que restar esa misma cantidad de posiciones usando `-k`.

2. **¿Que ventajas ofrece el uso de ´ map sobre un bucle for tradicional en este contexto?**

**Evita variables mutables (`var`):** No hace falta crear una variable acumuladora vacía ni modificar estados paso a paso; `map` devuelve directamente un nuevo `String` transformado.

**Mayor claridad:** Permite escribir en una sola línea qué transformación se aplica a cada carácter sin preocuparse por índices o contadores.

**Integración funcional:** Se combina de forma limpia con `Try`, `Option` y funciones anónimas sin efectos colaterales.

4. **¿Cómo ayuda el tipado estático a prevenir errores al pasar funciones como argumentos?**

El tipado estático obliga a que cualquier función enviada a `procesarMensaje` cumpla exactamente con la firma `(Char, Int) => Char`. Si se intenta pasar una función con parámetros o retorno distintos, Scala detecta el error antes de compilar, evitando fallos inesperados durante la ejecución del programa.



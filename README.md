# Mini Proyecto: Higher Order Functions en Criptografía

## Descripción del Proyecto
Este proyecto implementa un motor de cifrado genérico en Scala aplicando los principios de la **Programación Funcional**: funciones de orden superior (*Higher-Order Functions*), funciones anónimas (lambdas), transformación sobre colecciones inmutables mediante `map`, y manejo seguro de errores y efectos colaterales utilizando `Try`, `Success`, `Failure`, `Option` y `Either`.

El motor permite desacoplar la lógica de recorrido y validación del mensaje respecto al algoritmo de transformación de cada carácter, recibiendo la estrategia de cifrado como parámetro `(Char, Int) => Char`.

---

## Estructura y Funcionamiento

1. **Validación Funcional:**
   * `validarMensaje(mensaje: String): Option[String]`: Evita excepciones `NullPointerException` encapsulando entradas nulas con `Option(mensaje)` y filtrando cadenas vacías.
   * `validarClave(clave: Int): Either[String, Int]`: Valida que la clave numérica esté dentro de un rango seguro (`-255` a `255`), retornando `Left` con el mensaje descriptivo en caso de error o `Right` con la clave válida.

2. **Estrategias de Cifrado:**
   * **Cifrado César:** Desplaza letras mayúsculas del alfabeto inglés (`A-Z`, 26 caracteres) aplicando aritmética modular:
     $$E(x, k) = (pos(x) + k) \bmod 26$$
   * **Cifrado XOR:** Aplica el operador lógico exclusivo a nivel de bits (`^`) entre el valor entero ASCII del carácter y la clave:
     $$E(x, k) = ASCII(x) \oplus k$$

3. **Motor Principal (`procesarMensaje`):**
   * Recibe `(mensaje: String, clave: Int, funcionCifrado: (Char, Int) => Char)` y devuelve un `Try[String]`.
   * Utiliza `.map(...)` para aplicar la función de transformación carácter por carácter sin mutar el estado original.

---

## Resultados de Ejecución

Al ejecutar `Criptografia.main`, se obtienen los siguientes resultados:

```text
=== MINI PROYECTO: HIGHER ORDER FUNCTIONS EN CRIPTOGRAFÍA ===

--- 1. Caso Cifrado César ---
Original: FUNCIONAL | Clave: 3 -> Cifrado: IXQFLRQDO
Descifrado con clave -3: FUNCIONAL

--- 2. Caso Cifrado XOR ---
Original: scala | Clave: 5 -> Cifrado: vfdid
Recuperado aplicando XOR nuevamente con k=5: scala

--- 3. Pruebas de Manejo de Errores ---
[Prueba Cadena Vacía] Capturado -> El mensaje no puede ser nulo ni estar vacío
[Prueba Null] Capturado -> El mensaje no puede ser nulo ni estar vacío
[Prueba Clave Inválida] Capturado -> Clave inválida: debe estar entre -255 y 255
[Prueba Carácter No Soportado] Capturado -> Carácter no soportado para César: 'c' (solo letras A-Z)

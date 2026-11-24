# Mini-Proyecto
# Mini Proyecto: Higher Order Functions en Criptografía

**Asignatura:** Programación Funcional y Reactiva
**Lenguaje:** Scala 3
**Autora:** Ingrid [Apellido]
**Docente:** [Nombre del docente]

---

## 1. Objetivo

Crear un motor de cifrado que reciba **una función como parámetro** (función de orden superior) y usarlo con dos métodos: **César** y **XOR**. Los errores se manejan con `Try`, `Success` y `Failure`.

---

## 2. Conceptos previos

### Option: hay un valor o no hay

```scala
def dividir(a: Int, b: Int): Option[Int] =
  if (b == 0) None else Some(a / b)

dividir(6, 2)               // Some(3)
dividir(6, 0)               // None
dividir(6, 0).getOrElse(0)  // 0
```

```scala
def getText(msg: String): Option[String] =
  Option(msg).map(_.toUpperCase)          // Option(null) es None

getText(null).map(_.length).getOrElse(0)  // 0, sin excepción
```

### Either: éxito (Right) o error con mensaje (Left)

```scala
def validarEdad(edad: Int): Either[String, Int] =
  if (edad < 0) Left("Edad inválida")
  else if (edad < 18) Left("Menor de edad")
  else Right(edad)

validarEdad(-5)  // Left(Edad inválida)
validarEdad(20)  // Right(20)
```

### Try: éxito (Success) o excepción capturada (Failure)

`Try` se usa cuando el código **puede lanzar una excepción**. Si la lanza, `Try` la captura y no se cae el programa.

```scala
import scala.util.{Try, Success, Failure}

def toInt(s: String): Try[Int] = Try(Integer.parseInt(s))

toInt("1")    // Success(1)
toInt("Uno")  // Failure(java.lang.NumberFormatException: For input string: "Uno")
```

Se usa con `match case`, igual que `Some/None` y `Right/Left`:

```scala
toInt("Uno") match
  case Success(n)     => println("Número: " + n)
  case Failure(error) => println("Error: " + error.getMessage)
```

También tiene `map` y `flatMap`. Si hay un `Failure`, se propaga sin ejecutar lo siguiente:

```scala
toInt("10").map(n => n * 2)    // Success(20)
toInt("abc").map(n => n * 2)   // Failure(...)
```

| Estructura | Casos | Se usa para |
|------------|-------|-------------|
| `Option` | `Some` / `None` | Puede no haber valor |
| `Either` | `Right` / `Left` | Error con mensaje |
| `Try` | `Success` / `Failure` | Código que lanza excepciones |

---

## 3. Fórmulas

### Cifrado César

$$E(x, k) = (\text{pos}(x) + k) \bmod 26$$

- $x$: carácter, $\text{pos}(x)$: posición en el alfabeto (A = 0, ..., Z = 25), $k$: clave.
- Para descifrar se usa la clave opuesta: $-k$.
- En Scala, `%` puede dar negativos, por eso se normaliza: `(((pos + k) % 26) + 26) % 26`.

Ejemplo `"AD"` con $k = 1$: A (0) → 1 → **B**, D (3) → 4 → **E**, resultado `"BE"`.

### Cifrado XOR

$$E(x, k) = \text{ASCII}(x) \oplus k$$

Se usa el operador `^`. Aplicarlo dos veces con la misma clave devuelve el original:

$$(x \oplus k) \oplus k = x$$

Ejemplo `'s'` con $k = 5$: `0111 0011` ⊕ `0000 0101` = `0111 0110` = 118 = **'v'**.

---

## 4. Código

Archivo completo: [`CriptografiaHOF.scala`](./CriptografiaHOF.scala)

### Validaciones

```scala
def validarMensaje(mensaje: String): Try[String] =
  if (mensaje == null) Failure(new Exception("El mensaje es nulo"))
  else if (mensaje.isEmpty) Failure(new Exception("El mensaje esta vacio"))
  else Success(mensaje)

def validarClave(clave: Int): Try[Int] =
  if (clave < -1000 || clave > 1000) Failure(new Exception("Clave invalida: " + clave))
  else Success(clave)
```

### Función de orden superior

```scala
def procesarMensaje(mensaje: String, clave: Int, estrategia: (Char, Int) => Char): Try[String] =
  validarMensaje(mensaje).flatMap(m =>
    validarClave(clave).flatMap(k =>
      Try(m.map(c => estrategia(c, k)))   // map: aplica la estrategia a cada caracter
    )
  )
```

La **estrategia** (`(Char, Int) => Char`) llega como parámetro y `map` la aplica a cada carácter. Si algo falla (mensaje, clave o la propia estrategia), el resultado es un `Failure`.

### Estrategia César

```scala
def cifradoCesar(c: Char, k: Int): Char =
  if (c >= 'A' && c <= 'Z') (((c - 'A' + k) % 26 + 26) % 26 + 'A').toChar
  else if (c >= 'a' && c <= 'z') (((c - 'a' + k) % 26 + 26) % 26 + 'a').toChar
  else throw new Exception("Caracter no soportado: '" + c + "'")
```

### Estrategia XOR

```scala
def cifradoXor(c: Char, k: Int): Char =
  if (k < 0 || k > 255) throw new Exception("Clave XOR invalida: " + k)
  else (c.toInt ^ k).toChar
```

### Mostrar resultados

```scala
def mostrar(resultado: Try[String]): Unit =
  resultado match
    case Success(texto) => println("Exito: " + texto)
    case Failure(error) => println("Error: " + error.getMessage)
```

---

## 5. Pruebas y resultados

### Caso 1: César

```scala
val cifrado = procesarMensaje("FUNCIONAL", 3, cifradoCesar)
mostrar(cifrado)
val descifrado = cifrado.flatMap(t => procesarMensaje(t, -3, cifradoCesar))
mostrar(descifrado)
```

```
Exito: IXQFLRQDO
Exito: FUNCIONAL
```

### Caso 2: XOR

La lambda se pasa directamente y usa `^`:

```scala
val xor1 = procesarMensaje("scala", 5, (c, k) => (c.toInt ^ k).toChar)
mostrar(xor1)
val xor2 = xor1.flatMap(t => procesarMensaje(t, 5, (c, k) => (c.toInt ^ k).toChar))
mostrar(xor2)
```

```
Exito: vfdid
Exito: scala
```

| Carácter | ASCII | XOR 5 | Resultado | Cifrado |
|:--------:|:-----:|:-----:|:---------:|:-------:|
| s | 115 | $115 \oplus 5$ | 118 | v |
| c | 99 | $99 \oplus 5$ | 102 | f |
| a | 97 | $97 \oplus 5$ | 100 | d |
| l | 108 | $108 \oplus 5$ | 105 | i |
| a | 97 | $97 \oplus 5$ | 100 | d |

### Caso 3: Manejo de errores

```scala
mostrar(procesarMensaje("", 3, cifradoCesar))                // cadena vacia
mostrar(procesarMensaje(null, 3, cifradoCesar))              // cadena nula
mostrar(procesarMensaje("HOLA", 100000, cifradoCesar))       // clave muy grande
mostrar(procesarMensaje("HOLA MUNDO 123!", 3, cifradoCesar)) // caracteres especiales
mostrar(procesarMensaje("scala", -5, cifradoXor))            // clave XOR invalida
```

```
Error: El mensaje esta vacio
Error: El mensaje es nulo
Error: Clave invalida: 100000
Error: Caracter no soportado: ' '
Error: Clave XOR invalida: -5
```

El programa no se cae en ningún caso: cada error queda capturado como `Failure`.

---

## 6. Preguntas para reflexionar

**¿Por qué XOR es su propia inversa con el mismo `^`, mientras que César requiere invertir el signo de la clave?**
Porque $(x \oplus k) \oplus k = x$: un bit XOR consigo mismo da 0 y $x \oplus 0 = x$. César es una suma módulo 26, y para deshacer una suma hay que restar, es decir, sumar $-k$.

**¿Qué ventajas ofrece `map` sobre un bucle `for` tradicional?**
No necesita variables mutables ni contadores, devuelve un `String` nuevo sin modificar el original y permite cambiar el algoritmo pasando otra función.

**¿Cómo ayuda el tipado estático al pasar funciones como argumentos?**
El tipo `(Char, Int) => Char` obliga a que toda estrategia reciba un `Char` y un `Int` y devuelva un `Char`. Si la firma no coincide, el compilador avisa antes de ejecutar.

---

## 7. Cómo ejecutar

```bash
scala-cli run CriptografiaHOF.scala
```

o con el compilador de Scala 3:

```bash
scalac CriptografiaHOF.scala
scala main
```

---

## 8. Conclusiones

- Con funciones de orden superior, `procesarMensaje` no cambia aunque cambie el algoritmo: solo se le pasa otra estrategia.
- `map` transforma el mensaje sin variables mutables ni bucles.
- `Try`, `Success` y `Failure` permiten capturar los errores y mostrarlos con `match case`.
- César y XOR son cifrados didácticos y no son seguros para proteger información real.

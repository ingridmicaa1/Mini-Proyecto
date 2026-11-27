package Mini_Proyecto
import scala.util.{Try, Success, Failure}

object Criptografia {

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


    val cifrado2 = procesarMensaje(msgXOR, claveXOR, cifradoXOR)
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
}

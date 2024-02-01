import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.Await
import scala.concurrent.duration._

// Define el modelo de datos
case class Usuario(id: Option[Int], nombre: String, edad: Int)

// Define la tabla correspondiente en Slick
class Usuarios(tag: Tag) extends Table[Usuario](tag, "usuarios") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def nombre = column[String]("nombre")
  def edad = column[Int]("edad")
  def * = (id.?, nombre, edad) <> (Usuario.tupled, Usuario.unapply)
}

object ProgramaReactiva extends App {
  // Crea la conexión a la base de datos SQLite
  val db = Database.forConfig("sqlite")

  // Crea la tabla si no existe
  val usuarios = TableQuery[Usuarios]
  val setup = DBIO.seq(usuarios.schema.createIfNotExists)
  val setupFuture = db.run(setup)
  Await.result(setupFuture, 5.seconds)

  // Inserta algunos usuarios en la base de datos
  val usuariosIniciales = Seq(
    Usuario(None, "Juan", 25),
    Usuario(None, "María", 30),
    Usuario(None, "Carlos", 22),
    Usuario(None, "Ana", 25),
    Usuario(None, "Luis", 30),
    Usuario(None, "Elena", 22)
  )

  val insertAction = usuarios ++= usuariosIniciales
  val insertFuture = db.run(insertAction)
  Await.result(insertFuture, 5.seconds)

  // Operaciones reactivas en la base de datos utilizando Slick y las operaciones mencionadas
  val query = usuarios
    .filter(_.edad >= 25) // Filtra usuarios mayores de 25 años
    .distinct // Elimina duplicados
    .map(usuario => (usuario.nombre, usuario.edad)) // Realiza un mapeo a un par (nombre, edad)
    .groupBy(_._2) // Agrupa por edad
    .map { case (edad, grupo) => (edad, grupo.length) } // Mapea a un par (edad, cantidad de usuarios)
    .toList // Convierte a una lista
    .sortBy(_._1) // Ordena por edad

  val resultadoFuture = db.run(query.result)
  val resultado = Await.result(resultadoFuture, 5.seconds)

  // Imprime el resultado
  resultado.foreach { case (edad, cantidad) =>
    println(s"Usuarios mayores o iguales a $edad años: $cantidad")
  }

  // Cierra la base de datos
  db.close()
}

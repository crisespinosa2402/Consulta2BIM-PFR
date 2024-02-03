import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.Materializer
import scala.concurrent.ExecutionContext.Implicits.global


  object ejemploReactiva {
    def main(args: Array[String]): Unit = {
      // Crear el sistema de actores y el materializador
      implicit val system: ActorSystem = ActorSystem("ejemploReactiva")
      implicit val materializer: Materializer = Materializer(system)

      // Definir la fuente que emite números del 1 al 10
      val source = Source(1 to 10)

      // Transformar y filtrar el flujo
      val transformedSource = source
        .map(_ * 2)
        .filter(_ % 4 == 0)

      // Extender el flujo concatenando cada número con su triple
      val extendedSource = transformedSource
        .flatMapConcat(num => Source(List(num, num * 3)))

      // Definir un Sink que imprime cada elemento en la consola
      val imprimirSink = Sink.foreach[Int](println)

      // Ejecutar el flujo extendido con el Sink definido
      extendedSource.runWith(imprimirSink)
        // Al completar, terminar el sistema de actores
        .onComplete(_ => system.terminate())
    }
}

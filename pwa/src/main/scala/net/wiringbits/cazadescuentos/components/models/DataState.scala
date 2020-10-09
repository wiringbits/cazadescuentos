package net.wiringbits.cazadescuentos.components.models

sealed trait DataState[T] extends Product with Serializable {
  def loaded(data: T): DataState.Loaded[T] = DataState.Loaded(data)
}

object DataState {
  final case class Loading[T]() extends DataState[T]
  final case class Loaded[T](data: T) extends DataState[T]

  def loading[T]: DataState[T] = Loading[T]()
}

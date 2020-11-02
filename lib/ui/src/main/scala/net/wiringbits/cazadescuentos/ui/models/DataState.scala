package net.wiringbits.cazadescuentos.ui.models

sealed trait DataState[T] extends Product with Serializable {
  def loaded(data: T): DataState.Loaded[T] = DataState.Loaded(data)
  def failed(msg: String): DataState.Failed[T] = DataState.Failed(msg)
}

object DataState {
  final case class Loading[T]() extends DataState[T]
  final case class Loaded[T](data: T) extends DataState[T]
  final case class Failed[T](msg: String) extends DataState[T]

  def loading[T]: DataState[T] = Loading[T]()
}

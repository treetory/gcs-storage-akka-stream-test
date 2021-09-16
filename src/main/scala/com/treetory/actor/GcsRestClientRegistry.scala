package com.treetory.actor

import akka.actor.typed.Behavior
import com.google.cloud.storage.BlobInfo

final case class BlobInfoList(blobInfoList: List[BlobInfo])

object GcsRestClientRegistry {

  sealed trait Command

  final case class SignedURL(url: String)

  def registry(empty: Set[Nothing]): Behavior[Command] = ???

  def apply(): Behavior[Command] = registry(Set.empty)

}

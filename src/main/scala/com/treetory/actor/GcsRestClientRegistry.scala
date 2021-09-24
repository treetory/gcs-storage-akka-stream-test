package com.treetory.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.treetory.util.{ExcelExporterUtil, GoogleStorage, PagedList}

import java.io.File

object GcsRestClientRegistry {

  sealed trait Command

  final case class Page(token: String, replyTo: ActorRef[PagedList]) extends Command
  final case class Excel(token: String, replyTo: ActorRef[File]) extends Command

  def registry(): Behavior[Command] = {
    Behaviors.receiveMessage {
      case Page(token, replyTo) =>
        replyTo ! GoogleStorage.pagedList(token)
        Behaviors.same
      case Excel(token, replyTo) =>
        replyTo ! ExcelExporterUtil.gcsExport(token)
        Behaviors.same
    }
  }

  def apply(): Behavior[Command] = registry()

}

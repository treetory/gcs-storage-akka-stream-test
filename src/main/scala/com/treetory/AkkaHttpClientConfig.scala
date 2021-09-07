package com.treetory

import akka.actor.ActorSystem
import akka.http.scaladsl.{ConnectionContext, HttpsConnectionContext}

import java.security.cert.X509Certificate
import javax.net.ssl.{KeyManager, SSLContext, X509TrustManager}

object AkkaHttpClientConfig {

  implicit val client: ActorSystem = ActorSystem("HttpClient")

  val sslContext = SSLContext.getInstance("TLSv1.2")
  sslContext.init(Array[KeyManager](), Array(NoCheckX509TrustManager), null)

  val context: HttpsConnectionContext = ConnectionContext.httpsClient(sslContext)

}

object NoCheckX509TrustManager extends X509TrustManager {
  override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = ???

  override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = ???

  override def getAcceptedIssuers: Array[X509Certificate] = Array[X509Certificate]()
}
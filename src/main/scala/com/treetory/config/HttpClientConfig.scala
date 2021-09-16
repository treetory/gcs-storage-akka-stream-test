package com.treetory.config

import akka.actor.ActorSystem
import akka.http.scaladsl.ConnectionContext
import com.typesafe.sslconfig.akka.AkkaSSLConfig

import java.security.cert.X509Certificate
import javax.net.ssl.{KeyManager, SSLContext, X509TrustManager}

object HttpClientConfig {

  implicit val client: ActorSystem = ActorSystem("HttpClient")

  val context = ConnectionContext.https(
    {
      object NoCheckX509TrustManager extends X509TrustManager {
        override def checkClientTrusted(chain: Array[X509Certificate], authType: String) = ()

        override def checkServerTrusted(chain: Array[X509Certificate], authType: String) = ()

        override def getAcceptedIssuers = Array[X509Certificate]()
      }
      val context = SSLContext.getInstance("TLSv1.2")
      context.init(Array[KeyManager](), Array(NoCheckX509TrustManager), null)
      context
    },
    Option(AkkaSSLConfig().mapSettings(s => {
      s.withLoose(s.loose.withDisableSNI(true))
      s.withLoose(s.loose.withDisableHostnameVerification(true))
    }))
  )

}

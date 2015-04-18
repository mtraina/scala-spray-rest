package com.roblayton.spray.server

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import spray.routing.Route
import spray.http.MediaTypes

import com.roblayton.spray.Fragment
import com.roblayton.spray.MineralFragment

object Main extends App with SimpleRoutingApp {
  implicit var actorSystem = ActorSystem()

  var fragments = Fragment.fragments

  def getJson(route: Route): Route = {
    get {
      respondWithMediaType(MediaTypes.`application/json`) {
        route
      }
    }
  }

  lazy val helloRoute = get {
    path("hello") {
      complete {
        "Hello World!"
      }
    }
  }

  startServer(interface = "localhost", port = 8080) {
    helloRoute ~
    getJson {
      path("fragments") {
        respondWithMediaType(MediaTypes.`application/json`) {
          complete {
            Fragment.toJson(fragments)
          }
        }
      }
    } ~
    get {
      path("fragment" / IntNumber / "details") { index =>
        complete {
          Fragment.toJson(fragments(index))
        }
      }
    } ~
    post {
      path("fragment" / "add") {
        parameters("name"?, "kind"?, "weight".as[Double]) { (name, kind, weight) =>
          val newFragment = MineralFragment(
            name.getOrElse("mineral"),
            kind.getOrElse("Mineral"),
            weight)

          fragments = newFragment :: fragments

          complete {
            "OK"
          }
        }
      }
    }
  }
}
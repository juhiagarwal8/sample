// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/Juhi Agarwal/git/play-samples/conf/users.routes
// @DATE:Sun Jun 28 00:55:17 IST 2020

import play.api.mvc.Call


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:2
package v1.user {

  // @LINE:2
  class ReverseUserController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:2
    def getAllUsers(): Call = {
      
      Call("GET", _prefix)
    }
  
    // @LINE:5
    def getUserById(id:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("id", id)))
    }
  
    // @LINE:3
    def createUser(): Call = {
      
      Call("POST", _prefix)
    }
  
    // @LINE:6
    def updateUser(id:String): Call = {
      
      Call("PUT", _prefix + { _defaultPrefix } + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("id", id)))
    }
  
  }


}

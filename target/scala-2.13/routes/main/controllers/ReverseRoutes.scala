// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/Juhi Agarwal/git/play-samples/conf/routes
// @DATE:Sun Jun 28 00:55:17 IST 2020

import play.api.mvc.Call


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:1
package controllers {

  // @LINE:1
  class ReverseHomeController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:1
    def index(): Call = {
      
      Call("GET", _prefix)
    }
  
  }


}

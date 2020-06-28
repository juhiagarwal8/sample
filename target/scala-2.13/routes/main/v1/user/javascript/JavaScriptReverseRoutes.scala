// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/Juhi Agarwal/git/play-samples/conf/users.routes
// @DATE:Sun Jun 28 00:55:17 IST 2020

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:2
package v1.user.javascript {

  // @LINE:2
  class ReverseUserController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:2
    def getAllUsers: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "v1.user.UserController.getAllUsers",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + """"})
        }
      """
    )
  
    // @LINE:5
    def getUserById: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "v1.user.UserController.getUserById",
      """
        function(id0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("id", id0))})
        }
      """
    )
  
    // @LINE:3
    def createUser: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "v1.user.UserController.createUser",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + """"})
        }
      """
    )
  
    // @LINE:6
    def updateUser: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "v1.user.UserController.updateUser",
      """
        function(id0) {
          return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("id", id0))})
        }
      """
    )
  
  }


}

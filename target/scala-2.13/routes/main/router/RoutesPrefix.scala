// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/Juhi Agarwal/git/play-samples/conf/routes
// @DATE:Sun Jun 28 00:55:17 IST 2020


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}

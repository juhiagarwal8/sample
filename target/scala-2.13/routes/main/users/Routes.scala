// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/Juhi Agarwal/git/play-samples/conf/users.routes
// @DATE:Sun Jun 28 00:55:17 IST 2020

package users

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset
import _root_.play.libs.F

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:2
  UserController_0: v1.user.UserController,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:2
    UserController_0: v1.user.UserController
  ) = this(errorHandler, UserController_0, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    users.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, UserController_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """v1.user.UserController.getAllUsers(request:Request)"""),
    ("""POST""", this.prefix, """v1.user.UserController.createUser(request:Request)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """""" + "$" + """id<[^/]+>""", """v1.user.UserController.getUserById(request:Request, id:String)"""),
    ("""PUT""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """""" + "$" + """id<[^/]+>""", """v1.user.UserController.updateUser(request:Request, id:String)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:2
  private[this] lazy val v1_user_UserController_getAllUsers0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val v1_user_UserController_getAllUsers0_invoker = createInvoker(
    
    (req:play.mvc.Http.Request) =>
      UserController_0.getAllUsers(fakeValue[play.mvc.Http.Request]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "users",
      "v1.user.UserController",
      "getAllUsers",
      Seq(classOf[play.mvc.Http.Request]),
      "GET",
      this.prefix + """""",
      """""",
      Seq()
    )
  )

  // @LINE:3
  private[this] lazy val v1_user_UserController_createUser1_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val v1_user_UserController_createUser1_invoker = createInvoker(
    
    (req:play.mvc.Http.Request) =>
      UserController_0.createUser(fakeValue[play.mvc.Http.Request]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "users",
      "v1.user.UserController",
      "createUser",
      Seq(classOf[play.mvc.Http.Request]),
      "POST",
      this.prefix + """""",
      """""",
      Seq()
    )
  )

  // @LINE:5
  private[this] lazy val v1_user_UserController_getUserById2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val v1_user_UserController_getUserById2_invoker = createInvoker(
    
    (req:play.mvc.Http.Request) =>
      UserController_0.getUserById(fakeValue[play.mvc.Http.Request], fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "users",
      "v1.user.UserController",
      "getUserById",
      Seq(classOf[play.mvc.Http.Request], classOf[String]),
      "GET",
      this.prefix + """""" + "$" + """id<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:6
  private[this] lazy val v1_user_UserController_updateUser3_route = Route("PUT",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val v1_user_UserController_updateUser3_invoker = createInvoker(
    
    (req:play.mvc.Http.Request) =>
      UserController_0.updateUser(fakeValue[play.mvc.Http.Request], fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "users",
      "v1.user.UserController",
      "updateUser",
      Seq(classOf[play.mvc.Http.Request], classOf[String]),
      "PUT",
      this.prefix + """""" + "$" + """id<[^/]+>""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:2
    case v1_user_UserController_getAllUsers0_route(params@_) =>
      call { 
        v1_user_UserController_getAllUsers0_invoker.call(
          req => UserController_0.getAllUsers(req))
      }
  
    // @LINE:3
    case v1_user_UserController_createUser1_route(params@_) =>
      call { 
        v1_user_UserController_createUser1_invoker.call(
          req => UserController_0.createUser(req))
      }
  
    // @LINE:5
    case v1_user_UserController_getUserById2_route(params@_) =>
      call(params.fromPath[String]("id", None)) { (id) =>
        v1_user_UserController_getUserById2_invoker.call(
          req => UserController_0.getUserById(req, id))
      }
  
    // @LINE:6
    case v1_user_UserController_updateUser3_route(params@_) =>
      call(params.fromPath[String]("id", None)) { (id) =>
        v1_user_UserController_updateUser3_invoker.call(
          req => UserController_0.updateUser(req, id))
      }
  }
}

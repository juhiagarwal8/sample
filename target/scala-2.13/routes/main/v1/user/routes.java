// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/Juhi Agarwal/git/play-samples/conf/users.routes
// @DATE:Sun Jun 28 00:55:17 IST 2020

package v1.user;

import users.RoutesPrefix;

public class routes {
  
  public static final v1.user.ReverseUserController UserController = new v1.user.ReverseUserController(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final v1.user.javascript.ReverseUserController UserController = new v1.user.javascript.ReverseUserController(RoutesPrefix.byNamePrefix());
  }

}

// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/Juhi Agarwal/git/play-samples/conf/routes
// @DATE:Sun Jun 28 00:55:17 IST 2020

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseHomeController HomeController = new controllers.ReverseHomeController(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseHomeController HomeController = new controllers.javascript.ReverseHomeController(RoutesPrefix.byNamePrefix());
  }

}

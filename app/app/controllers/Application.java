package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * This is the main application controller
 * 
 * @author JStevens
 * 
 */
public class Application extends Controller
{

	/**
	 * load the index page
	 * 
	 * @return
	 */
	public static Result index()
	{
		return redirect(controllers.routes.Application.home());
	}

	/*
	 * This is the home page for our app
	 */
	public static Result home()
	{
		return ok(views.html.index.render());
	}

}

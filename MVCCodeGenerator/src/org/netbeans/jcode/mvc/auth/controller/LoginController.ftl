package ${package};

import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.annotation.Controller;
import javax.mvc.annotation.View;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


@Path("app")
@Controller
public class LoginController {

    @Inject
    private Models models;

    @Context
    private SecurityContext securityContext;

    @GET
    public Response application() {
        if (securityContext.getUserPrincipal() != null) {
            return Response.ok("redirect:app/home").build();
        } else {
            return Response.ok("redirect:app/login").build();
        }
    }

    @View("login.jsp")
    @GET
    @Path("login")
    public void loginForm(@QueryParam("auth") int status) {
        if (status == -1) {
            models.put("msg", "login failed");
        }
    }

    @POST
    @Path("login")
    public Response login() {
        return Response.ok("redirect:app/home").build();
    }

    @View("home.jsp")
    @GET
    @Path("home")
    public void home() {
    }

    @POST
    @Path("logout")
    public Response logout(@Context HttpServletRequest request) throws ServletException {
        request.logout();
        return Response.ok("redirect:app/login").build();
    }
}

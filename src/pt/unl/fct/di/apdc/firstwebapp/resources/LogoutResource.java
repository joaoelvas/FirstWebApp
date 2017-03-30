package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.TokenID;

@Path("logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {
	
	private static final DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Gson g = new Gson();
	
	public LogoutResource() {
		
	}
	
	@POST
	@Path("/")
	public Response logout(TokenID data) {
		
		Key key = KeyFactory.createKey("Token", data.tokenID);
		
		try {
			DATASTORE.get(key);
			
			DATASTORE.delete(key);
			
			return Response.ok(g.toJson("Logged out.")).build();
		
			
		} catch(EntityNotFoundException e) {
			return Response.status(Status.UNAUTHORIZED).entity(g.toJson("No active session!")).build();
		} catch(Exception e) {
			LOG.logp(Level.WARNING, LoginResource.class.getName(), "isLogged", e.getMessage(), e);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
}

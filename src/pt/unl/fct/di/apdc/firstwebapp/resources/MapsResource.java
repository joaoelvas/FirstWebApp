package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.exceptions.NoUserFoundException;
import pt.unl.fct.di.apdc.firstwebapp.exceptions.TokenExpiredException;
import pt.unl.fct.di.apdc.firstwebapp.util.AddressData;
import pt.unl.fct.di.apdc.firstwebapp.util.TokenID;

@Path("maps")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MapsResource {

	private static final DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Gson g = new Gson();
	
	public MapsResource() {
		
	}
	
	@POST
	@Path("/address")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserAddress(TokenID data) {
		
		try {
			String username = getUsername(data.getTokenID());
			
			Key key = KeyFactory.createKey("User", username);
			
			Entity user = DATASTORE.get(key);
			
			String street = (String) user.getProperty("street");
			String local = (String) user.getProperty("local");
			String postalCode = (String) user.getProperty("postalCode");
			
			AddressData add = new AddressData(street, local, postalCode);
			
			LOG.info("Username: " + username + " requested address.");
			
			return Response.ok(g.toJson(add)).build();
			
		} catch(TokenExpiredException e) {
			return Response.status(Status.UNAUTHORIZED).entity(g.toJson("Token Expired")).build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.UNAUTHORIZED).entity(g.toJson("Authentication failed")).build();
		}
	}
	
	@POST
	@Path("/allusers")
	public Response getAllUserAddress(TokenID data) {
		
		
		try {
			String username = getUsername(data.getTokenID());
			
			Query q = new Query("User");
			
			PreparedQuery pq = DATASTORE.prepare(q);
			
			Iterable<Entity> i = pq.asIterable();
			
			List<Entity> users = new LinkedList<>();
			
			for(Entity e: i) {
				users.add(e);
			}
			
			LOG.info("Username: " + username + " requested alluser address.");
			
			return Response.ok(g.toJson(users)).build();
			
		} catch (TokenExpiredException e) {
			return Response.status(Status.UNAUTHORIZED).entity(g.toJson("Token Expired")).build();
		}
	}
	
	private String getUsername(String tokenId) throws TokenExpiredException {
		
		Key key = KeyFactory.createKey("Token", tokenId);
		
		try {
			Entity token = DATASTORE.get(key);
			
			String username = (String) token.getProperty("username");
			
			return username;
		} catch (EntityNotFoundException e) {
			throw new TokenExpiredException();
		}
	}
}

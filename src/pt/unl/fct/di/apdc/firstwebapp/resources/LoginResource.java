package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.api.client.util.Data;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Transaction;
import org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.TokenID;

@Path("login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {
	
	private static final DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Gson g = new Gson();
	
	public LoginResource() {
		
	}
	
//	@POST
//	@Path("/v0")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response doLogin(LoginData data) {
//		LOG.fine("Attempt to loggin user: " + data.username);
//		
//		if(data.username.equals("joaoelvas") && data.password.equals("password")) {
//			AuthToken token = new AuthToken(data.username);
//			LOG.info("User '" + data.username + "' logged in sussessfully.");
//			return Response.ok(g.toJson(token)).build();
//		}
//		
//		LOG.warning("Failed login attempt for username: " + data.username);
//		return Response.status(Status.FORBIDDEN).entity(g.toJson("Incorrect username or password.")).build();
//	}
//	
//	@POST
//	@Path("/v1")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response doLogin1(LoginData data) {
//		
//		try {
//			Entity person = DATASTORE.get(KeyFactory.createKey("Person", data.username));
//			String hashedPWD = (String) person.getProperty("password");
//			if(hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
//				AuthToken token = new AuthToken(data.username);
//				LOG.info("User '" + data.username + "' logged in sussessfully.");
//				return Response.ok(g.toJson(token)).build();
//			}
//			
//		} catch (EntityNotFoundException e) {
//			LOG.warning("Failed login attempt for username: " + data.username);
//			return Response.status(Status.FORBIDDEN).entity(g.toJson("(1) Incorrect username or password.")).build();
//		}
//		
//		
//		return Response.status(Status.FORBIDDEN).entity(g.toJson("(2) Incorrect username or password.")).build();
//	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin2(LoginData data, @Context HttpServletRequest request, @Context HttpHeaders headers) {
		
		LOG.fine("Attempt to login use: " + data.email);
		
		Transaction txn = DATASTORE.beginTransaction();
		Key key = KeyFactory.createKey("User", data.email);
		
		try {
			Entity user = DATASTORE.get(key);
			
			Query ctrQuery = new Query("UserStats").setAncestor(key);
			
			List<Entity> results = DATASTORE.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
			Entity uStats = null;
			
			if(results.isEmpty()) {
				uStats = new Entity("UserStats", user.getKey());
				uStats.setProperty("user_stats_logins", 0L);
				uStats.setProperty("user_stats_failed", 0L);
			} else {
				uStats = results.get(0);
			}
			
			String hashedPWD = (String) user.getProperty("password");
			
			if(hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
				// correct password
				
				// logs
				Entity log = new Entity("Log", user.getKey());
				log.setProperty("user_login_ip", request.getRemoteAddr());
				log.setProperty("user_login_host", request.getRemoteHost());
				log.setProperty("user_login_latlon", headers.getHeaderString("X-AppEngine-CityLatLon"));
				log.setProperty("user_login_city", headers.getHeaderString("X-AppEngine-City"));
				log.setProperty("user_login_country", headers.getHeaderString("X-AppEngine-Country"));
				log.setProperty("user_login_time", new Date());
				
				// get user statistics and updates it
				uStats.setProperty("user_stats_logins", 1L + (long) uStats.getProperty("user_stats_logins"));
				uStats.setProperty("user_stats_failed", 0L);
				uStats.setProperty("user_stats_last", new Date());
				
				// batch operation
				List<Entity> logs = Arrays.asList(log,uStats);
				DATASTORE.put(txn, logs);
				txn.commit();
				
				// return token
				AuthToken token = new AuthToken(data.email);
				
				// store the token
				Entity t = new Entity("Token", token.getTokenID());
				t.setProperty("username", token.getUsername());
				t.setProperty("creation_time", token.getCreationData());
				t.setProperty("expiration_time", token.getExpirationData());
				
				DATASTORE.put(t);
				
				LOG.info("User '" + data.email + "' logged in successfully.");
				return Response.ok(g.toJson(token)).build();
				
			} else {
				// incorrect password
				
				uStats.setProperty("user_stats_failed", 1L + (long) uStats.getProperty("user_stats_failed"));
				DATASTORE.put(txn, uStats);
				txn.commit();
				
				LOG.warning("Wrong password for user: '" + data.email + "'.");
				return Response.status(Status.FORBIDDEN).build();
			}
			
		} catch(EntityNotFoundException e) {
			// username does not exist
			LOG.warning("Failed login attempt on username: " + data.email);
			LOG.logp(Level.WARNING, LoginResource.class.getName(), "doLogin2", e.getMessage(), e);
			return Response.status(Status.FORBIDDEN).build();
		} catch(Exception e) {
			LOG.logp(Level.WARNING, LoginResource.class.getName(), "doLogin2", e.getMessage(), e);
			return Response.status(Status.BAD_REQUEST).build();
		}finally {
			if(txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
	
	@POST
	@Path("/check")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response isLogged(TokenID data) {
		
		
		Key key = KeyFactory.createKey("Token", data.getTokenID());
		
		try {
			Entity token = DATASTORE.get(key);
			
			if((long) token.getProperty("expiration_time") < System.currentTimeMillis()) {
				return Response.status(Status.UNAUTHORIZED).entity(g.toJson("Session expired.")).build();
			} else {
				return Response.ok(g.toJson("Resuming session!")).build();
			}
			
		} catch(EntityNotFoundException e) {
			return Response.status(Status.FORBIDDEN).entity(g.toJson("Invalid token!")).build();
		} catch(Exception e) {
			LOG.logp(Level.WARNING, LoginResource.class.getName(), "isLogged", e.getMessage(), e);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@POST
	@Path("/user")
	public Response user(LoginData data) {
		Key userKey = KeyFactory.createKey("Person", data.email);
		
		try {
			Entity user = DATASTORE.get(userKey);
			String HashedPWD = (String) user.getProperty("password");
			
			if(HashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);
				Date yesterday = cal.getTime();
				
				// obtain user login statistics
				Filter propertyFilter = 
						new FilterPredicate("user_login_time", FilterOperator.GREATER_THAN_OR_EQUAL, yesterday);
				
				Query ctrQuery = new Query("UserLog")
						.setAncestor(userKey)
						.setFilter(propertyFilter)
						.addSort("user_login_time", SortDirection.DESCENDING);
				
				ctrQuery.addProjection(new PropertyProjection("user_login_time", Data.class));
				List<Entity> results = DATASTORE.prepare(ctrQuery).asList(FetchOptions.Builder.withLimit(3));
				
				List<Date> loginDates = new ArrayList<>();
				
				for(Entity userLog:results) {
					loginDates.add((Date) userLog.getProperty("user_login_time"));
				}
				
				return Response.ok(g.toJson(loginDates)).build();
				
			} else {
				LOG.warning("Wrong password for username: " + data.email);
				return Response.status(Status.FORBIDDEN).build();
			}
			
		} catch(EntityNotFoundException e) {
			LOG.warning("User: '" + data.email + "' not found.");
			return Response.status(Status.FORBIDDEN).entity(g.toJson("(1) Incorrect username or password.")).build();
		}
	}
}

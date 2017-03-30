package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
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
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

@Path("register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	private static final DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Gson g = new Gson();
	
	public RegisterResource() {
		
	}
	
//	@POST
//	@Path("/v1")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response register(RegisterData data) {
//		
//		Entity person = new Entity("Person", data.username);
//		person.setProperty("password",DigestUtils.sha512Hex(data.password));
//		person.setUnindexedProperty("user_creation_time", new Date());
//		
//		try {
//			DATASTORE.put(person);
//		} catch(Exception e) {
//			return Response.status(Status.BAD_REQUEST).entity(g.toJson("Error occured.")).build();
//		}
//		
//		
//		return Response.ok().entity(g.toJson("User registered.")).build();
//	}
//	
//	@POST
//	@Path("/v2")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response register2(RegisterData data) {
//		
//		
//		
//		try {
//			DATASTORE.get(KeyFactory.createKey("Person", data.username));
//		} catch (EntityNotFoundException e1) {
//			try {
//				Entity person = new Entity("Person", data.username);
//				person.setProperty("password",DigestUtils.sha512Hex(data.password));
//				person.setProperty("email",data.email);
//				person.setProperty("name",data.name);
//				person.setUnindexedProperty("user_creation_time", new Date());
//				
//				DATASTORE.put(person);
//				LOG.info("User '" + data.username + "' registered!");
//			} catch(Exception e) {
//				return Response.status(Status.BAD_REQUEST).entity(g.toJson("Error occured.")).build();
//			}
//			
//			return Response.ok().entity(g.toJson("User registered.")).build();
//		}
//		
//		LOG.warning("Entity found: " + data.username);
//		return Response.status(Status.FORBIDDEN).entity(g.toJson("User already exists.")).build();	
//	}
//	
//	@POST
//	@Path("/v3")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response register3(RegisterData data) {
//		Transaction txn = DATASTORE.beginTransaction();
//		
//		try {
//			DATASTORE.get(KeyFactory.createKey("Person", data.username));
//			txn.rollback();
//			LOG.warning("Entity found: " + data.username);
//			return Response.status(Status.FORBIDDEN).entity(g.toJson("User already exists.")).build();
//		} catch(EntityNotFoundException e) {
//			Entity person = new Entity("Person", data.username);
//			person.setProperty("password",DigestUtils.sha512Hex(data.password));
//			person.setProperty("email",data.email);
//			person.setProperty("name",data.name);
//			person.setUnindexedProperty("user_creation_time", new Date());
//			
//			DATASTORE.put(txn, person);
//			LOG.info("User '" + data.username + "' registered!");
//			txn.commit();
//			return Response.ok().entity(g.toJson("User registered.")).build();
//		} finally {
//			if(txn.isActive()) {
//				txn.rollback();
//			}
//		}
//	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(RegisterData data) {
		Transaction txn = DATASTORE.beginTransaction();
		try {
			DATASTORE.get(KeyFactory.createKey("User", data.email));
			txn.rollback();
			LOG.warning("Entity found: " + data.email);
			return Response.status(Status.FORBIDDEN).entity(g.toJson("User already exists.")).build();
		} catch(EntityNotFoundException e) {
			Entity user = new Entity("User", data.email);
			user.setProperty("fixedPhone", data.fixedPhone);
			user.setProperty("mobilePhone", data.mobilePhone);
			user.setProperty("street",data.street);
			user.setProperty("comp",data.comp);
			user.setProperty("local",data.local);
			user.setProperty("postalCode",data.cp);
			user.setProperty("nif",data.nif);
			user.setProperty("cc",data.cc);
			user.setProperty("password",DigestUtils.sha512Hex(data.password));
			user.setUnindexedProperty("user_creation_time", new Date());
			
			DATASTORE.put(txn, user);
			LOG.info("User '" + data.email + "' registered!");
			txn.commit();
			return Response.ok().entity(g.toJson("User registered.")).build();
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	
}

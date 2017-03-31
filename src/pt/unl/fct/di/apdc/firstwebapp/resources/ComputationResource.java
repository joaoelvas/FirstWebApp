package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;

@Path("tasks")
public class ComputationResource {

	private static final DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private final Gson g = new Gson();
	
	private static final DateFormat fmt = new SimpleDateFormat("yyy-MM-dd HH:mm:ss:SSSZ");
	
	public ComputationResource() {
		
	}
	
	@GET
	@Path("/time")
	public Response getCurrentTime() {
		LOG.fine("Replying to data request.");
		return Response.ok().entity(g.toJson(fmt.format(new Date()))).build();
	}
	
	@POST
	@Path("/tokencleanup")
	public Response executeComputeTask() {
		LOG.fine("Starting to execute computation tasks.");
		
		try {
			Query q = new Query("Token");
			
			PreparedQuery pq = DATASTORE.prepare(q);
			
			Iterable<Entity> i = pq.asIterable();
			
			for(Entity e: i) {
				long expiration_time = (long) e.getProperty("expiration_time");
				
				if(expiration_time < System.currentTimeMillis()) {
					DATASTORE.delete(e.getKey());
				}
				
				LOG.fine("Deleted token created on " + (String) e.getProperty("creation_time"));
				
			}
			
		} catch (Exception e) {
			LOG.logp(Level.SEVERE, this.getClass().getCanonicalName(), "executeComputeTask", "An exception has ocurred", e);
			return Response.serverError().build();
		}
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/tokencleanup")
	public Response triggerExecuteComputeTask() {
		Queue queue = QueueFactory.getDefaultQueue(); 
		queue.add(TaskOptions.Builder.withUrl("/rest/tasks/tokencleanup"));
		return Response.ok().build(); 
		}
	
	
}

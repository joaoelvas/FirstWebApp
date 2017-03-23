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

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;

@Path("/utils")
public class ComputationResource {

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
	@Path("/compute")
	public Response executeComputeTask() {
		LOG.fine("Starting to execute computation tasks.");
		
		try {
			Thread.sleep(60*1000*5);
		} catch (Exception e) {
			LOG.logp(Level.SEVERE, this.getClass().getCanonicalName(), "executeComputeTak", "An exception has ocurred", e);
			return Response.serverError().build();
		}
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/compute")
	public Response triggerExecuteComputeTask() {
		Queue queue = QueueFactory.getDefaultQueue(); 
		queue.add(TaskOptions.Builder.withUrl("/rest/utils/compute"));
		return Response.ok().build(); 
		}
	
	
}

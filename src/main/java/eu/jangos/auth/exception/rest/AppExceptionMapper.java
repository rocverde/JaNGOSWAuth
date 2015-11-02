package eu.jangos.auth.exception.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
 
/**
 * 
 * @author Warkdev
 */
public class AppExceptionMapper implements ExceptionMapper<AppException> {
 
	public Response toResponse(AppException ex) {
		return Response.status(ex.getStatus())
				.entity(new ErrorMessage(ex))
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
 
}

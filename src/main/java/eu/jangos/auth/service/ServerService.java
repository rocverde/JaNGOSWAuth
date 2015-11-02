package eu.jangos.auth.service;

import eu.jangos.auth.controller.ServersController;
import eu.jangos.auth.dto.ServersDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.exception.rest.AppException;
import eu.jangos.auth.exception.rest.AppExceptionMapper;
import eu.jangos.auth.model.Servers;
import eu.jangos.auth.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * ServerService is the REST API to handle queries towards the database for the
 * servers entities.
 *
 * @author Warkdev
 * @version 1.0
 */
@Stateless
@Path("/servers")
@Api(value = "/servers", tags = "servers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerService {

    @EJB
    ServersController sc;

    @GET
    @Path("{id}")
    @ApiOperation(value = "Find server by id",
            notes = "For a valid response, the id must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "role not found")
    })
    /**
     * Find a server with the corresponding ID.
     */
    public Response findByID(
            @ApiParam(value = "ID of the role to find", required = true) @PathParam("id") int id)
            throws AppException {

        Response response = Utils.checkPermission("servers:find");

        if (response != null) {
            return response;
        }
        
        Servers server;

        try {
            server = sc.get(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        return Response.status(Response.Status.OK).entity(Utils.getServersDTO(server)).build();
    }

    @GET
    @Path("/findByName/{name}")
    @ApiOperation(value = "Find a server by name",
            notes = "For a valid response, the name must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid name"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Role not found")
    })
    /**
     * Find a server with the corresponding name.
     */
    public Response findByName(
            @ApiParam(value = "Name of the role to find", required = true) @PathParam("name") String name)
            throws AppException {

        Response response = Utils.checkPermission("servers:find");

        if (response != null) {
            return response;
        }
        
        Servers server;
        try {
            server = sc.get(name);
        } catch (InvalidArgumentException iae) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    404,
                    iae.getMessage(),
                    iae.getDeveloperMessage(),
                    ""));
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        return Response.status(Response.Status.OK).entity(Utils.getServersDTO(server)).build();
    }

    @GET
    @Path("/all")
    @ApiOperation(value = "Returns all servers")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),})
    /**
     * Returns all servers available in the database.
     */
    public Response getAll()
            throws AppException {

        Response response = Utils.checkPermission("servers:find");

        if (response != null) {
            return response;
        }
        
        List<ServersDTO> listDTO = new ArrayList<>();
        List<Servers> listServers = sc.getAll();

        listServers.stream().forEach((s) -> {
            listDTO.add(Utils.getServersDTO(s));
        });

        return Response.status(Response.Status.OK).entity(listDTO).build();
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Delete the server with the given ID")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Parameter not found")
    })
    /**
     * Delete the server with the given ID.
     */
    public Response delete(
            @ApiParam(value = "ID of the server to be deleted", required = true) @PathParam("id") int id) {

        Response response = Utils.checkPermission("servers:find");

        if (response != null) {
            return response;
        }
        
        try {
            this.sc.delete(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @ApiOperation(value = "Create the server according to the given information")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid input or server already exists"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "role not found")
    })
    /**
     * Create a new server according to the given information.
     */
    public Response create(
            @ApiParam(value = "Role to be created", required = true) ServersDTO server) {

        Response response = Utils.checkPermission("servers:create");

        if (response != null) {
            return response;
        }
        
        try {
            return Response.status(Response.Status.OK).entity(Utils.getServersDTO(this.sc.create(server))).build();
        } catch (InvalidArgumentException iae) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    400,
                    iae.getMessage(),
                    iae.getDeveloperMessage(),
                    ""));
        } catch (EntityExistsException eee) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    400,
                    eee.getMessage(),
                    "",
                    ""));
        }
    }

    @PUT
    @ApiOperation(value = "Update the server according to the given information")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Server not found")
    })
    /**
     * Update a server according to the given information.
     */
    public Response update(
            @ApiParam(value = "Server to be updated", required = true) ServersDTO server) {

        Response response = Utils.checkPermission("servers:update");

        if (response != null) {
            return response;
        }
        
        try {
            return Response.status(Response.Status.OK).entity(Utils.getServersDTO(this.sc.update(server))).build();
        } catch (InvalidArgumentException iae) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    400,
                    iae.getMessage(),
                    iae.getDeveloperMessage(),
                    ""));
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }
    }
}

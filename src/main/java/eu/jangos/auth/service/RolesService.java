package eu.jangos.auth.service;

import eu.jangos.auth.controller.RolesController;
import eu.jangos.auth.dto.RolesDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.exception.rest.AppException;
import eu.jangos.auth.exception.rest.AppExceptionMapper;
import eu.jangos.auth.model.Roles;
import eu.jangos.auth.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * RolesService is the REST API to handle queries towards the database for the
 * roles entities.
 *
 * @author Warkdev
 * @version 1.0
 */
@Stateless
@Path("/roles")
@Api(value = "/roles", tags = "roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RolesService {

    @EJB
    RolesController rc;

    @GET
    @Path("{id}")
    @ApiOperation(value = "Find role by id",
            notes = "For a valid response, the id must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "role not found")
    })
    /**
     * Find a role with the corresponding ID.
     */
    public Response findByID(
            @ApiParam(value = "ID of the role to find", required = true) @PathParam("id") int id)
            throws AppException {

        Response response = Utils.checkPermission("roles:find");

        if (response != null) {
            return response;
        }
        
        Roles role;

        try {
            role = rc.get(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        return Response.status(Response.Status.OK).entity(Utils.getRolesDTO(role)).build();
    }

    @GET
    @Path("/findByName/{name}")
    @ApiOperation(value = "Find a role by name",
            notes = "For a valid response, the name must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid name"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Role not found")
    })
    /**
     * Find a locale with the corresponding name.
     */
    public Response findByName(
            @ApiParam(value = "Name of the role to find", required = true) @PathParam("name") String name)
            throws AppException {

        Response response = Utils.checkPermission("roles:find");

        if (response != null) {
            return response;
        }
        
        Roles role;
        try {
            role = rc.get(name);
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

        return Response.status(Response.Status.OK).entity(Utils.getRolesDTO(role)).build();
    }

    @GET
    @Path("/all")
    @ApiOperation(value = "Returns all roles")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),})
    /**
     * Returns all roles available in the database.
     */
    public Response getAll()
            throws AppException {

        Response response = Utils.checkPermission("roles:find");

        if (response != null) {
            return response;
        }
        
        List<RolesDTO> listDTO = new ArrayList<>();
        List<Roles> listRoles = rc.getAll();

        listRoles.stream().forEach((r) -> {
            listDTO.add(Utils.getRolesDTO(r));
        });

        return Response.status(Response.Status.OK).entity(listDTO).build();
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Delete the role with the given ID")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Parameter not found")
    })
    /**
     * Delete the role with the given ID.
     */
    public Response delete(
            @ApiParam(value = "ID of the role to be deleted", required = true) @PathParam("id") int id) {

        Response response = Utils.checkPermission("roles:delete");

        if (response != null) {
            return response;
        }
        
        try {
            this.rc.delete(id);
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
    @ApiOperation(value = "Create the role according to the given information")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid input or role already exists"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "role not found")
    })
    /**
     * Create a new role according to the given information.
     */
    public Response create(
            @ApiParam(value = "Role to be created", required = true) RolesDTO role) {

        Response response = Utils.checkPermission("roles:create");

        if (response != null) {
            return response;
        }
        
        try {
            return Response.status(Response.Status.OK).entity(Utils.getRolesDTO(this.rc.create(role))).build();
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
    @ApiOperation(value = "Update the role according to the given information")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Role not found")
    })
    /**
     * Update a role according to the given information.
     */
    public Response update(
            @ApiParam(value = "Role to be updated", required = true) RolesDTO role) {

        Response response = Utils.checkPermission("roles:update");

        if (response != null) {
            return response;
        }
        
        try {
            return Response.status(Response.Status.OK).entity(Utils.getRolesDTO(this.rc.update(role))).build();
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

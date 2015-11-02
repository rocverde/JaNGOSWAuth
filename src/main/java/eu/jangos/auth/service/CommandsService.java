package eu.jangos.auth.service;

import eu.jangos.auth.controller.CommandsController;
import eu.jangos.auth.dto.CommandsDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.rest.AppException;
import eu.jangos.auth.exception.rest.AppExceptionMapper;
import eu.jangos.auth.model.Commands;
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * CommandsService is the REST API to handle queries towards the database for
 * the commands entities.
 *
 * @author Warkdev
 * @version 1.0
 */
@Stateless
@Path("/commands")
@Api(value = "/commands", tags = "commands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommandsService {

    @EJB
    CommandsController cc;

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Find a command by id",
            notes = "For a valid response, the id must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "command not found")
    })
    /**
     * Find a command with the corresponding ID.
     */
    public Response findByID(
            @ApiParam(value = "ID of the command to find", required = true) @PathParam("id") int id)
            throws AppException {

        Response response = Utils.checkPermission("commands:find");

        if (response != null) {
            return response;
        }

        Commands command;
        try {
            command = cc.get(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        return Response.status(Response.Status.OK).entity(Utils.getCommandsDTO(command)).build();
    }

    @GET
    @Path("/all")
    @ApiOperation(value = "Returns all commands")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access")
    })
    /**
     * Returns all commands available in the database.
     */
    public Response getAll()
            throws AppException {
        Response response = Utils.checkPermission("commands:find");

        if (response != null) {
            return response;
        }

        List<CommandsDTO> listDTO = new ArrayList<>();
        List<Commands> listZone = cc.getAll();

        listZone.stream().forEach((c) -> {
            listDTO.add(Utils.getCommandsDTO(c));
        });

        return Response.status(Response.Status.OK).entity(listDTO).build();
    }
}

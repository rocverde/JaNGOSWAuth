package eu.jangos.auth.service;

import eu.jangos.auth.Constants;
import eu.jangos.auth.controller.AccountController;
import eu.jangos.auth.controller.BannedAccountController;
import eu.jangos.auth.controller.BannedIPController;
import eu.jangos.auth.controller.ParameterController;
import eu.jangos.auth.model.Account;
import eu.jangos.auth.utils.Utils;
import eu.jangos.auth.dto.AccountDTO;
import eu.jangos.auth.dto.BannedIPDTO;
import eu.jangos.auth.dto.Token;
import eu.jangos.auth.enums.BanType;
import eu.jangos.auth.enums.DateType;
import eu.jangos.auth.enums.LockType;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.exception.rest.AppException;
import eu.jangos.auth.exception.rest.AppExceptionMapper;
import eu.jangos.auth.model.BannedIP;
import eu.jangos.auth.security.token.JToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AccountService is the REST API to handle queries towards the database for the
 * account entities.
 *
 * @author Warkdev
 * @version 1.0
 */
@Stateless
@Path("/account")
@Api(value = "/account", tags = "account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    /**
     * AccountController business logic.
     */
    @EJB
    AccountController ac;

    /**
     * BannedAccountController business logic.
     */
    @EJB
    BannedAccountController bac;

    /**
     * BannedIPController business logic.
     */
    @EJB
    BannedIPController bic;

    /**
     * ParameterController business logic.
     */
    @EJB
    ParameterController pc;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create an account with the given parameters",
            notes = "For a valid response, the name must be available and should not be empty. As well, password must not be empty.",
            response = Response.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid input."),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "A dependency was not fullfilled."),
        @ApiResponse(code = 409, message = "Account already exist.")
    })
    /**
     * Create a new account with the provided parameters.
     */
    public Response create(
            @ApiParam(value = "Account to be created", required = true) AccountDTO account,
            @ApiParam(value = "Password of the account to be created", required = true) char[] password) {

        Response response = Utils.checkPermission("account:create");

        if (response != null) {
            return response;
        }

        try {
            return Response.status(Response.Status.OK).entity(Utils.getAccountDTO(ac.create(account, password))).build();
        } catch (EntityExistsException aee) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.CONFLICT.getStatusCode(),
                    405,
                    aee.getMessage(),
                    "",
                    ""));
        } catch (InvalidArgumentException iae) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    400,
                    iae.getMessage(),
                    iae.getDeveloperMessage(),
                    ""));
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    400,
                    die.getMessage(),
                    "",
                    ""));
        }
    }

    @POST
    @Path("/login/{name}/{ip}/{locale}/{session}")
    @ApiOperation(value = "Log in the given account",
            notes = "For a valid response, the account name must exist, the IP must have a valid IPv4 format and the locale must exists.",
            response = Response.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Account not found")
    })
    /**
     * Log in a given account.
     */
    public Response login(
            @ApiParam(value = "Name of the account to log in", required = true) @QueryParam("name") String name,
            @ApiParam(value = "IP of the account to log in", required = true) @QueryParam("ip") String ip,
            @ApiParam(value = "Locale of the account to log in", required = true) @QueryParam("locale") String locale,
            @ApiParam(value = "Session key of the account to log in", required = true) @QueryParam("session") String session) {
        Response response = Utils.checkPermission("account:login");

        if (response != null) {
            return response;
        }

        try {
            ac.login(name, ip, locale, session);
            return Response.status(Response.Status.OK).build();
        } catch (InvalidArgumentException iae) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    400,
                    iae.getMessage(),
                    iae.getDeveloperMessage(),
                    ""));
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    400,
                    die.getMessage(),
                    "",
                    ""));
        }
    }

    @POST
    @Path("/loginRealm/{name}/{realm}")
    @ApiOperation(value = "Log in a the account with the corresponding name into the given realm.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Account or realm not found")
    })
    /**
     * Log in a given account into a realm.
     */
    public Response loginRealm(
            @ApiParam(value = "Name of the account to log in on  the realm.", required = true) @QueryParam("name") String name,
            @ApiParam(value = "Name of the realm on which the account has logged in.", required = true) @QueryParam("realm") String realm) {
        Response response = Utils.checkPermission("realm:login");

        if (response != null) {
            return response;
        }

        try {
            ac.loginRealm(name, realm);
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
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Find account by id",
            notes = "For a valid response, the id must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid ID"),
        @ApiResponse(code = 403, message = "Access is forbidden"),
        @ApiResponse(code = 404, message = "Account not found")
    })
    /**
     * Find an account with the corresponding ID.
     */
    public Response find(
            @ApiParam(value = "ID of the account to find", required = true) @PathParam("id") int id)
            throws AppException {
        Response response = Utils.checkPermission("account:find");

        if (response != null) {
            return response;
        }

        Account account;
        try {
            account = ac.getAccount(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        AccountDTO accountDTO = Utils.getAccountDTO(account);
        return Response.status(Response.Status.OK).entity(accountDTO).build();

    }

    @GET
    @Path("/{name}/{match}/{lock}/{cdtype}/{cfrom}/{cto}/{ldtype}/{lfrom}/{lto}")
    @ApiOperation(value = "Find all the matching accounts to the given parameters", notes = "The date parameters must be provided in epoch format")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "No matching account found")
    })
    /**
     * Find all accounts matching the given parameters.
     */
    public Response find(
            @ApiParam(value = "Name of the account to be found", required = true)
            @PathParam("name") String name,
            @ApiParam(value = "Indicates whether the name must match exactly or not", required = true, allowableValues = "true, false")
            @PathParam("match") boolean match,
            @ApiParam(value = "Indicates whether the accounts to be retrieved are locked or not", required = true, allowableValues = "LOCKED,UNLOCKED,BOTH")
            @DefaultValue(value = "BOTH") @PathParam("lock") LockType type,
            @ApiParam(value = "Indicates the creation date filter type.")
            @DefaultValue(value = "NONE") @PathParam("cdtype") DateType cdType,
            @ApiParam(value = "Indicates the creation date to compare to (start date)", required = true)
            @PathParam("cfrom") long creationFrom,
            @ApiParam(value = "Indicates the creation date to compare to (end date)")
            @PathParam("cto") long creationTo,
            @ApiParam(value = "Indicates the login date filter type.")
            @DefaultValue(value = "NONE") @PathParam("ldtype") DateType ldtype,
            @ApiParam(value = "Indicates the login date to compare to (start date)", required = true)
            @PathParam("lfrom") long loginFrom,
            @ApiParam(value = "Indicates the login date to compare to (end date)")
            @PathParam("lto") long loginTo
    )
            throws AppException {
        Response response = Utils.checkPermission("account:find");

        if (response != null) {
            return response;
        }

        List<AccountDTO> listDTO = new ArrayList<>();
        List<Account> listAccount = ac.getMatchingAccounts(name + (match ? "" : "%"), type, cdType, new Date(creationFrom), new Date(creationTo), ldtype, new Date(loginFrom), new Date(loginTo));

        if (listAccount.isEmpty()) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    400,
                    "No matching account found.",
                    "",
                    ""));
        } else {
            listAccount.stream().forEach((a) -> {
                listDTO.add(Utils.getAccountDTO(a));
            });
            return Response.status(Response.Status.OK).entity(listDTO).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Delete the account from the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid ID"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Account not found")
    })
    /**
     * Delete the account with the given ID.
     */
    public Response delete(
            @ApiParam(value = "ID of the account to be deleted", required = true) @PathParam("id") int id) {
        Response response = Utils.checkPermission("account:delete");

        if (response != null) {
            return response;
        }

        try {
            this.ac.delete(id);
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
        return Response.status(Response.Status.OK).build();
    }

    @PUT
    @ApiOperation(value = "Update the given account.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Account parameters are invalid"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Account not found")
    })
    /**
     * Update the provided account.
     */
    public Response update(
            @ApiParam(value = "Account to be updated", required = true) AccountDTO account) {
        Response response = Utils.checkPermission("account:update");

        if (response != null) {
            return response;
        }

        try {
            this.ac.update(account);
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

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/ban/{account}/{banisher}/{reason}/{duration}")
    @ApiOperation(value = "Ban the given account.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Account or banisher not found")
    })
    /**
     * Ban the given account.
     */
    public Response ban(
            @ApiParam(value = "Account to ban", required = true) @PathParam("account") String account,
            @ApiParam(value = "Name of the banisher", required = true) @PathParam("banisher") String banisher,
            @ApiParam(value = "Reason of the ban", required = true) @PathParam("reason") String reason,
            @ApiParam(value = "Duration of the ban (in days), 0 means a permanent ban.", required = true) @PathParam("duration") int duration) {

        Response response = Utils.checkPermission("account:ban");

        if (response != null) {
            return response;
        }

        try {
            this.bac.banAccount(this.ac.getAccount(account.toUpperCase()), this.ac.getAccount(banisher.toUpperCase()), reason, duration);
        } catch (Exception e) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    "There was a problem during the ban operation.",
                    e.getMessage(),
                    ""));
        }

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/unban")
    @ApiOperation(value = "Unban the given account.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Account not found")
    })
    /**
     * Unban the given account.
     */
    public Response unban(@ApiParam(value = "Account to unban", required = true) String account) {

        Response response = Utils.checkPermission("account:ban");

        if (response != null) {
            return response;
        }

        try {
            this.bac.unbanAccount(this.ac.getAccount(account));
        } catch (Exception e) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    "There was a problem during the unban operation.",
                    e.getMessage(),
                    ""));
        }

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/lock")
    @ApiOperation(value = "Lock the given account.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Error during input validation"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Account not found")
    })
    /**
     * Lock the given account.
     */
    public Response lock(@ApiParam(value = "Account to lock", required = true) String account) {

        Response response = Utils.checkPermission("account:lock");

        if (response != null) {
            return response;
        }

        try {
            this.ac.lock(account);
        } catch (InvalidArgumentException iea) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    404,
                    iea.getMessage(),
                    iea.getDeveloperMessage(),
                    ""));
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
    @Path("/unlock")
    @ApiOperation(value = "Unlock the given account.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Error during input validation"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Account not found")
    })
    /**
     * Unlock the given account.
     */
    public Response unlock(@ApiParam(value = "Account to unlock", required = true) String account) {

        Response response = Utils.checkPermission("account:lock");

        if (response != null) {
            return response;
        }

        try {
            this.ac.unlock(account);
        } catch (InvalidArgumentException iea) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    404,
                    iea.getMessage(),
                    iea.getDeveloperMessage(),
                    ""));
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/isBanned/{id}")
    @ApiOperation(value = "Indicates whether the account with the given ID is banned or not",
            notes = "This method returns true or false.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Account does not exist")
    })
    /**
     * Indicates whether the given account ID is banned or not.
     */
    public Response isBanned(
            @ApiParam(value = "ID of the account to check", required = true)
            @PathParam("id") int id) {
        Response response = Utils.checkPermission("account:ban");

        if (response != null) {
            return response;
        }

        try {
            this.ac.getAccount(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        return Response.status(Response.Status.OK).entity(bac.isAccountBanned(id)).build();
    }

    @GET
    @Path("/isIPBanned/{ip}")
    @ApiOperation(value = "Indicates whether an IP address is banned or not.",
            notes = "This method returns true or false.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Error during the input validation"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "IP does not exist")
    })
    /**
     * Indicates whether an IP is ban or not.
     */
    public Response isIPBanned(
            @ApiParam(value = "IP to be checked", required = true) @PathParam("ip") String ip) {

        Response response = Utils.checkPermission("account:ban");

        if (response != null) {
            return response;
        }

        try {
            boolean isBanned = this.bic.isIPBanned(ip);
            return Response.status(Response.Status.OK).entity(isBanned).build();
        } catch (InvalidArgumentException iae) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    404,
                    iae.getMessage(),
                    iae.getDeveloperMessage(),
                    ""));
        }
    }

    @GET
    @Path("/findIP/{type}")
    @ApiOperation(value = "Returns a list of all matching banned IP depending on the filter parameter.")
    @ApiResponses(value
            = @ApiResponse(code = 403, message = "Forbidden access")
    )
    /**
     * Returns a list of all matching banned IP records depending on the filter
     * parameter.
     */
    public Response findIPBanned(
            @ApiParam(value = "The filter to be applied", required = true) @PathParam("type") BanType type) {
        logger.debug("findIPBanned entered.");

        Response response = Utils.checkPermission("account:ban");

        if (response != null) {
            return response;
        }

        List<BannedIPDTO> listDTO = new ArrayList<>();
        List<BannedIP> listBannedIP = new ArrayList<>();

        switch (type) {
            case ALL:
                listBannedIP = this.bic.getAll();
                break;
            case ACTIVE:
                listBannedIP = this.bic.getAllActiveBannedIPs();
                break;
            case INACTIVE:
                listBannedIP = this.bic.getAllInactiveBannedIPs();
                break;
        }

        if (listBannedIP.isEmpty()) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    400,
                    "No matching account found.",
                    "",
                    ""));
        } else {
            listBannedIP.stream().forEach((b) -> {
                listDTO.add(Utils.getBannedIPDTO(b));
            });
            return Response.status(Response.Status.OK).entity(listDTO).build();
        }
    }

    @POST
    @Path("/banIP/{ip}/{banisher}/{reason}/{duration}")
    @ApiOperation(value = "Ban the given ip.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Banisher account not found")
    })
    /**
     * Ban the given IP.
     */
    public Response banIP(
            @ApiParam(value = "IP to ban", required = true) @PathParam("ip") String ip,
            @ApiParam(value = "Name of the banisher", required = true) @PathParam("banisher") String banisher,
            @ApiParam(value = "Reason of the ban", required = true) @PathParam("reason") String reason,
            @ApiParam(value = "Duration of the ban (in days), 0 means a permanent ban.", required = true) @PathParam("duration") int duration) {

        Response response = Utils.checkPermission("account:ban");

        if (response != null) {
            return response;
        }

        try {
            this.bic.banIP(ip, this.ac.getAccount(banisher), reason, duration);
        } catch (Exception e) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    "There was a problem during the ban operation.",
                    e.getMessage(),
                    ""));
        }

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/unbanIP")
    @ApiOperation(value = "Unban the given ip.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "IP not found")
    })
    /**
     * Unban the given ip.
     */
    public Response unbanIP(@ApiParam(value = "Account to unban", required = true) String ip) {

        Response response = Utils.checkPermission("account:ban");

        if (response != null) {
            return response;
        }

        try {
            this.bic.unbanIP(ip);
        } catch (Exception e) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    "There was a problem during the unban operation.",
                    e.getMessage(),
                    ""));
        }

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/impersonation")
    @ApiOperation(value = "Generate a token allowing to impersonate the API-call")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Error during input validation"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "The account name or the server key does not exist")
    })
    /**
     * Generate a token allowing to impersonate the API-call
     */
    public Response impersonation(@ApiParam(value = "Account to impersonate") String account) {

        Response response = Utils.checkPermission("account:impersonate");

        if (response != null) {
            return response;
        }

        Token token = new Token();
        JToken jToken = new JToken();

        try {
            // Token is generated and encrypted based on the server key.
            // It simply contains the account id and the name. Can be enrich with TTL or so.
            Account a = this.ac.getAccount(account.toUpperCase());
            String key = this.pc.getParameter(Constants.SERVER_KEY_PARAM).getVal();

            jToken.setValue(a.getId() + ":" + a.getName());
            jToken.encrypt(key);
            token.setValue(jToken.getValue());

            a.setToken(jToken.getValue());

            ac.update(a);

            return Response.status(Response.Status.OK).entity(token).build();
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        } catch (InvalidArgumentException iae) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    404,
                    iae.getMessage(),
                    iae.getDeveloperMessage(),
                    ""));
        }
    }
}

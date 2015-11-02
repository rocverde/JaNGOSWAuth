package eu.jangos.auth.utils;

import eu.jangos.auth.model.Account;
import eu.jangos.auth.model.BannedAccount;
import eu.jangos.auth.model.Locale;
import eu.jangos.auth.model.Realm;
import eu.jangos.auth.dto.AccountDTO;
import eu.jangos.auth.dto.AuthParameterDTO;
import eu.jangos.auth.dto.BannedAccountDTO;
import eu.jangos.auth.dto.BannedIPDTO;
import eu.jangos.auth.dto.CommandsDTO;
import eu.jangos.auth.dto.LocaleDTO;
import eu.jangos.auth.dto.RealmDTO;
import eu.jangos.auth.dto.RealmTimeZoneDTO;
import eu.jangos.auth.dto.RealmTypeDTO;
import eu.jangos.auth.dto.RolesDTO;
import eu.jangos.auth.dto.ServersDTO;
import eu.jangos.auth.exception.rest.AppException;
import eu.jangos.auth.exception.rest.AppExceptionMapper;
import eu.jangos.auth.model.BannedIP;
import eu.jangos.auth.model.Commands;
import eu.jangos.auth.model.Parameter1;
import eu.jangos.auth.model.RealmTimeZone;
import eu.jangos.auth.model.RealmType;
import eu.jangos.auth.model.Roles;
import eu.jangos.auth.model.Servers;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;

/**
 * Utils provide several convenience features.
 *
 * @author Warkdev
 */
public class Utils {

    public static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    /**
     * Indicates whether the IP provided in parameter is a valid IP or not.
     *
     * @param ip A string representation of the IP to be checked. Must be
     * xxx.xxx.xxx.xxx format.
     * @return True if the IP is a valid address, false otherwise.
     */
    public static boolean isValidIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return ip.matches(IPV4_PATTERN.pattern());
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static AccountDTO getAccountDTO(Account account) {
        if (account == null) {
            return null;
        }

        AccountDTO accountDTO = new AccountDTO();

        accountDTO.setId(account.getId());
        accountDTO.setName(account.getName());
        accountDTO.setCreation(account.getCreation());
        accountDTO.setEmail(account.getEmail());
        accountDTO.setFailedattempt(account.getFailedattempt());

        Collection<RolesDTO> rolesCollection = new ArrayList<>();

        account.getRolesCollection().stream().forEach((r) -> {
            rolesCollection.add(getRolesDTO(r));
        });

        accountDTO.setRolesCollection(rolesCollection);

        accountDTO.setFkLocale(getLocaleDTO(account.getFkLocale()));
        accountDTO.setHashPass(account.getHashPass());
        accountDTO.setLastIP(account.getLastIP());
        accountDTO.setLastlogin(account.getLastlogin());
        accountDTO.setLocked(account.getLocked());
        accountDTO.setSalt(account.getSalt());
        accountDTO.setToken(account.getToken());
        accountDTO.setSessionkey(account.getSessionkey());
        accountDTO.setVerifier(account.getVerifier());
        accountDTO.setFkPrefrealm(getRealmDTO(account.getFkPrefrealm()));

        return accountDTO;
    }

    public static RolesDTO getRolesDTO(Roles role) {
        if (role == null) {
            return null;
        }

        RolesDTO roleDTO = new RolesDTO();

        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());

        Collection<CommandsDTO> commandsCollection = new ArrayList<>();

        role.getCommandsCollection().stream().forEach((c) -> {
            commandsCollection.add(getCommandsDTO(c));
        });

        roleDTO.setCommandsCollection(commandsCollection);

        return roleDTO;
    }

    public static CommandsDTO getCommandsDTO(Commands command) {
        if (command == null) {
            return null;
        }

        CommandsDTO commandDTO = new CommandsDTO();

        commandDTO.setId(command.getId());
        commandDTO.setName(command.getName());
        commandDTO.setDescription(command.getDescription());

        return commandDTO;
    }

    public static LocaleDTO getLocaleDTO(Locale locale) {
        if (locale == null) {
            return null;
        }
        LocaleDTO localeDTO = new LocaleDTO();

        localeDTO.setId(locale.getId());
        localeDTO.setLocale(locale.getLocale());
        localeDTO.setLocaleString(locale.getLocaleString());

        return localeDTO;
    }

    public static RealmDTO getRealmDTO(Realm realm) {
        if (realm == null) {
            return null;
        }

        RealmDTO realmDTO = new RealmDTO();

        realmDTO.setAddress(realm.getAddress());
        realmDTO.setName(realm.getName());
        realmDTO.setCountPlayers(realm.getCountPlayers());
        realmDTO.setId(realm.getId());
        realmDTO.setMaxPlayers(realm.getMaxPlayers());
        realmDTO.setName(realm.getName());
        realmDTO.setPopulation(realm.getPopulation());
        realmDTO.setPort(realm.getPort());

        return realmDTO;
    }

    public static RealmTypeDTO getRealmTypeDTO(RealmType type) {
        if (type == null) {
            return null;
        }
        RealmTypeDTO typeDTO = new RealmTypeDTO();

        typeDTO.setId(type.getId());
        typeDTO.setType(type.getType());
        Collection<RealmDTO> listRealms = new ArrayList<>();
        type.getRealmCollection().stream().forEach((r) -> {
            listRealms.add(Utils.getRealmDTO(r));
        });
        typeDTO.setRealmCollection(listRealms);

        return typeDTO;
    }

    public static RealmTimeZoneDTO getRealmTimeZoneDTO(RealmTimeZone zone) {
        if (zone == null) {
            return null;
        }
        RealmTimeZoneDTO zoneDTO = new RealmTimeZoneDTO();

        zoneDTO.setId(zone.getId());
        zoneDTO.setName(zone.getName());
        Collection<RealmDTO> listRealms = new ArrayList<>();
        zone.getRealmCollection().stream().forEach((r) -> {
            listRealms.add(Utils.getRealmDTO(r));
        });
        zoneDTO.setRealmCollection(listRealms);

        return zoneDTO;
    }

    public static BannedAccountDTO getBannedAccountDTO(BannedAccount bannedAccount) {
        if (bannedAccount == null) {
            return null;
        }

        BannedAccountDTO account = new BannedAccountDTO();

        account.setId(bannedAccount.getId());
        account.setActive(bannedAccount.getActive());
        account.setBanDate(bannedAccount.getBanDate());
        account.setReason(bannedAccount.getReason());
        account.setUnban(bannedAccount.getUnban());

        return account;
    }

    public static BannedIPDTO getBannedIPDTO(BannedIP bannedIP) {
        if (bannedIP == null) {
            return null;
        }

        BannedIPDTO ip = new BannedIPDTO();

        ip.setId(bannedIP.getId());
        ip.setDate(bannedIP.getDate());
        ip.setIp(bannedIP.getIp());
        ip.setActive(bannedIP.getActive());
        ip.setReason(bannedIP.getReason());
        ip.setUnban(bannedIP.getUnban());
        ip.setFkBannedBy(getAccountDTO(bannedIP.getFkBannedBy()));

        return ip;
    }

    public static AuthParameterDTO getParameterDTO(Parameter1 parameter) {
        if (parameter == null) {
            return null;
        }

        AuthParameterDTO parameterDTO = new AuthParameterDTO();

        parameterDTO.setId(parameter.getId());
        parameterDTO.setParam(parameter.getParam());
        parameterDTO.setVal(parameter.getVal());

        return parameterDTO;
    }

    public static ServersDTO getServersDTO(Servers server) {
        if (server == null) {
            return null;
        }

        ServersDTO serverDTO = new ServersDTO();

        serverDTO.setId(server.getId());
        serverDTO.setName(server.getName());
        serverDTO.setKey(server.getKey());
        serverDTO.setRevoked(server.isRevoked());

        Collection<RolesDTO> rolesCollection = new ArrayList<>();

        server.getRolesCollection().stream().forEach((r) -> {
            rolesCollection.add(getRolesDTO(r));
        });

        serverDTO.setRolesCollection(rolesCollection);

        return serverDTO;
    }

    /**
     * Check that an authenticated user has the rights to execute this method.
     *
     * @param permission The permission string to be checked.
     * @return A forbidden response if it does not have the rights, null
     * otherwise.
     */
    public static Response checkPermission(String permission) {
        if (!SecurityUtils.getSubject().isPermitted(permission)) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.FORBIDDEN.getStatusCode(),
                    404,
                    "You don't have the permission to execute this operation",
                    "",
                    ""));
        }

        return null;
    }
}

package de.hhu.cs.dbs.propra.presentation.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class AdressenController {

    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("adressen")
    @GET
    public Response getAdresse(@QueryParam("hausnummer") String Hausnummer,
                               @QueryParam("strasse") String Strasse,
                               @QueryParam("plz")  String PLZ,
                               @QueryParam("stadt") String Stadt) {

        try (Connection connection = dataSource.getConnection()) {

            String sql = "SELECT [ADRESSE].[ROWID] AS [adresseid], [hausnummer], [strasse], [plz], [stadt] \n" +
                    "FROM [ADRESSE] \n" +
                    "WHERE [hausnummer] LIKE ifnull('%' || ? || '%', [hausnummer]) " +
                    "AND [strasse] LIKE ifnull('%' || ? || '%', [strasse]) " +
                    "AND [plz] LIKE ifnull('%' || ? || '%', [plz]) " +
                    "AND [stadt] LIKE ifnull('%' || ? || '%', [stadt]);";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setString(1, Hausnummer);
            preparedStatement.setString(2, Strasse);
            preparedStatement.setString(3, PLZ);
            preparedStatement.setString(4, Stadt);
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData resultSetMD = resultSet.getMetaData();
            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                for (int i = 1; i <= 5; i++) {
                    entity.put(resultSetMD.getColumnName(i), resultSet.getObject(i));
                }
                entities.add(entity);
            }
            resultSet.close();

            return Response.status(Response.Status.OK).entity(entities).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @Path("adressen")
    @RolesAllowed({"KUNDE"})
    @PATCH
    public Response patchAdresse(@QueryParam("hausnummer") String Hausnummer,
                                 @QueryParam("strasse") String Strasse,
                                 @QueryParam("stadt") String Stadt,
                                 @QueryParam("plz") String PLZ) {


        String username = securityContext.getUserPrincipal().getName();

        try (Connection connection = dataSource.getConnection()){

            String sql_check = "SELECT COUNT(*) " +
                    "FROM [ADRESSE] " +
                    "WHERE [ID] = (SELECT [AdresseID] FROM KUNDE WHERE [email] = ?)";
            PreparedStatement preparedStatement_check = connection.prepareStatement(sql_check);
            preparedStatement_check.closeOnCompletion();
            preparedStatement_check.setString(1, username);
            ResultSet resultSet = preparedStatement_check.executeQuery();

            if (resultSet.getInt(1) < 1) {
                return Response.status(Response.Status.NOT_FOUND).entity(new SQLException("Keine Adresse unter dieser ID vorhanden")).build();
            }

            String sql = "UPDATE [ADRESSE] \n" +
                    "SET [hausnummer] = ifnull(?,[hausnummer]), [strasse] = ifnull(?,[strasse]), [stadt] = ifnull(?,[stadt]), [plz] = ifnull(?,[plz]) \n" +
                    "WHERE [ID] = (SELECT [AdresseID] FROM KUNDE WHERE [email] = ?);";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setString(1, Hausnummer);
            preparedStatement.setString(2, Strasse);
            preparedStatement.setString(3, Stadt);
            preparedStatement.setString(4, PLZ);
            preparedStatement.setString(5, username);
            preparedStatement.executeUpdate();

            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException e) {
            try {
                if (e.getErrorCode() == 19 && e.getMessage().contains("Diese Adresse ist mehr als einem Kunden zugewiesen und kann nicht geaendert werden.")) {
                    return patchAdresse_19_addNew(Hausnummer, Strasse, Stadt, PLZ, username);
                }
                if (e.getErrorCode() == 19 && e.getMessage().contains("UNIQUE constraint failed")) {
                    return patchAdresse_19_changeID(Hausnummer, Strasse, Stadt, PLZ, username);
                }
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            } catch (SQLException e2) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            }
        }
    }
        //curl -u Jojo@Allstar.net:IsThis4JoJoReference -X PATCH "http://localhost:8080/adressen?hausnummer=1&strasse=klabusterweg&stadt=indahood&plz=88764"


    private Response patchAdresse_19_changeID(String Hausnummer, String Strasse, String Stadt, String PLZ, String username) throws SQLException{
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);

        String sql = "SELECT a2.[ID] AS [ID] " +
                "FROM [ADRESSE] a2 CROSS JOIN [ADRESSE] a1 INNER JOIN [KUNDE] k ON a1.[ID] = k.[AdresseID] WHERE " +
                "(a2.[hausnummer], a2.[strasse], a2.[stadt], a2.[plz]) = " +
                "(ifnull(?,a1.[hausnummer]), ifnull(?,a1.[strasse]), ifnull(?, a1.[stadt]), ifnull(?, a1.[plz])) " +
                "AND k.[email] = ?;";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.closeOnCompletion();
        preparedStatement.setString(1, Hausnummer);
        preparedStatement.setString(2, Strasse);
        preparedStatement.setString(3, Stadt);
        preparedStatement.setString(4, PLZ);
        preparedStatement.setString(5, username);

            ResultSet resultSet = preparedStatement.executeQuery();


        int AdresseID = resultSet.getInt(resultSet.findColumn("ID"));

        String sql_give_new_address = "UPDATE [KUNDE] SET [AdresseID] = ? WHERE [email] = ?;";
        preparedStatement = connection.prepareStatement(sql_give_new_address);
        preparedStatement.closeOnCompletion();
        preparedStatement.setInt(1, AdresseID);
        preparedStatement.setString(2, username);

        try {
            preparedStatement.executeUpdate();
        } catch (SQLException er) {
            connection.rollback();
            connection.close();
            throw(er);
        }
        connection.commit();
        connection.close();
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private Response patchAdresse_19_addNew(String Hausnummer, String Strasse, String Stadt, String PLZ, String username) throws SQLException{
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        String sql = "INSERT INTO [ADRESSE] ([hausnummer], [strasse], [stadt], [plz]) \n " +
                "SELECT ifnull(?,[hausnummer]), ifnull(?,[strasse]), ifnull(?, [stadt]), ifnull(?, [plz]) FROM [ADRESSE] INNER JOIN [KUNDE] ON [ADRESSE].[ID] = [KUNDE].[AdresseID] WHERE [email] = ?;";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.closeOnCompletion();
        preparedStatement.setString(1, Hausnummer);
        preparedStatement.setString(2, Strasse);
        preparedStatement.setString(3, Stadt);
        preparedStatement.setString(4, PLZ);
        preparedStatement.setString(5, username);
        try {
            preparedStatement.executeUpdate();
        } catch (SQLException er) {
            connection.rollback();
            connection.close();
            if(er.getErrorCode() == 19 && er.getMessage().contains("UNIQUE constraint failed")) {
                return patchAdresse_19_changeID(Hausnummer, Strasse, Stadt, PLZ, username);
            }
            throw(er);
        }
        int AdresseID = preparedStatement.getGeneratedKeys().getInt(1);

        String sql_give_new_address = "UPDATE [KUNDE] SET [AdresseID] = (SELECT [ID] FROM [ADRESSE] WHERE ROWID = ?) WHERE [email] = ?;";
        preparedStatement = connection.prepareStatement(sql_give_new_address);
        preparedStatement.closeOnCompletion();
        preparedStatement.setInt(1, AdresseID);
        preparedStatement.setString(2, username);

        try {
            preparedStatement.executeUpdate();
        } catch (SQLException er) {
            connection.rollback();
            connection.close();
            throw(er);
        }
        connection.commit();
        connection.close();
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}

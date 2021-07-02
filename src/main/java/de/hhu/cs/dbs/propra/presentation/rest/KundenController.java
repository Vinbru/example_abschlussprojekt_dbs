package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

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

public class KundenController {

    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("kunden")
    @GET
    public Response getKunden(@QueryParam("beitragsbefreit") @DefaultValue("") Boolean Beitragsbefreit,
                              @QueryParam("guthaben") @DefaultValue("0.00") double Guthaben) {

        try (Connection connection = dataSource.getConnection()) {

            String sql =    "SELECT [KUNDE].[ROWID] AS [kundenid], " +
                            "[guthaben], " +
                            "[beitragsbefreit], " +
                            "[KUNDE].[email], " +
                            "[passwort], " +
                            "[vorname], " +
                            "[nachname], " +
                            "DATE([geburtsdatum]) AS [geburtsdatum] \n" +
                            "FROM [KUNDE] NATURAL JOIN [NUTZER] \n" +
                            "WHERE [guthaben] >= ? AND [beitragsbefreit] = ifnull(?,[beitragsbefreit]);";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setDouble(1, Guthaben);
            try { preparedStatement.setBoolean(1, Beitragsbefreit);}catch(Exception e){}
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData resultSetMD = resultSet.getMetaData();
            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                for(int i = 1; i <= 8; i++) {
                    if(resultSetMD.getColumnName(i).equals("beitragsbefreit"))
                    {
                        entity.put(resultSetMD.getColumnName(i), resultSet.getObject(i).equals(1));
                    }
                    else entity.put(resultSetMD.getColumnName(i), resultSet.getObject(i));
                }
                entities.add(entity);
            }
            resultSet.close();

            return Response.status(Response.Status.OK).entity(entities).build();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @Path("kunden")
    @POST
    public Response postKunde(@FormDataParam("email") String Email,
                              @FormDataParam("passwort") String Passwort,
                              @FormDataParam("vorname") String Vorname,
                              @FormDataParam("nachname") String Nachname,
                              @FormDataParam("geburtsdatum") String Geburtsdatum,
                              @FormDataParam("guthaben") Double Guthaben,
                              @FormDataParam("beitragsbefreit") Boolean Beitragsbefreit,
                              @FormDataParam("adresseid") Integer Adresseid) {

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            String sql_check = "SELECT COUNT(*) FROM [NUTZER] " +
                    "WHERE [email] = ? AND [passwort] = ? AND [vorname] = ? AND [nachname] = ? AND [geburtsdatum] = datetime(?)";
            PreparedStatement preparedStatement_check = connection.prepareStatement(sql_check);
            preparedStatement_check.closeOnCompletion();
            preparedStatement_check.setString(1, Email);
            preparedStatement_check.setString(2, Passwort);
            preparedStatement_check.setString(3, Vorname);
            preparedStatement_check.setString(4, Nachname);
            preparedStatement_check.setString(5, Geburtsdatum);
            ResultSet resultSet = preparedStatement_check.executeQuery();

            if (resultSet.getInt(1) == 0) {
                String sql = "INSERT INTO [NUTZER] ([email], [passwort], [vorname], [nachname], [geburtsdatum]) " +
                        "VALUES (?, ?, ?, ?, datetime(?)) ;";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.closeOnCompletion();
                preparedStatement.setString(1, Email);
                preparedStatement.setString(2, Passwort);
                preparedStatement.setString(3, Vorname);
                preparedStatement.setString(4, Nachname);
                preparedStatement.setString(5, Geburtsdatum);

                try {
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    connection.rollback();
                    System.err.println(e.getMessage());
                    return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
                }
            }

            String sql2 =   "INSERT INTO [KUNDE] ([email], [guthaben], [beitragsbefreit], [AdresseID])" +
                            "VALUES (?, ?, ?, (SELECT [ID] FROM ADRESSE WHERE [ROWID] = ?));";


            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement2.closeOnCompletion();
            preparedStatement2.setString(1, Email);
            try{preparedStatement2.setDouble(2, Guthaben);}catch(Exception e){}
            try{preparedStatement2.setBoolean(3, Beitragsbefreit);}catch(Exception e){}
            try{preparedStatement2.setInt(4, Adresseid);} catch (Exception e){}


            try {preparedStatement2.executeUpdate();
            } catch (SQLException e){
                connection.rollback();
                System.err.println(e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            }

            connection.commit();

            String rowID = preparedStatement2.getGeneratedKeys().getString(1);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(rowID).build()).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }

        //curl -u Jojo@Allstar.net:IsThis4JoJoReference -X POST http://localhost:8080/kunden -F "vorname=muster" -F "nachname=mann" -F "geburtsdatum=2021-03-17" -F "guthaben=24.00" -F "beitragsbefreit=false" -F "adresseid=2" -F "email=momo@moes.de" -F "passwort=Passw0rt" -v
    }
}

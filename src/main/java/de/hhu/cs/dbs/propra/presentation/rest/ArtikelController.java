package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class ArtikelController {

    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("artikel")
    @GET
    public Response getArtikel(@QueryParam("isbn") String ISBN,
                              @QueryParam("bezeichnung") String Bezeichnung,
                              @QueryParam("beschreibung") String Beschreibung,
                              @QueryParam("coverbild") String Coverbild,
                              @QueryParam("erscheinungsdatum") String Erscheinungsdatum) {

        try (Connection connection = dataSource.getConnection()) {

            String sql =    "SELECT [ARTIKEL].[ROWID] AS [artikelid], " +
                            "[ARTIKEL].isbn, " +
                            "DATE([erscheinungsdatum]) AS [erscheinungsdatum], " +
                            "[beschreibung], [bezeichnung], " +
                            "HEX([coverbild]) as [coverbild]" +
                            //"CASE [coverbild] " +
                            //"WHEN [coverbild] LIKE ? THEN [coverbild] ELSE ? END [coverbild]\n" +
                            "FROM [ARTIKEL] LEFT JOIN [COVERBILD] ON [ARTIKEL].[isbn] = [COVERBILD].[isbn] \n" +
                            "WHERE [ARTIKEL].[isbn] LIKE ifnull('%' || ? || '%', [ARTIKEL].[isbn]) " +
                            "AND [bezeichnung] LIKE ifnull('%' || ? || '%', [bezeichnung]) " +
                            "AND [beschreibung] LIKE ifnull('%' || ? || '%', [beschreibung]) " +
                            "AND [erscheinungsdatum] >= datetime(ifnull(?, [erscheinungsdatum])) " +
                            "AND HEX([coverbild]) like '%' || HEX(?) || '%' ;";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setString(1, ISBN);
            preparedStatement.setString(2, Bezeichnung);
            preparedStatement.setString(3, Beschreibung);
            preparedStatement.setString(4, Erscheinungsdatum);
            preparedStatement.setString(5, Coverbild);
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData resultSetMD = resultSet.getMetaData();
            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                for(int i = 1; i <= 6; i++) {
                    entity.put(resultSetMD.getColumnName(i), resultSet.getObject(i));
                }
                entities.add(entity);
            }
            resultSet.close();
            connection.close();

            return Response.status(Response.Status.OK).entity(entities).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @Path("artikel")
    @RolesAllowed({"BIBLIOTHEKAR"})
    @POST
    public Response postArtikel(@FormDataParam("autorid") Integer AutorID,
                                @FormDataParam("genreid") Integer GenreID,
                                @FormDataParam("mediumid") Integer MediumID,
                                @FormDataParam("isbn") String ISBN,
                                @FormDataParam("erscheinungsdatum") String Erscheinungsdatum,
                                @FormDataParam("beschreibung") String Beschreibung,
                                @FormDataParam("bezeichnung") String Bezeichnung,
                                @FormDataParam("coverbild") InputStream Coverbild
                                //@FormDataParam("coverbild") String Coverbild
    ) {

        try (Connection connection = dataSource.getConnection()){

            connection.setAutoCommit(false);

            String sql = "INSERT INTO [ARTIKEL] ([isbn], [beschreibung], [bezeichnung], [erscheinungsdatum], [art]) " +
                        "VALUES (?, ?, ?, datetime(?), (SELECT [art] FROM [MEDIUM] where ROWID = ?));";


            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setString(1, ISBN);
            preparedStatement.setString(2, Beschreibung);
            preparedStatement.setString(3, Bezeichnung);
            preparedStatement.setString(4, Erscheinungsdatum);
            try{preparedStatement.setInt(5, MediumID);}catch(Exception e){}

            try {preparedStatement.executeUpdate();
            } catch (SQLException e){
                connection.rollback();
                System.err.println(e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            }

            String sql2 = "INSERT INTO [ARTIKEL_GEHOERT_ZU_GENRE] ([isbn], [Genre Name]) " +
                            "VALUES (?, (SELECT [bezeichnung] FROM [GENRE] where ROWID = ?));";


            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement2.closeOnCompletion();
            preparedStatement2.setString(1, ISBN);
            try{preparedStatement2.setInt(2, GenreID);}catch(Exception e){}

            try {preparedStatement2.executeUpdate();
            } catch (SQLException e){
                connection.rollback();
                System.err.println(e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            }

            String sql3 = "INSERT INTO [AUTOR_VERFASST_ARTIKEL] ([isbn], [AutorID]) " +
                    "VALUES (?, (SELECT [ID] FROM [AUTOR] where ROWID = ?));";


            PreparedStatement preparedStatement3 = connection.prepareStatement(sql3);
            preparedStatement3.closeOnCompletion();
            preparedStatement3.setString(1, ISBN);
            try{preparedStatement3.setInt(2, AutorID);}catch(Exception e){}

            try {preparedStatement3.executeUpdate();
            } catch (SQLException e){
                connection.rollback();
                System.err.println(e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            }

            if (Coverbild != null)
            {
            String sql4 = "INSERT INTO [COVERBILD] ([isbn], [coverbild]) VALUES (?, ?);";

            //Coverbild = new FileInputStream(System.getProperty("user.dir")+"\\images\\978-3837070071_image.png");

            PreparedStatement preparedStatement4 = connection.prepareStatement(sql4);
            preparedStatement4.closeOnCompletion();
            preparedStatement4.setString(1, ISBN);
            preparedStatement4.setBytes(2,Coverbild.readAllBytes());
            //preparedStatement4.setBytes(2,Coverbild.getBytes(StandardCharsets.US_ASCII));

            try {preparedStatement4.executeUpdate();
            } catch (SQLException e){
                connection.rollback();
                System.err.println(e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            }
            }

            connection.commit();

            String rowID = preparedStatement.getGeneratedKeys().getString(1);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(rowID).build()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        } catch (FileNotFoundException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }

//curl -u NineToFiveNancy@UniBib.de:3ndlichFreitag -X POST http://localhost:8080/artikel -F "autorid=1" -F "genreid=2" -F "mediumid=3" -F "isbn=333" -F "erscheinungsdatum=2000-01-02" -F "beschreibung=besch" -F "bezeichnung=bez" -v
    }

    @Path("artikel/{artikelid}")
    @RolesAllowed("BIBLIOTHEKAR")
    @DELETE
    public Response deleteExemplar(@PathParam("artikelid") Integer ArtikelID) {


        try (Connection connection = dataSource.getConnection()) {
            String sql_check = "SELECT COUNT(*) FROM [ARTIKEL] WHERE ROWID = ?";
            PreparedStatement preparedStatement_check = connection.prepareStatement(sql_check);
            preparedStatement_check.closeOnCompletion();
            try{preparedStatement_check.setInt(1, ArtikelID);}catch(Exception e){}
            ResultSet resultSet = preparedStatement_check.executeQuery();

            if (resultSet.getInt(1) < 1) {
                return Response.status(Response.Status.NOT_FOUND).entity(new SQLException("Kein Artikel unter dieser ID vorhanden")).build();
            }

            String sql = "DELETE FROM [ARTIKEL] WHERE ROWID = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            try{preparedStatement.setInt(1, ArtikelID);}catch(Exception e){}
            preparedStatement.executeUpdate();
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
    //curl -u NineToFiveNancy@UniBib.de:3ndlichFreitag -X DELETE http://localhost:8080/artikel/20

}

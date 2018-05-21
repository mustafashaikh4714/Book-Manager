package boookaager.database;

import boookmanager.listbook.BooklistController.Book;
import boookmanager.listmembers.ListmemberController.Member;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author mustafa
 */
public final class DatabaseHandler {

    private static DatabaseHandler handler = null;
    String path= System.getenv("APPDATA");
   // String path= System.getProperty("user.home");
    private final String DB_URL = "jdbc:derby:"+path+"/database;create=true";
    private Connection conn = null;
    private Statement stmt = null;

    private DatabaseHandler() {
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();

    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    void createConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cant load database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    //setup book table
    void setupBookTable() {
        String TABLE_NAME = "BOOK";
        try {
            stmt = conn.createStatement();

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {
//              String delete="DROP TABLE IF EXISTS BOOK";
//              stmt.executeUpdate(delete);
//              System.out.println("your table is drop");
                System.out.println("Table " + TABLE_NAME + "already exists. Ready for go!");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "	id varchar(200) primary key,\n"
                        + "	title varchar(200),\n"
                        + "	author varchar(200),\n"
                        + "	publisher varchar(100),\n"
                        + "	isAvail boolean default true"
                        + " )");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupDatabase");
        } finally {
        }
    }

    //setup member table

    void setupMemberTable() {
        String TABLE_NAME = "MEMBER";
        try {
            stmt = conn.createStatement();

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {
//                   String delete="DROP TABLE IF EXISTS MEMBER";
//              stmt.executeUpdate(delete);
//             System.out.println("your table is drop");
                System.out.println("Table " + TABLE_NAME + "already exists. Ready for go!");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "	id varchar(200) primary key,\n"
                        + "	name varchar(200),\n"
                        + "	mobile varchar(20),\n"
                        + "	email varchar(100)\n"
                        + " )");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupDatabase");
        } finally {
        }
    }

    //setup issue table
    void setupIssueTable() {
        String TABLE_NAME = "ISSUE";
        try {

            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();

            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
//                   String delete="DROP TABLE IF EXISTS ISSUE";
//              stmt.executeUpdate(delete);
//              System.out.println("your table is drop");
                System.out.println("Table " + TABLE_NAME + "already exists. Ready for go!");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "     bookID varchar(200) primary key,\n"
                        + "	memberID varchar(200),\n"
                        + "	issueTime timestamp default CURRENT_TIMESTAMP,\n"
                        + "	renew_count integer default 0,\n"
                        + "	FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                        + "	FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                        + " )");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupDatabase");
        } finally {
        }
    }

    //Delete Member.

    public boolean deleteMember(Member member) {
        try {
            String deleteStatement = "DELETE FROM MEMBER WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, member.getId());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //resultset
    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        } finally {
        }
        return result;
    }

    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(qu);                 //error
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        } finally {
        }
    }
               //Delete Book.

    public boolean deleteBook(Book book) {
        try {
            String deleteStatement = "DELETE FROM BOOK WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, book.getId());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateBook(Book book) throws SQLException {
        String update = "UPDATE BOOK SET title=?, publisher=?,author=? WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(update);
        stmt.setString(1, book.getTitle());
        stmt.setString(2, book.getPublisher());
        stmt.setString(3, book.getAuthor());
        stmt.setString(4, book.getId());
        int res = stmt.executeUpdate();
        if (res > 0) {
            return true;
        } else {
            return false;
        }

    }

    public boolean updateMember(Member member) throws SQLException {
        String update = "UPDATE MEMBER SET name=?, mobile=?,email=? WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(update);
        stmt.setString(1, member.getName());
        stmt.setString(2, member.getMobile());
        stmt.setString(3, member.getEmail());
        stmt.setString(4, member.getId());
        int res = stmt.executeUpdate();
        if (res > 0) {
            return true;
        } else {
            return false;
        }

    }
}

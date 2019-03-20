
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqliteHelper {
    final static Logger logger = LoggerFactory.getLogger(SqliteHelper.class);
    
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private String dbFilePath;
    
    public SqliteHelper(String dbFilePath) throws ClassNotFoundException, SQLException {
        this.dbFilePath = dbFilePath;
        connection = getConnection(dbFilePath);
    }
    
    public Connection getConnection(String dbFilePath) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        return conn;
    }
    public Map<String, Map<String, String>> getCourseInfo() throws ClassNotFoundException, SQLException{
        try {
            resultSet = getStatement().executeQuery("select * from course_m3u8_info");
            Map<String, Map<String, String>> rets = new HashMap<String,Map<String, String>>();
            String[] column_names = {"chapter_title","course_title","section_id","section_index","userId","course_name"};
            try {
    			while(resultSet.next()) {
    				Map<String,String> tmp_map = new HashMap<String,String>();
    				for (String column_name : column_names) {
    					tmp_map.put(column_name, resultSet.getString(column_name));
					}
    				rets.put(resultSet.getString("section_id"), tmp_map);
    			}
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
            return rets;
        } finally {
            destroyed();
        }
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        if (null == connection) connection = getConnection(dbFilePath);
        return connection;
    }
    
    private Statement getStatement() throws SQLException, ClassNotFoundException {
        if (null == statement) statement = getConnection().createStatement();
        return statement;
    }
    
    public void destroyed() {
        try {
            if (null != connection) {
                connection.close();
                connection = null;
            }
            
            if (null != statement) {
                statement.close();
                statement = null;
            }
            
            if (null != resultSet) {
                resultSet.close();
                resultSet = null;
            }
        } catch (SQLException e) {
            logger.error("Sqlite数据库关闭时异常", e);
        }
    }
}
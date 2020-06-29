
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDefaultJTableDAO {
    Connection con;
    Statement st;
    PreparedStatement ps;
    ResultSet rs;

    String server = "localhost"; // MySQL 서버 주소
    String database = "management"; // MySQL DATABASE 이름
    String user_name = "root"; //  MySQL 서버 아이디
    String password = "sangho123"; // MySQL 서버 비밀번호



    public UserDefaultJTableDAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println(" !! <JDBC 오류> Driver load 오류: " + e.getMessage());
            e.printStackTrace();
        }

        // 2.연결
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database + "?useSSL=false", user_name, password);
            System.out.println("정상적으로 연결되었습니다.");
        } catch (SQLException e) {
            System.err.println("con 오류:" + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * DB닫기 기능 메소드
     * */
    public void dbClose() {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (ps != null) ps.close();
        } catch (Exception e) {
            System.out.println(e + "=> dbClose fail");
        }
    }//dbClose() ---

    /**
     * 인수로 들어온 ID에 해당하는 레코드 검색하여 중복여부 체크하기 리턴값이 true =사용가능 , false = 중복임
     * */
    public boolean getIdByCheck(String id) {
        boolean result = true;

        try {
            ps = con.prepareStatement("SELECT * FROM user WHERE name=?");
            ps.setString(1, id.trim());
            rs = ps.executeQuery(); //실행
            if (rs.next())
                result = false; //레코드가 존재하면 false

        } catch (SQLException e) {
            System.out.println(e + "=>  getIdByCheck fail");
        } finally {
            dbClose();
        }

        return result;

    }//getIdByCheck()


    /**
     * 유저 삽입
     * */
    public int userListInsert(UserJDailogGUI user) {
        int result = 0;
        try {
            ps = con.prepareStatement("insert into user values(?,?,?)");
            ps.setString(1, user.email.getText());
            ps.setString(2, user.password.getText());
            ps.setString(3, user.name.getText());

            result = ps.executeUpdate(); //실행 -> 저장

        } catch (SQLException e) {
            System.out.println(e + "=> userListInsert fail");
        } finally {
            dbClose();
        }

        return result;

   }//userListInsert()

    /**
     * user 조회
     * */
    public void userSelectAll(DefaultTableModel t_model) {
        try {
            st = con.createStatement();
            rs = st.executeQuery("select * from user");

            // DefaultTableModel에 있는 기존 데이터 지우기
            for (int i = 0; i < t_model.getRowCount();) {
                t_model.removeRow(0);
            }

            while (rs.next()) {
                Object data[] = { rs.getString(1), rs.getString(2),
                        rs.getString(3) };

                t_model.addRow(data); //DefaultTableModel에 레코드 추가
            }

        } catch (SQLException e) {
            System.out.println(e + "=> userSelectAll fail");
        } finally {
            dbClose();
        }
    }//userSelectAll()

    /**
     * name에 해당하는 유저 삭제하기
     * */
    public int userDelete(String id) {
        int result = 0;
        try {
            ps = con.prepareStatement("delete from user where email = ? ");
            ps.setString(1, id.trim());
            result = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e + "=> userDelete fail");
        }finally {
            dbClose();
        }

        return result;
    }//userDelete()

    /**
     * name에 해당하는 유저 수정하기
     * */
    public int userUpdate(UserJDailogGUI user) {
        int result = 0;
        String sql = "UPDATE user SET email=?, password=?, name=? WHERE email=email";

        try {
            ps = con.prepareStatement(sql);
            // ?의 순서대로 값 넣기
            ps.setString(1, user.email.getText());
            ps.setString(2, user.password.getText());
            ps.setString(3, user.name.getText());



            // 실행하기
            result = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e + "=> userUpdate fail");
        } finally {
            dbClose();
        }

        return result;
    }//userUpdate()
}

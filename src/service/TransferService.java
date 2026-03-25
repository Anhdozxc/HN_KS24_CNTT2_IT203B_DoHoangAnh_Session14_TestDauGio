package service;

import utils.DBConnection;
import java.sql.*;

public class TransferService {

    public static void transfer(String fromId, String toId, double amount) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // bat dau transaction

            // 1. Kiem tra so du
            String checkSql = "SELECT Balance FROM Accounts WHERE AccountId = ?";
            PreparedStatement ps = conn.prepareStatement(checkSql);
            ps.setString(1, fromId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new Exception("Tai khoan gui khong ton tai");
            }

            double balance = rs.getDouble("Balance");

            if (balance < amount) {
                throw new Exception("Khong du so du");
            }

            // 2. Goi procedure tru tien
            CallableStatement cs1 = conn.prepareCall("{CALL sp_UpdateBalance(?, ?)}");
            cs1.setString(1, fromId);
            cs1.setDouble(2, -amount);
            cs1.execute();

            // 3. Goi procedure cong tien
            CallableStatement cs2 = conn.prepareCall("{CALL sp_UpdateBalance(?, ?)}");
            cs2.setString(1, toId);
            cs2.setDouble(2, amount);
            cs2.execute();

            // 4. commit
            conn.commit();
            System.out.println("Chuyen tien thanh cong!");

            // 5. Hien thi ket qua
            String resultSql = "SELECT * FROM Accounts WHERE AccountId IN (?, ?)";
            PreparedStatement ps2 = conn.prepareStatement(resultSql);
            ps2.setString(1, fromId);
            ps2.setString(2, toId);

            ResultSet rs2 = ps2.executeQuery();

            System.out.println("=== Ket qua sau khi chuyen ===");
            while (rs2.next()) {
                System.out.println(
                        rs2.getString("AccountId") + " - " +
                                rs2.getString("FullName") + " - " +
                                rs2.getDouble("Balance")
                );
            }

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // rollback neu loi
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Loi: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
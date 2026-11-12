package bac.ac.libis.dao;

import bac.ac.libis.config.DatabaseConfig;
import bac.ac.libis.model.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class MemberDaoImpl implements MemberDao {
    private static final String SELECT_MEMBERS = """
            SELECT member_id, member_number, username, first_name, last_name,
                   email, phone_number, role
            FROM members
            """;

    @Override
    public Member findByUsername(String username) throws SQLException {
        return findOne(SELECT_MEMBERS + " WHERE username = ?", username);
    }

    @Override
    public Member findById(int memberId) throws SQLException {
        return findOne(SELECT_MEMBERS + " WHERE member_id = ?", memberId);
    }

    @Override
    public Member findByEmail(String email) throws SQLException {
        return findOne(SELECT_MEMBERS + " WHERE email = ?", email);
    }

    @Override
    public Member findByMemberNumber(String memberNumber) throws SQLException {
        return findOne(SELECT_MEMBERS + " WHERE member_number = ?", memberNumber);
    }

    private Member findOne(String sql, Object value) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setObject(1, value);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapMember(rs) : null;
            }
        }
    }

    @Override
    public List<Member> findAll() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(SELECT_MEMBERS);
             ResultSet rs = pst.executeQuery()) {
            List<Member> members = new ArrayList<>();
            while (rs.next()) {
                members.add(mapMember(rs));
            }
            return members;
        }
    }

    @Override
    public boolean registerMember(Member member) throws SQLException {

        String sql = """
            INSERT INTO members
            (member_number, username, first_name, last_name,
             email, phone_number, password, role)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, member.getMember_No());
            statement.setString(2, member.getUsername());
            statement.setString(3, member.getFirstName());
            statement.setString(4, member.getLastName());
            statement.setString(5, member.getEmail());
            statement.setString(6, member.getPhoneNumber());
            statement.setString(7, member.getPassword());
            statement.setString(8, "member");

            int rowsAffected = statement.executeUpdate();

            return rowsAffected == 1;
        }
    }

    @Override
    public List<Member> search(String term, String filterBy) throws SQLException {
        if (term == null || term.isBlank()) {
            return findAll();
        }
        // Only fixed column names may be included in the SQL.
        String predicate = switch (filterBy == null ? "" : filterBy) {
            case "name" -> "CONCAT(first_name, ' ', last_name) LIKE ?";
            case "username" -> "username LIKE ?";
            case "email" -> "email LIKE ?";
            case "firstName" -> "first_name LIKE ?";
            case "lastName" -> "last_name LIKE ?";
            case "memberNumber", "member_number" -> "member_number LIKE ?";
            default -> "username LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR member_number LIKE ? OR CONCAT(first_name, ' ', last_name) LIKE ?";
        };
        int parameterCount = predicate.contains(" OR ") ? 6 : 1;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(SELECT_MEMBERS + " WHERE " + predicate)) {
            String pattern = "%" + term.trim() + "%";
            for (int i = 1; i <= parameterCount; i++) {
                pst.setString(i, pattern);
            }
            try (ResultSet rs = pst.executeQuery()) {
                List<Member> members = new ArrayList<>();
                while (rs.next()) {
                    members.add(mapMember(rs));
                }
                return members;
            }
        }
    }

    @Override
    public boolean update(Member member) throws SQLException {
        // Omit the password column when blank: the existing credential stays untouched.
        boolean changePassword = member.getPassword() != null && !member.getPassword().isBlank();
        String sql = "UPDATE members SET member_number=?, username=?, first_name=?, last_name=?, email=?, phone_number=?"
                + (changePassword ? ", password=?" : "") + " WHERE member_id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, member.getMember_No());
            pst.setString(2, member.getUsername());
            pst.setString(3, member.getFirstName());
            pst.setString(4, member.getLastName());
            pst.setString(5, member.getEmail());
            pst.setString(6, member.getPhoneNumber());
            int index = 7;
            if (changePassword) pst.setString(index++, member.getPassword());
            pst.setInt(index, member.getMemberId());
            return pst.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int memberId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM members WHERE member_id=?")) {
            pst.setInt(1, memberId);
            return pst.executeUpdate() == 1;
        }
    }

    private Member mapMember(ResultSet rs) throws SQLException {
        Member member = new Member(
                rs.getInt("member_id"),
                rs.getString("username"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone_number"),
                null,
                rs.getString("role")
        );
        member.setMember_No(rs.getString("member_number"));
        return member;
    }
}

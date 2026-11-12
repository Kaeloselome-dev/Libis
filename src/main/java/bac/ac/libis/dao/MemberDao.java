package bac.ac.libis.dao;

import bac.ac.libis.model.Member;

import java.sql.SQLException;
import java.util.List;

public interface MemberDao {
    Member findByUsername(String username) throws SQLException;
    Member findById(int memberId) throws SQLException;
    Member findByEmail(String email) throws SQLException;
    Member findByMemberNumber(String memberNumber) throws SQLException;
    List<Member> findAll() throws SQLException;
    List<Member> search(String term, String filterBy) throws SQLException;
    boolean update(Member member) throws SQLException;
    boolean delete(int memberId) throws SQLException;
    boolean registerMember(Member member) throws SQLException;
}

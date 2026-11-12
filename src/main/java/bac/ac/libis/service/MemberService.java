package bac.ac.libis.service;

import bac.ac.libis.dao.MemberDao;
import bac.ac.libis.dao.MemberDaoImpl;
import bac.ac.libis.exception.ValidationException;
import bac.ac.libis.model.Member;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MemberService {
    private final MemberDao members;
    public MemberService() { this(new MemberDaoImpl()); }
    public MemberService(MemberDao members) { this.members = members; }

    public List<Member> search(String term, String filter) throws SQLException { return members.search(term, filter); }
    public Member findById(int id) throws SQLException { return members.findById(id); }

    public void save(Member member) throws SQLException, ValidationException {
        Map<String, String> errors = new LinkedHashMap<>();
        required(errors, "memberNumber", member.getMember_No(), 20);
        required(errors, "firstName", member.getFirstName(), 50);
        required(errors, "lastName", member.getLastName(), 50);
        required(errors, "username", member.getUsername(), 50);
        required(errors, "email", member.getEmail(), 100);
        if (member.getEmail() != null && !member.getEmail().isBlank() && !member.getEmail().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
            errors.put("email", "Enter a valid email address.");
        if (member.getMemberId() == 0) required(errors, "password", member.getPassword(), 100);
        else if (member.getPassword() != null && member.getPassword().length() > 100) errors.put("password", "Use at most 100 characters.");
        if (member.getPhoneNumber() != null && member.getPhoneNumber().length() > 20) errors.put("phoneNumber", "Use at most 20 characters.");
        duplicate(errors, "memberNumber", members.findByMemberNumber(member.getMember_No()), member);
        duplicate(errors, "email", members.findByEmail(member.getEmail()), member);
        duplicate(errors, "username", members.findByUsername(member.getUsername()), member);
        if (!errors.isEmpty()) throw new ValidationException(errors);
        if (member.getMemberId() != 0 && members.findById(member.getMemberId()) == null)
            throw new ValidationException(Map.of("form", "This member no longer exists."));
        try {
            boolean saved = member.getMemberId() == 0 ? members.registerMember(member) : members.update(member);
            if (!saved) throw new ValidationException(Map.of("form", "This member could not be saved. Reload and try again."));
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new ValidationException(Map.of("form", "Member number, email or username is already in use. Please check these fields."));
        }
    }

    private void required(Map<String, String> errors, String key, String value, int max) {
        if (value == null || value.isBlank()) errors.put(key, "This field is required.");
        else if (value.length() > max) errors.put(key, "Use at most " + max + " characters.");
    }

    private void duplicate(Map<String, String> errors, String key, Member found, Member member) {
        if (found != null && found.getMemberId() != member.getMemberId()) errors.put(key, "This value is already in use.");
    }

    public String delete(int id, int librarianId) throws SQLException, ValidationException {
        if (id == librarianId) throw new ValidationException(Map.of("form", "You cannot delete your own account."));
        Member member = members.findById(id);
        if (member == null || !members.delete(id)) throw new ValidationException(Map.of("form", "This member no longer exists."));
        return "Deleted member " + member.getFullName() + " (" + member.getMember_No() + ").";
    }
}

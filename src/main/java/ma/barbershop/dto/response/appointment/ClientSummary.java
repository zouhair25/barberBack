package ma.barbershop.dto.response.appointment;

import ma.barbershop.domain.entity.User;

public record ClientSummary(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
    public static ClientSummary from(User u) {
        if (u == null) return null;
        return new ClientSummary(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                u.getPhone()
        );
    }
}

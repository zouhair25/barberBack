package ma.barbershop.dto.response.appointment;

import ma.barbershop.domain.entity.BarberService;
import ma.barbershop.domain.entity.CentreSoin;
import ma.barbershop.domain.enums.ServiceCategory;

import java.math.BigDecimal;

public record CentreSoinSummary(
        Long id,
        String shopName,
        String address
) {
    public static CentreSoinSummary from(CentreSoin s) {
        if (s == null) return null;
        return new CentreSoinSummary(
                s.getId(),
                s.getShopName(),
                s.getAddress()
        );
    }
}

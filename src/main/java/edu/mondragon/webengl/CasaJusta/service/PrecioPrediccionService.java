package edu.mondragon.webengl.CasaJusta.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PrecioPrediccionService {

    private static final BigDecimal MIL = new BigDecimal("1000");
    private static final BigDecimal INTERCEPTO = new BigDecimal("-29.28");
    private static final BigDecimal PENDIENTE = new BigDecimal("2.83");

    public BigDecimal predecirPrecioEnMiles(Integer metrosCuadrados) {
        if (metrosCuadrados == null || metrosCuadrados <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal metros = BigDecimal.valueOf(metrosCuadrados);
        BigDecimal precio = INTERCEPTO.add(PENDIENTE.multiply(metros));

        if (precio.signum() < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return precio.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal predecirPrecioEnEuros(Integer metrosCuadrados) {
        BigDecimal precioMiles = predecirPrecioEnMiles(metrosCuadrados);
        return precioMiles.multiply(MIL).setScale(2, RoundingMode.HALF_UP);
    }
}

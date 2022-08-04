package br.com.beautystyle.database.converters;

import androidx.room.TypeConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalConverter {

    @TypeConverter
    public static BigDecimal fromLong(Long value) {
        return value == null ? null : new BigDecimal(value)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }

    @TypeConverter
    public static Long fromBigDecimal(BigDecimal value) {
        return value == null ? null : value.multiply(new BigDecimal(100))
                .longValueExact();
    }
}

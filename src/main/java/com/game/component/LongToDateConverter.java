package com.game.component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;

@Converter
public class LongToDateConverter implements AttributeConverter<Long, Date> {

    @Override
    public Date convertToDatabaseColumn(Long attribute) {
        if (attribute == null) {
            return null;
        }
        return new Date(attribute);
    }

    @Override
    public Long convertToEntityAttribute(Date dbData) {
        if (dbData == null) {
            return null;
        }
        return dbData.getTime();
    }
}

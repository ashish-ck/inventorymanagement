package com.drivojoy.inventory.utils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomJsonDateDeserializer extends JsonDeserializer<LocalDate>{

    @Override
    public LocalDate deserialize(JsonParser jsonparser,
            DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
    	 String date = jsonparser.getText();
    	final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        final LocalDate dt = LocalDate.parse(date, dtf);
		return dt;

    }
}

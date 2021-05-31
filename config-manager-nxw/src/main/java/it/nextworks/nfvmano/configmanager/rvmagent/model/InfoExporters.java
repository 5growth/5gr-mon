package it.nextworks.nfvmano.configmanager.rvmagent.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class InfoExporters {
    private static InfoExporters instance;
    private static HashMap<String, List<ExporterParameter>> infoExporters = null;
    private static final String json_file = "exporters.json";

    private InfoExporters() {
        TypeReference<HashMap<String, List<ExporterParameter>>> typeRef
                = new TypeReference<HashMap<String, List<ExporterParameter>>>() {
        };

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(json_file);

        ObjectMapper mapper = new ObjectMapper();

        try {
            infoExporters = mapper.readValue(inputStream, typeRef);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static synchronized InfoExporters getInstance() {
        if (instance == null) {
            instance = new InfoExporters();
        }
        return instance;
    }

    public static HashMap<String, List<ExporterParameter>> getInfoExporters() {
        InfoExporters infoExporters = getInstance();
        return infoExporters.infoExporters;
    }
}

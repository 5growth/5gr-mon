package it.nextworks.nfvmano.configmanager.rvmagent.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InfoExporters {
    private static InfoExporters instance;
    private static HashMap<String, List<ExporterParameter>> infoExporters = null;
    private static final String json_file = "exporters.json";

    private InfoExporters() {
        TypeReference<HashMap<String, List<ExporterParameter>>> typeRef
                = new TypeReference<HashMap<String, List<ExporterParameter>>>() {
        };


        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(json_file).getFile());
        Object obj = null;
        JSONObject jsonObject = null;
        ObjectMapper mapper = new ObjectMapper();

        try {
            infoExporters = mapper.readValue(new FileReader(file), typeRef);
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

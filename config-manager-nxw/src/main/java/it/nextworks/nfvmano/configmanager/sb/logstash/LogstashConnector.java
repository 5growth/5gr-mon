package it.nextworks.nfvmano.configmanager.sb.logstash;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.topics.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class LogstashConnector {

    private static final Logger log = LoggerFactory.getLogger(LogstashConnector.class);

    private File logstashConfig;

    public LogstashConnector( String logstashConfigPath ) {

        log.info("Looking for logstash config at {}", logstashConfigPath);
        logstashConfig = new File(logstashConfigPath);
        if (!logstashConfig.isFile()) {
            throw new IllegalArgumentException(String.format(
                    "Illegal logstash config file path '%s'",
                    logstashConfigPath
            ));
        }
        log.info("Logstash config found");
    }

    public Future<Void> setTopic(List<Topic> topics) {
        String content = "";
        BufferedReader reader = null;
        FileWriter writer = null;

        try {
            if (log.isTraceEnabled()) {
                log.trace("Writing Logstash topics:\n");
                log.trace(topics.toString());
            }

            reader = new BufferedReader(new FileReader(logstashConfig));
            //Reading all the lines of input text file into oldContent
            String line = reader.readLine();

            while (line != null) {
                if(line.contains("topics")) {
                    line = createTopicLine(topics);
                }
                content = content + line + System.lineSeparator();
                line = reader.readLine();
            }
            //Rewriting the input text file with newContent
            writer = new FileWriter(logstashConfig);
            writer.write(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                //Closing the resources
                if(reader != null) reader.close();
                if(writer != null) writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Future.succeededFuture();
    }

    private String createTopicLine(List<Topic> topics) {
        return "                topics => " + topics.toString();
    }
}



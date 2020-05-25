package it.nextworks.nfvmano.configmanager;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.post;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class RVMagentTest {
//    @Ignore
    @Test
    public void testCreateRVMagent() throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.post("http://127.0.0.1:8989/prom-manager/agent")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body("{\n\"install_method\": \"cloud_init\"," +
                        "\n\"description\": \"VNF_1\"\n}")
                .asString();
        System.out.println(response.getBody());
        Unirest.setTimeouts(0, 0);
        response = Unirest.post("http://127.0.0.1:8989/prom-manager/agent_command")
                .header("Content-Type", "application/json")
                .body("{\n        \"agent_id\": \"vm_agent_1\",\n        \"args\": [\"arg1_value\", \"arg2_value\"],\n        \"env\": {\"MY_ENV_VARIABLE\": \"MY_ENV_VARIABLE_VALUE\"},\n        \"type_message\": \"bash_script\",\n        \"cwd\": \"/tmp\",\n        \"body\": [\"#! /bin/bash -e\",\n                 \"date\",\n                 \"date\",\n                 \"echo $1\",\n                 \"exit 0\"]\n    }")
                .asString();
        System.out.println(response.getBody());

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Unirest.setTimeouts(0, 0);
        response = Unirest.get("http://127.0.0.1:8989/prom-manager/agent_command/vm_agent_1/1")
                .asString();
        System.out.println(response.getBody());
    }

    @Ignore
    @Test
    public void testCreatePrometheusCollector() throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.post("http://127.0.0.1:8989/prom-manager/agent")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body("{\n\"install_method\": \"cloud_init\"," +
                        "\n\"description\": \"VNF_1\"\n}")
                .asString();
        System.out.println(response.getBody());
        Unirest.setTimeouts(0, 0);
        response = Unirest.post("http://127.0.0.1:8989/prom-manager/add_prometheus_collector")
                .header("Content-Type", "application/json")
                .body("{\n        \"agent_id\": \"vm_agent_1\"," +
                        "\n        \"host\": \"172.18.194.42\",\n" +
                        "        \"prometheus_topic\": \"prometheus\",\n" +
                        "        \"port\": \"8003\",\n " +
                        "       \"node_url_suffix\": \"/metrics\",\n" +
                        "        \"prometheus_job\": \"kafka_job3\",\n" +
                        "        \"interval\": \"1\",\n" +
                        "        \"labels\":[\n     " +
                        "   \t{\"key\":\"instance\",\n     " +
                        "   \t\"value\": \"192.168.100.1:5000\"}\n   " +
                        "     \t]\n    }")
                .asString();
        System.out.println(response.getBody());

//        Unirest.setTimeouts(0, 0);
//        response = Unirest.delete("http://127.0.0.1:8989/prom-manager/del_prometheus_collector/vm_agent_1/172.18.194.42:8003")
//                .body("")
//                .asString();
//
//        System.out.println(response.getBody());
    }

}


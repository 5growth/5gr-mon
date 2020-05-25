package it.nextworks.nfvmano.configmanager.render;

import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import it.nextworks.nfvmano.configmanager.utils.ConfigReader;
import org.apache.commons.codec.Charsets;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CreateInitScript {

    private String restHost = null;
    private String restPort = "8989";
    private String fileServerRoot = "/tmp/resources";
    private String fileServerUrl = null;
    private String initScriptTemplate = "script/linux-download.sh.template";
    private String installScriptTemplate = "script/linux.sh.template";
    private String bootstrapServers = null;
    private String rvmAgentName = null;
    private String rvmDaemonUser = null;
    private String fileServer = "./fileserver";
    private String file_server_url = null;

    public String getRestHost() {
        return restHost;
    }

    public void setRestHost(String restHost) {
        this.restHost = restHost;
    }

    public String getRestPort() {
        return restPort;
    }

    public void setRestPort(String restPort) {
        this.restPort = restPort;
    }

    public String getFileServerRoot() {
        return fileServerRoot;
    }

    public void setFileServerRoot(String fileServerRoot) {
        this.fileServerRoot = fileServerRoot;
    }

    public String getFileServerUrl() {
        return "http://" + restHost + ':' + restPort + "/resources";
    }

    public String getInitScriptTemplate() {
        return initScriptTemplate;
    }

    public void setInitScriptTemplate(String initScriptTemplate) {
        this.initScriptTemplate = initScriptTemplate;
    }

    public String getInstallScriptTemplate() {
        return installScriptTemplate;
    }

    public void setInstallScriptTemplate(String installScriptTemplate) {
        this.installScriptTemplate = installScriptTemplate;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getRvmAgentName() {
        return rvmAgentName;
    }

    public void setRvmAgentName(String rvmAgentName) {
        this.rvmAgentName = rvmAgentName;
    }

    public String getRvmDaemonUser() {
        return rvmDaemonUser;
    }

    public void setRvmDaemonUser(String rvmDaemonUser) {
        this.rvmDaemonUser = rvmDaemonUser;
    }

    public String getFileServer() {
        return fileServer;
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    public String getFile_server_url() {
        return "http://" + restHost + ':' + restPort + "/resources";
    }

    public CreateInitScript(String rvmAgentName, String rvmDaemonUser) {
        ConfigReader config = new ConfigReader();
        String kafka_bootstrap_servers = config.getProperty("kafka.bootstrap.servers");
        String host = kafka_bootstrap_servers.split(":")[0];
        this.restHost = host;
        this.bootstrapServers = host;

        this.rvmAgentName = rvmAgentName;
        this.rvmDaemonUser = rvmDaemonUser;
    }

    public String generate_scripts(){
        Jinjava jinjava = new Jinjava();
        String cert_content = "";
        String remote_ssl_cert_path = "~" + rvmDaemonUser + "/" + rvmAgentName + "/rvmagent/ssl/rvmagent_internal_cert.pem";

        String template = null;

        try {
            template = Resources.toString(Resources.getResource(installScriptTemplate), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> daemon_env = new HashMap<String, Object> ();
        daemon_env.put("REST_PORT", restPort);
        daemon_env.put("REST_HOST", restHost);
        daemon_env.put("BOOTSTRAP_SERVERS", bootstrapServers);
        daemon_env.put("FORWARD_TOPIC", rvmAgentName + "_forward");
        daemon_env.put("BACKWARD_TOPIC", rvmAgentName + "_backward");
        daemon_env.put("LOGFILE", "LOGFILE");
        daemon_env.put("RVM_DAEMON_USER", rvmDaemonUser);
        daemon_env.put("RVM_DAEMON_WORKDIR", "~" + rvmDaemonUser + "/" + rvmAgentName + "/work");
        daemon_env.put("RVM_DAEMON_NAME", rvmAgentName);
        daemon_env.put("AGENT_CONFIG", "/home/" + rvmDaemonUser + "/" + rvmAgentName + "/config/config.json");
        Map<String, Object> rvm_agent = new HashMap<String, Object> ();
        rvm_agent.put("rest_host", restHost);
        rvm_agent.put("workdir", "~" + rvmDaemonUser + "/" + rvmAgentName + "/work");
        rvm_agent.put("agent_dir", "~" + rvmDaemonUser + "/" + rvmAgentName);
        rvm_agent.put("name", rvmAgentName);
        rvm_agent.put("envdir", "~" + rvmDaemonUser + "/" + rvmAgentName + "/env");
        Map<String, Object> installScriptVar = new HashMap<String, Object> ();
        installScriptVar.put("conf", rvm_agent);
        installScriptVar.put("daemon_env", daemon_env);
        installScriptVar.put("pm_options", "");
        installScriptVar.put("custom_env", null);
        installScriptVar.put("custom_env_path", "~" + rvmDaemonUser + "/custom_agent_env.sh");
        installScriptVar.put("file_server_url", getFile_server_url());
        installScriptVar.put("ssl_cert_content", cert_content);
        installScriptVar.put("ssl_cert_path", remote_ssl_cert_path);
        installScriptVar.put("auth_token_header", "Authentication-Token");
        installScriptVar.put("auth_token_value", null);
        installScriptVar.put("install", "True");
        installScriptVar.put("configure", "true");
        installScriptVar.put("start", "True");
        installScriptVar.put("add_ssl_cert","False");
        String renderedTemplate = jinjava.render(template, installScriptVar);
        String uuid = UUID.randomUUID().toString();
        FileWriter fileWriter = null;
        try {
            String path = String.format("%s/scripts/%s.sh", fileServer, uuid);
            fileWriter = new FileWriter(path);
            fileWriter.write(renderedTemplate);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> args_dict = new HashMap<String, Object> ();
        String script_url = String.format("%s/scripts/%s.sh", getFileServerUrl(), uuid);
        args_dict.put("ssl_cert_content", "cert");
        args_dict.put("sudo", "sudo");
        args_dict.put("link", script_url);
        args_dict.put("ssl_cert_path", "/home/" + rvmDaemonUser + "/" + rvmAgentName + "/cert/cert.pem");


        String template2 = null;

        try {
            template2 = Resources.toString(Resources.getResource(initScriptTemplate), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String renderedTemplate2 = jinjava.render(template2, args_dict);
        return renderedTemplate2;
    }
}
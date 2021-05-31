package it.nextworks.nfvmano.configmanager.rvmagent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import io.vertx.ext.web.api.validation.ValidationException;
import it.nextworks.nfvmano.configmanager.MainVerticle;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.RVMAgentCommand;
import it.nextworks.nfvmano.configmanager.utils.Validated;
import org.apache.commons.codec.Charsets;

import java.io.IOException;
import java.util.*;

public class RVMAgentExporter implements Validated{
    @JsonProperty("agent_id")
    private String agentId;
    @JsonProperty("command_id")
    private Integer commandId;
    private ArrayList<String> args = new ArrayList<String>();
    private Map<String, String> env = new HashMap<String, String> ();
    @JsonProperty("type_message")
    private String typeMessage = "bash_script";
    private String cwd = "/tmp";
    private String exporter;
    private ArrayList<String> body;
    private String host = "127.0.0.1";
    @JsonProperty("prometheus_topic")
    private String prometheusTopic = "prometheus";
    private String port;
    @JsonProperty("node_url_suffix")
    private String nodeUrlSuffix = "/metrics";
    @JsonProperty("prometheus_job")
    private String prometheusJob;
    private String interval = "1";
    private List<StructureKeyValue> labels;
    private List<StructureKeyValue> params;
    @JsonIgnore
    private HashMap<String, List<ExporterParameter>> infoExporters;

    public RVMAgentExporter() {
        this.infoExporters = InfoExporters.getInfoExporters();
    }

    public RVMAgentExporter(String agentId, Integer commandId, ArrayList<String> args, Map<String, String> env, String typeMessage, String cwd, String exporter, String objectType, ArrayList<String> body) {
        this.infoExporters = InfoExporters.getInfoExporters();
        this.agentId = agentId;
        this.commandId = commandId;
        this.args = args;
        this.env = env;
        this.typeMessage = typeMessage;
        this.cwd = cwd;
        this.exporter = exporter;
        this.body = body;

    }

    public Optional<ValidationException> validate() {
        if (agentId == null) {
            return Optional.of(new ValidationException("ALERT: agentId cannot be null"));
        }
        return Optional.empty();
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Integer getCommandId() {
        return commandId;
    }

    public void setCommandId(Integer commandId) {
        this.commandId = commandId;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public void setArgs(ArrayList<String> args) {
        this.args = args;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    public String getCwd() {
        return cwd;
    }

    public void setCwd(String cwd) {
        this.cwd = cwd;
    }

    public String getExporter() {
        return exporter;
    }

    public void setExporter(String exporter) {
        this.exporter = exporter;
    }

    public ArrayList<String> getBody() {
        return body;
    }

    public void setBody(ArrayList<String> body) {
        this.body = body;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPrometheusTopic() {
        return prometheusTopic;
    }

    public void setPrometheusTopic(String prometheusTopic) {
        this.prometheusTopic = prometheusTopic;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getNodeUrlSuffix() {
        return nodeUrlSuffix;
    }

    public void setNodeUrlSuffix(String nodeUrlSuffix) {
        this.nodeUrlSuffix = nodeUrlSuffix;
    }

    public String getPrometheusJob() {
        return prometheusJob;
    }

    public void setPrometheusJob(String prometheusJob) {
        this.prometheusJob = prometheusJob;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public List<StructureKeyValue> getLabels() {
        return labels;
    }

    public void setLabels(List<StructureKeyValue> labels) {
        this.labels = labels;
    }

    public List<StructureKeyValue> getParams() {
        return params;
    }

    public void setParams(List<StructureKeyValue> params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RVMAgentExporter that = (RVMAgentExporter) o;
        return Objects.equals(agentId, that.agentId) &&
                Objects.equals(commandId, that.commandId) &&
                Objects.equals(args, that.args) &&
                Objects.equals(env, that.env) &&
                Objects.equals(typeMessage, that.typeMessage) &&
                Objects.equals(cwd, that.cwd) &&
                Objects.equals(exporter, that.exporter) &&
                Objects.equals(body, that.body) &&
                Objects.equals(host, that.host) &&
                Objects.equals(prometheusTopic, that.prometheusTopic) &&
                Objects.equals(port, that.port) &&
                Objects.equals(nodeUrlSuffix, that.nodeUrlSuffix) &&
                Objects.equals(prometheusJob, that.prometheusJob) &&
                Objects.equals(interval, that.interval) &&
                Objects.equals(labels, that.labels) &&
                Objects.equals(params, that.params) &&
                Objects.equals(infoExporters, that.infoExporters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentId, commandId, args, env, typeMessage, cwd, exporter, body, host, prometheusTopic, port, nodeUrlSuffix, prometheusJob, interval, labels, params, infoExporters);
    }

    @Override
    public String toString() {
        return "RVMAgentExporter{" +
                "agentId='" + agentId + '\'' +
                ", commandId=" + commandId +
                ", args=" + args +
                ", env=" + env +
                ", typeMessage='" + typeMessage + '\'' +
                ", cwd='" + cwd + '\'' +
                ", exporter='" + exporter + '\'' +
                ", body=" + body +
                ", host='" + host + '\'' +
                ", prometheusTopic='" + prometheusTopic + '\'' +
                ", port='" + port + '\'' +
                ", nodeUrlSuffix='" + nodeUrlSuffix + '\'' +
                ", prometheusJob='" + prometheusJob + '\'' +
                ", interval='" + interval + '\'' +
                ", labels=" + labels +
                ", params=" + params +
                ", infoExporters=" + infoExporters +
                '}';
    }

    @JsonIgnore
    public RVMAgentCommand getInstallCommand() {
        String port = null;
        String template_name = null;
        Jinjava jinjava = new Jinjava();
        List<ExporterParameter> infoExporter = infoExporters.get(this.exporter);
        for (ExporterParameter exporterParameter: infoExporter){
             if(exporterParameter.getKey().equals("install_template")){
                template_name = exporterParameter.getValue();
            }
        }
        ClassLoader classLoader = getClass().getClassLoader();
        String template_path = "exporter_templates/" + template_name;
        String template = null;
        try {
            template = Resources.toString(Resources.getResource(template_path), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> args_dict = new HashMap<String, Object> ();
        String path = String.format("http://%s:%s", MainVerticle.ip, MainVerticle.port);
        List<String> paths = new ArrayList<String>();
        for (StructureKeyValue keyValue :this.getLabels()) {
            args_dict.put(keyValue.getKey(), keyValue.getValue());
            if (keyValue.getKey().startsWith("file")){
                paths.add(keyValue.getValue());
            }
        }
        for (StructureKeyValue keyValue :this.getParams()) {
            args_dict.put(keyValue.getKey(), keyValue.getValue());
            if (keyValue.getKey().startsWith("file")){
                paths.add(keyValue.getValue());
            }
        }
        args_dict.put("paths", paths);
        args_dict.put("fileserver_url", path);
        args_dict.put("kafka_bootstrap_server", MainVerticle.kafka_bootstrap_server);
        String script = jinjava.render(template, args_dict);
        RVMAgentCommand rvmAgentCommand = new RVMAgentCommand();
        rvmAgentCommand.setAgentId(this.getAgentId());
        rvmAgentCommand.setArgs(this.getArgs());
        rvmAgentCommand.setCwd(this.getCwd());
        rvmAgentCommand.setBody(new ArrayList<String>(Arrays.asList(script.split("\n"))));
        rvmAgentCommand.setEnv(this.getEnv());
        rvmAgentCommand.setTypeMessage(this.getTypeMessage());
        rvmAgentCommand.setObjectType("command");
        return rvmAgentCommand;
    }

    @JsonIgnore
    public RVMAgentCommand getUninstallCommand() {
        String template_name = null;
        Jinjava jinjava = new Jinjava();
        List<ExporterParameter> infoExporter = infoExporters.get(this.exporter);
        for (ExporterParameter exporterParameter: infoExporter){
            if(exporterParameter.getKey().equals("uninstall_template")){
                template_name = exporterParameter.getValue();
            }
        }
        String template_path = "exporter_templates/" + template_name;
        String template = null;
        try {
            template = Resources.toString(Resources.getResource(template_path), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String script = jinjava.render(template, null);
        RVMAgentCommand rvmAgentCommand = new RVMAgentCommand();
        rvmAgentCommand.setAgentId(this.getAgentId());
        rvmAgentCommand.setArgs(this.getArgs());
        rvmAgentCommand.setCwd(this.getCwd());
        rvmAgentCommand.setBody(new ArrayList<String>(Arrays.asList(script.split("\n"))));
        rvmAgentCommand.setEnv(this.getEnv());
        rvmAgentCommand.setTypeMessage(this.getTypeMessage());
        rvmAgentCommand.setObjectType("command");
        return rvmAgentCommand;
    }

    @JsonIgnore
    public PrometheusCollector getInstallCollector() {
        PrometheusCollector addPrometheusCollector = new PrometheusCollector();
        String port = null;
        List<ExporterParameter> infoExporter = infoExporters.get(this.exporter);
        for (ExporterParameter exporterParameter : infoExporter) {
            if (exporterParameter.getKey().equals("port")) {
                port = exporterParameter.getValue();
            }
        }
        if (port == null){
            return null;
        }
        addPrometheusCollector.setPort(port);
        addPrometheusCollector.setObjectType("add_prometheus_collector");
        addPrometheusCollector.setHost(this.getHost());
        addPrometheusCollector.setInterval(this.getInterval());
        addPrometheusCollector.setLabels(this.getLabels());
        addPrometheusCollector.setNodeUrlSuffix(this.getNodeUrlSuffix());
        addPrometheusCollector.setPrometheusJob(this.getPrometheusJob());
        addPrometheusCollector.setPrometheusTopic(this.getPrometheusTopic());
        addPrometheusCollector.setRvmAgentId(this.getAgentId());
        addPrometheusCollector.setParams(this.getParams());
        return addPrometheusCollector;
    }

    @JsonIgnore
    public void setCollector(PrometheusCollector return_addPrometheusCollector) {

        return_addPrometheusCollector.getCollectorId();
        this.setHost(return_addPrometheusCollector.getHost());
        this.setInterval(return_addPrometheusCollector.getInterval());
        this.setLabels(return_addPrometheusCollector.getLabels());
        this.setNodeUrlSuffix(return_addPrometheusCollector.getNodeUrlSuffix());
        this.setPort(return_addPrometheusCollector.getPort());
        this.setPrometheusJob(return_addPrometheusCollector.getPrometheusJob());
        this.setPrometheusTopic(return_addPrometheusCollector.getPrometheusTopic());
        this.setAgentId(return_addPrometheusCollector.getRvmAgentId());
        this.setParams(return_addPrometheusCollector.getParams());
    }

    @JsonIgnore
    public void setCommand(RVMAgentCommand returnRVMAgentCommand) {
        this.setAgentId(returnRVMAgentCommand.getAgentId());
        this.setArgs(returnRVMAgentCommand.getArgs());
        this.setBody(returnRVMAgentCommand.getBody());
        this.setCommandId(returnRVMAgentCommand.getCommandId());
        this.setCwd(returnRVMAgentCommand.getCwd());
        this.setEnv(returnRVMAgentCommand.getEnv());
        this.setTypeMessage(returnRVMAgentCommand.getTypeMessage());
    }
    @JsonIgnore
    public String getPrometheusCollectorId() {
        List<ExporterParameter> infoExporter = infoExporters.get(this.exporter);
        for (ExporterParameter exporterParameter : infoExporter) {
            if (exporterParameter.getKey().equals("port")) {
                this.port = exporterParameter.getValue();
            }
        }
        return this.host + ":" + this.port;
    }

}

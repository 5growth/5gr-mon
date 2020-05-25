package it.nextworks.nfvmano.configmanager.rvmagent.model;

import java.util.Map;

public class EnvItem {
    private Map<String, String> envItem;

    public EnvItem() {
    }

    public EnvItem(Map<String, String> envItem) {
        this.envItem = envItem;
    }

    public Map<String, String> getEnvItem() {
        return envItem;
    }

    public void setEnvItem(Map<String, String> envItem) {
        this.envItem = envItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnvItem envItem = (EnvItem) o;

        return this.envItem != null ? this.envItem.equals(envItem.envItem) : envItem.envItem == null;
    }

    @Override
    public int hashCode() {
        return envItem != null ? envItem.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Env{" +
                "envItem=" + envItem +
                '}';
    }
}

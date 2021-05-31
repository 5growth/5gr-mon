
package it.nextworks.nfvmano.configmanager.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CreatedResponse {

    @JsonProperty("created")
    private List<String> created = null;

    public CreatedResponse created(Collection<String> deleted) {
        this.created(new ArrayList<>(deleted));
        return this;
    }

    public CreatedResponse created(List<String> created) {
        this.created = created;
        return this;
    }

    public CreatedResponse addDeletedItem(String deletedItem) {
        if (this.created == null) {
            this.created = new ArrayList<>();
        }
        this.created.add(deletedItem);
        return this;
    }

    public List<String> getDeleted() {
        return created;
    }

    public void setDeleted(List<String> created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreatedResponse that = (CreatedResponse) o;
        return Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(created);
    }

    @Override
    public String toString() {
        return "CreatedResponse{" +
                "created=" + created +
                '}';
    }
}

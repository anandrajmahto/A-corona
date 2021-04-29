
package com.ARMsCreation.a_corona.Pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Data {

    private Summary summary;
    private List<Regional> regional = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<Regional> getRegional() {
        return regional;
    }

    public void setRegional(List<Regional> regional) {
        this.regional = regional;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

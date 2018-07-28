package me.ccrama.redditslide.Tumblr;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
        "status", "msg"
})
public class Meta {

    @JsonProperty("status")
    private Integer status;
    @JsonProperty("msg")
    private String msg;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * @return The status
     */
    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return The msg
     */
    @JsonProperty("msg")
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg The msg
     */
    @JsonProperty("msg")
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

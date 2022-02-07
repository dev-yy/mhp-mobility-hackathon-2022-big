
package com.mhp.mobility.hackathon.pojos;

import java.io.Serializable;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
                     "id",
                     "bezeichnung"
})
@Generated ("jsonschema2pojo")
public class Status implements Serializable {
   
   private final static long serialVersionUID = -2627001309380468604L;
   
   @JsonProperty ("id")
   private Integer           id;
   @JsonProperty ("bezeichnung")
   private String            bezeichnung;
   
   @JsonProperty ("id")
   public Integer getId() {
      return id;
   }
   
   @JsonProperty ("id")
   public void setId(Integer id) {
      this.id = id;
   }
   
   @JsonProperty ("bezeichnung")
   public String getBezeichnung() {
      return bezeichnung;
   }
   
   @JsonProperty ("bezeichnung")
   public void setBezeichnung(String bezeichnung) {
      this.bezeichnung = bezeichnung;
   }
   
}

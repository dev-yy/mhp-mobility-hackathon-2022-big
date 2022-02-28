
package com.mhp.mobility.hackathon.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
                     "fahrzeugprojektProduktKey",
                     "artefaktTyp",
                     "referenzItems"
})
@Generated ("jsonschema2pojo")
public class Nutzdateninformationen implements Serializable {
   
   private final static long  serialVersionUID = -6097562775275470904L;
   
   @JsonProperty ("fahrzeugprojektProduktKey")
   private String             fahrzeugprojektProduktKey;
   @JsonProperty ("artefaktTyp")
   private String             artefaktTyp;
   @JsonProperty ("referenzItems")
   private List<ReferenzItem> referenzItems    = new ArrayList<ReferenzItem>();
   
   @JsonProperty ("fahrzeugprojektProduktKey")
   public String getFahrzeugprojektProduktKey() {
      return fahrzeugprojektProduktKey;
   }
   
   @JsonProperty ("fahrzeugprojektProduktKey")
   public void setFahrzeugprojektProduktKey(String fahrzeugprojektProduktKey) {
      this.fahrzeugprojektProduktKey = fahrzeugprojektProduktKey;
   }
   
   @JsonProperty ("artefaktTyp")
   public String getArtefaktTyp() {
      return artefaktTyp;
   }
   
   @JsonProperty ("artefaktTyp")
   public void setArtefaktTyp(String artefaktTyp) {
      this.artefaktTyp = artefaktTyp;
   }
   
   @JsonProperty ("referenzItems")
   public List<ReferenzItem> getReferenzItems() {
      return referenzItems;
   }
   
   @JsonProperty ("referenzItems")
   public void setReferenzItems(List<ReferenzItem> referenzItems) {
      this.referenzItems = referenzItems;
   }
   
}

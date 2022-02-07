
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
                     "diagnoseAdresse",
                     "steuergeraetename",
                     "teilenummer",
                     "softwareVersion",
                     "hardwareVersion",
                     "status",
                     "referenzierteDaten"
})
@Generated ("jsonschema2pojo")
public class ReferenzItem implements Serializable {
   
   private final static long        serialVersionUID   = 542332189010777281L;
   
   @JsonProperty ("diagnoseAdresse")
   private String                   diagnoseAdresse;
   @JsonProperty ("steuergeraetename")
   private String                   steuergeraetename;
   @JsonProperty ("teilenummer")
   private String                   teilenummer;
   @JsonProperty ("softwareVersion")
   private String                   softwareVersion;
   @JsonProperty ("hardwareVersion")
   private String                   hardwareVersion;
   @JsonProperty ("status")
   private Status                   status;
   @JsonProperty ("referenzierteDaten")
   private List<ReferenzierteDaten> referenzierteDaten = new ArrayList<ReferenzierteDaten>();
   
   @JsonProperty ("diagnoseAdresse")
   public String getDiagnoseAdresse() {
      return diagnoseAdresse;
   }
   
   @JsonProperty ("diagnoseAdresse")
   public void setDiagnoseAdresse(String diagnoseAdresse) {
      this.diagnoseAdresse = diagnoseAdresse;
   }
   
   @JsonProperty ("steuergeraetename")
   public String getSteuergeraetename() {
      return steuergeraetename;
   }
   
   @JsonProperty ("steuergeraetename")
   public void setSteuergeraetename(String steuergeraetename) {
      this.steuergeraetename = steuergeraetename;
   }
   
   @JsonProperty ("teilenummer")
   public String getTeilenummer() {
      return teilenummer;
   }
   
   @JsonProperty ("teilenummer")
   public void setTeilenummer(String teilenummer) {
      this.teilenummer = teilenummer;
   }
   
   @JsonProperty ("softwareVersion")
   public String getSoftwareVersion() {
      return softwareVersion;
   }
   
   @JsonProperty ("softwareVersion")
   public void setSoftwareVersion(String softwareVersion) {
      this.softwareVersion = softwareVersion;
   }
   
   @JsonProperty ("hardwareVersion")
   public String getHardwareVersion() {
      return hardwareVersion;
   }
   
   @JsonProperty ("hardwareVersion")
   public void setHardwareVersion(String hardwareVersion) {
      this.hardwareVersion = hardwareVersion;
   }
   
   @JsonProperty ("status")
   public Status getStatus() {
      return status;
   }
   
   @JsonProperty ("status")
   public void setStatus(Status status) {
      this.status = status;
   }
   
   @JsonProperty ("referenzierteDaten")
   public List<ReferenzierteDaten> getReferenzierteDaten() {
      return referenzierteDaten;
   }
   
   @JsonProperty ("referenzierteDaten")
   public void setReferenzierteDaten(List<ReferenzierteDaten> referenzierteDaten) {
      this.referenzierteDaten = referenzierteDaten;
   }
   
}


package com.mhp.mobility.hackathon.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
                     "fileIdentifier",
                     "filename",
                     "filetype",
                     "referenzierteEinzelDaten"
})
@Generated ("jsonschema2pojo")
public class ReferenzierteDaten implements Serializable {
   
   private final static long              serialVersionUID         = 6895192545473199915L;
   
   @JsonProperty ("fileIdentifier")
   private String                         fileIdentifier;
   @JsonProperty ("filename")
   private String                         filename;
   @JsonProperty ("filetype")
   private String                         filetype;
   @JsonProperty ("referenzierteEinzelDaten")
   private List<ReferenzierteEinzelDaten> referenzierteEinzelDaten = new ArrayList<ReferenzierteEinzelDaten>();
   
   @JsonProperty ("fileIdentifier")
   public String getFileIdentifier() {
      return fileIdentifier;
   }
   
   @JsonProperty ("fileIdentifier")
   public void setFileIdentifier(String fileIdentifier) {
      this.fileIdentifier = fileIdentifier;
   }
   
   @JsonProperty ("filename")
   public String getFilename() {
      return filename;
   }
   
   @JsonProperty ("filename")
   public void setFilename(String filename) {
      this.filename = filename;
   }
   
   @JsonProperty ("filetype")
   public String getFiletype() {
      return filetype;
   }
   
   @JsonProperty ("filetype")
   public void setFiletype(String filetype) {
      this.filetype = filetype;
   }
   
   @JsonProperty ("referenzierteEinzelDaten")
   public List<ReferenzierteEinzelDaten> getReferenzierteEinzelDaten() {
      return referenzierteEinzelDaten;
   }
   
   @JsonProperty ("referenzierteEinzelDaten")
   public void setReferenzierteEinzelDaten(List<ReferenzierteEinzelDaten> referenzierteEinzelDaten) {
      this.referenzierteEinzelDaten = referenzierteEinzelDaten;
   }
   
}

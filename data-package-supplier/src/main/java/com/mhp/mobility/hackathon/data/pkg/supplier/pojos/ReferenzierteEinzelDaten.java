
package com.mhp.mobility.hackathon.data.pkg.supplier.pojos;

import java.io.Serializable;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({
                     "fileIdentifier",
                     "filename",
                     "filetype"
})
@Generated ("jsonschema2pojo")
public class ReferenzierteEinzelDaten implements Serializable {
   
   private final static long serialVersionUID = -1272250775685665353L;
   
   @JsonProperty ("fileIdentifier")
   private String            fileIdentifier;
   @JsonProperty ("filename")
   private String            filename;
   @JsonProperty ("filetype")
   private String            filetype;
   
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
   
}

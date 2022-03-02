package com.mhp.mobility.hackathon.data.pkg.supplier.pojos;

public class NiMessage {
   
   private String blFileIdentifer;
   
   public NiMessage() {}
   
   public NiMessage(String blFileIdentifer) {
      this.blFileIdentifer = blFileIdentifer;
   }
   
   public String getBlFileIdentifer() {
      return blFileIdentifer;
   }
   
   public void setBlFileIdentifer(String blFileIdentifer) {
      this.blFileIdentifer = blFileIdentifer;
   }
   
   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder("NiMessage{");
      sb.append("blFileIdentifer='").append(blFileIdentifer).append('\'');
      sb.append('}');
      return sb.toString();
   }
}

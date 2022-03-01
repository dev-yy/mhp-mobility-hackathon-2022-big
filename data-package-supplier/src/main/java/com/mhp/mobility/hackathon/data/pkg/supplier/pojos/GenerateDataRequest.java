package com.mhp.mobility.hackathon.data.pkg.supplier.pojos;

public class GenerateDataRequest {
   
   private String blFileIdentifier;
   
   private int    cntItems;
   
   private int    cntOdx;
   
   private int    cntPdx;
   
   private int    cntPcc;
   
   private int    cntLum;
   
   public GenerateDataRequest(String blFileIdentifier, int cntItems, int cntOdx, int cntPdx, int cntPcc, int cntLum) {
      this.blFileIdentifier = blFileIdentifier;
      this.cntItems = cntItems;
      this.cntOdx = cntOdx;
      this.cntPdx = cntPdx;
      this.cntPcc = cntPcc;
      this.cntLum = cntLum;
   }
   
   public String getBlFileIdentifier() {
      return blFileIdentifier;
   }
   
   public void setBlFileIdentifier(String blFileIdentifier) {
      this.blFileIdentifier = blFileIdentifier;
   }
   
   public int getCntItems() {
      return cntItems;
   }
   
   public void setCntItems(int cntItems) {
      this.cntItems = cntItems;
   }
   
   public int getCntOdx() {
      return cntOdx;
   }
   
   public void setCntOdx(int cntOdx) {
      this.cntOdx = cntOdx;
   }
   
   public int getCntPdx() {
      return cntPdx;
   }
   
   public void setCntPdx(int cntPdx) {
      this.cntPdx = cntPdx;
   }
   
   public int getCntPcc() {
      return cntPcc;
   }
   
   public void setCntPcc(int cntPcc) {
      this.cntPcc = cntPcc;
   }
   
   public int getCntLum() {
      return cntLum;
   }
   
   public void setCntLum(int cntLum) {
      this.cntLum = cntLum;
   }
}

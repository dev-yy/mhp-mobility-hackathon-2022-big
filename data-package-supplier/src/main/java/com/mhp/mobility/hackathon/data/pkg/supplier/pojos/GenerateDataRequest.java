package com.mhp.mobility.hackathon.data.pkg.supplier.pojos;

public class GenerateDataRequest {
   
   private int cntItems;
   
   private int cntOdx;
   
   private int cntPdx;
   
   private int cntLum;
   
   public GenerateDataRequest(int cntItems, int cntOdx, int cntPdx, int cntLum) {
      this.cntItems = cntItems;
      this.cntOdx = cntOdx;
      this.cntPdx = cntPdx;
      this.cntLum = cntLum;
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
   
   public int getCntLum() {
      return cntLum;
   }
   
   public void setCntLum(int cntLum) {
      this.cntLum = cntLum;
   }
}

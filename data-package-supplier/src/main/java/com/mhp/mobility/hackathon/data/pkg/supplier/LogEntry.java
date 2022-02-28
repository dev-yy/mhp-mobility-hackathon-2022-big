package com.mhp.mobility.hackathon.data.pkg.supplier;

public class LogEntry {
   
   private String   initiator;
   
   private String   action;
   
   private Object[] args;
   
   private String   message;
   
   private Object[] additional_infos;
   
   private Object   return_value;
   
   private Long     duration;
   
   public LogEntry(String initiator, String action, Object[] args, Object[] additionalInfos) {
      this.initiator = initiator;
      this.action = action;
      this.args = args;
      this.additional_infos = additionalInfos;
   }
   
   public LogEntry(String initiator, String action, Object return_value, Long duration, Object[] additional_infos) {
      this.initiator = initiator;
      this.action = action;
      this.return_value = return_value;
      this.duration = duration;
      this.additional_infos = additional_infos;
   }
   
   public LogEntry(String initiator, String action, Object[] args, String message,
                   Object[] additional_infos) {
      super();
      this.initiator = initiator;
      this.action = action;
      this.args = args;
      this.message = message;
      this.additional_infos = additional_infos;
   }
   
   public String getInitiator() {
      return initiator;
   }
   
   public void setInitiator(String initiator) {
      this.initiator = initiator;
   }
   
   public String getAction() {
      return action;
   }
   
   public void setAction(String action) {
      this.action = action;
   }
   
   public Object[] getArgs() {
      return args;
   }
   
   public void setArgs(Object[] args) {
      this.args = args;
   }
   
   public Object[] getAdditional_infos() {
      return additional_infos;
   }
   
   public void setAdditional_infos(Object[] additional_infos) {
      this.additional_infos = additional_infos;
   }
   
   public Object getReturn_value() {
      return return_value;
   }
   
   public void setReturn_value(Object return_value) {
      this.return_value = return_value;
   }
   
   public Long getDuration() {
      return duration;
   }
   
   public void setDuration(Long duration) {
      this.duration = duration;
   }
   
   public String getMessage() {
      return message;
   }
   
   public void setMessage(String message) {
      this.message = message;
   }
   
}

package com.hk.meditechadmin.ModelClass;

public class Health {
    private String deasesName, reportType, labResult, comment, examineDate,condition,healthId;

    public Health() {
    }

    public Health(String deasesName, String reportType, String labResult, String comment, String examineDate,String condition,String healthId) {
        this.deasesName = deasesName;
        this.reportType = reportType;
        this.labResult = labResult;
        this.comment = comment;
        this.examineDate = examineDate;
        this.condition = condition;
        this.healthId = healthId;
    }

    public String getDeasesName() {
        return deasesName;
    }

    public void setDeasesName(String deasesName) {
        this.deasesName = deasesName;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getLabResult() {
        return labResult;
    }

    public void setLabResult(String labResult) {
        this.labResult = labResult;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getExamineDate() {
        return examineDate;
    }

    public void setExamineDate(String examineDate) {
        this.examineDate = examineDate;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }
}

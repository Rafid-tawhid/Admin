package com.hk.meditechadmin.ModelClass;

public class Document {
    private String reportImage,reportType,generateDate,documentId;

    public Document() {
    }

    public Document(String reportImage, String reportType, String generateDate, String documentId) {
        this.reportImage = reportImage;
        this.reportType = reportType;
        this.generateDate = generateDate;
        this.documentId = documentId;

    }

    public String getReportImage() {
        return reportImage;
    }

    public void setReportImage(String reportImage) {
        this.reportImage = reportImage;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getGenerateDate() {
        return generateDate;
    }

    public void setGenerateDate(String generateDate) {
        this.generateDate = generateDate;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}

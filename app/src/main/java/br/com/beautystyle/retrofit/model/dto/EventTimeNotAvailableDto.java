package br.com.beautystyle.retrofit.model.dto;

import java.time.LocalTime;

public class EventTimeNotAvailableDto {

    private String statusCode;
    private String message;
    private LocalTime data;

    public EventTimeNotAvailableDto() {
    }

    public EventTimeNotAvailableDto(String error) {
        this.message = error;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalTime getData() {
        return data;
    }

    public void setData(LocalTime endTime) {
        this.data = endTime;
    }
}

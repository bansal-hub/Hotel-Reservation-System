package com.hotel.exception;

public class RoomNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public RoomNotFoundException(String message) { super(message); }
    public RoomNotFoundException(String message, Throwable cause) { super(message, cause); }
}

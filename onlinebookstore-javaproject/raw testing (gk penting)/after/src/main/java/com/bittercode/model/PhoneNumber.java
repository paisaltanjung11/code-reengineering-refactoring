package com.bittercode.model;

public class PhoneNumber {
    private final String value;
    
    public PhoneNumber(String phone) {
        if (!isValid(phone)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.value = phone;
    }
    
    private boolean isValid(String phone) {
        return phone != null && phone.matches("\\d+") && phone.length() >= 10;
    }
    
    public String getValue() {
        return value;
    }
    
    public long toLong() {
        return Long.parseLong(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PhoneNumber other = (PhoneNumber) obj;
        return value.equals(other.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
} 
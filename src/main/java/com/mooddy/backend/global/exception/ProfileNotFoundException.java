package com.mooddy.backend.global.exception;

public class ProfileNotFoundException extends RuntimeException {
  public ProfileNotFoundException(String email) {
    super("Profile not found with email: " + email);
  }
}
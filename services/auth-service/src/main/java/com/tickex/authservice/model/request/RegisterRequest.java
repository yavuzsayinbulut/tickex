package com.tickex.authservice.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @Email
    @NotBlank(message = "Email boş olamaz")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 8, max = 128, message = "Şifre en az 8 karakter olmalı")
    private String password;

    @NotBlank(message = "Ad boş olamaz")
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String middleName;

    @NotBlank(message = "Soyad boş olamaz")
    @Size(max = 100)
    private String lastName;

    // Kimlik numarası (TCKN vs.) - DB'ye hashlenmiş gidecek
    @Size(max = 32, message = "Kimlik numarası en fazla 32 karakter olmalı")
    private String identityNumber;

    @Size(max = 8, message = "Ülke kodu en fazla 8 karakter olmalı (örn. +90)")
    private String phoneCountryCode;

    @Size(max = 32, message = "Telefon numarası en fazla 32 karakter olmalı")
    private String phoneNumber;

    @Past(message = "Doğum tarihi geçmiş bir tarih olmalıdır")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Size(max = 3, message = "Uyruk alanı ISO3 formatında olmalıdır (örn. TUR)")
    private String nationality;

    @Size(max = 20, message = "Cinsiyet alanı en fazla 20 karakter olmalıdır")
    private String gender;
}

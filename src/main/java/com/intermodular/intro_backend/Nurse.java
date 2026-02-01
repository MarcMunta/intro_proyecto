package com.intermodular.intro_backend;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "nurses")
public class Nurse {

    @Id
    @Column(name = "nurse_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @JsonProperty("last_name")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Lob 
    @Column(name = "profile_picture", columnDefinition="LONGBLOB")
    @JsonProperty("profile_picture")
    private byte[] profilePicture;

    public Nurse() {}

    public Nurse(Integer id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @JsonProperty("id") 
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    // Compat: getters/setters legacy name <-> firstName
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getName() { return firstName; }
    public void setName(String name) { this.firstName = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public byte[] getProfilePicture() { return profilePicture; }
    public void setProfilePicture(byte[] profilePicture) { this.profilePicture = profilePicture; }

    @Override
    public String toString() {
        return "Nurse [(" + id + ") - name=" + firstName + ", lastName=" + lastName + ", email=" + email + ", password=" + password + "]";
    }
}
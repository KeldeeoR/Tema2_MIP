package org.example;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // --- MODIFICARE CHEIE PENTRU BAREM (Cascade) ---
    // mappedBy = "ospatar" se referă la câmpul 'ospatar' din clasa Comanda.
    // cascade = CascadeType.ALL înseamnă că dacă ștergem User-ul, se șterg și comenzile.
    @OneToMany(mappedBy = "ospatar", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Comanda> comenzi = new ArrayList<>();

    public User() {}

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getteri si Setteri
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    // Nu e nevoie de setter explicit pentru listă, Hibernate o gestionează
    public List<Comanda> getComenzi() { return comenzi; }

    public void setUsername(String username) { this.username = username; }
}
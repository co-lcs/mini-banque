package io.github.co_lcs.mini_banque.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Entity
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 34) // 27 -> Longueur un IBAN en France, 34 dans le monde.
    private String iban;

    @Column(nullable = false)
    private String titulaire;

    @Column(scale = 2, nullable = false) // On veut afficher les centimes, pas au-delà
    private BigDecimal solde;

    @Column(nullable = false)
    private LocalDate dateOuverture;

    @Column(nullable = false)
    private Currency devise;

    protected Compte(){
    }

    public Compte(String iban, String titulaire, BigDecimal solde, LocalDate dateOuverture, Currency devise ) {
        this.iban = iban;
        this.titulaire = titulaire;
        this.solde = solde;
        this.dateOuverture = dateOuverture;
        this.devise = devise;
    }

    public Long getId() {
        return this.id;
    }

    public String getIban() {
        return this.iban;
    }

    public String getTitulaire() {
        return this.titulaire;
    }

    public BigDecimal getSolde() {
        return this.solde;
    }

    public LocalDate getDateOuverture() {
        return this.dateOuverture;
    }

    public Currency getDevise() {
        return this.devise;
    }

    public void setDevise(Currency devise) {
        this.devise = devise;
    }
}

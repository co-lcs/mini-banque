package io.github.co_lcs.mini_banque.repository;

import io.github.co_lcs.mini_banque.model.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRepository extends JpaRepository<Compte, Long> {
}

package io.github.co_lcs.mini_banque.controller;

import io.github.co_lcs.mini_banque.dto.CompteResponse;
import io.github.co_lcs.mini_banque.dto.CompteRequest;
import io.github.co_lcs.mini_banque.model.Compte;
import io.github.co_lcs.mini_banque.service.CompteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comptes")
public class CompteController {
    private final CompteService compteService;

    public CompteController(CompteService compteService){
        this.compteService = compteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompteResponse> getCompteById(@PathVariable Long id) {
        Compte compte = compteService.getCompteById(id);
        return ResponseEntity.ok(CompteResponse.fromEntity(compte));
    }

    @GetMapping
    public ResponseEntity<List<CompteResponse>> getComptes() {
        List<CompteResponse> comptes = compteService.getComptes().stream().map(CompteResponse::fromEntity).toList();
        return ResponseEntity.ok(comptes);
    }

    @PostMapping
    public ResponseEntity<CompteResponse> newCompte(@RequestBody CompteRequest request) {
        Compte savedCompte = compteService.saveCompte(
                request.iban(),
                request.titulaire(),
                request.solde(),
                request.devise()
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCompte.getId())
                .toUri();

        return ResponseEntity.created(location).body(CompteResponse.fromEntity(savedCompte));
    }

}

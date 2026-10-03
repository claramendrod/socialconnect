package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record ProdutoResponseDTO(
        @Schema(example = "1") Long idProduto,
        @Schema(example = "Arroz 5kg") String nome,
        @Schema(example = "ALIMENTO") CategoriaProduto categoria,
        @Schema(example = "3") Integer estoqueAtual,
        @Schema(example = "10") Integer estoqueMinimo,
        @Schema(example = "unidade") String unidadeMedida,
        @Schema(example = "2026-09-18") LocalDate dataCadastro,
        @Schema(description = "Indica estoque atual menor que o mínimo", example = "true")
        boolean estoqueBaixo
) {}

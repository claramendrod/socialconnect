package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.dto.*;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class ProdutoControllerIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void banco(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired TestRestTemplate rest;
    @Autowired ProdutoRepository repository;

    @BeforeEach
    void limpar() {
        repository.deleteAll();
    }

    private ProdutoRequestDTO dados(int estoque) {
        return new ProdutoRequestDTO("Arroz 5kg", CategoriaProduto.ALIMENTO, estoque, 10, "unidade");
    }

    @Test
    void deveCriarProdutoQuandoDadosValidos() {
        // Arrange
        ProdutoRequestDTO dto = dados(3);
        // Act
        ResponseEntity<ProdutoResponseDTO> resposta = rest.postForEntity("/api/v1/produtos", dto, ProdutoResponseDTO.class);
        // Assert
        assertEquals(201, resposta.getStatusCode().value());
        assertNotNull(resposta.getHeaders().getLocation());
        assertNotNull(resposta.getBody());
        assertTrue(resposta.getBody().estoqueBaixo());
    }

    @Test
    void deveRetornar409QuandoNomeDuplicado() {
        // Arrange
        rest.postForEntity("/api/v1/produtos", dados(3), String.class);
        // Act
        ResponseEntity<String> resposta = rest.postForEntity("/api/v1/produtos", dados(3), String.class);
        // Assert
        assertEquals(409, resposta.getStatusCode().value());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, resposta.getHeaders().getContentType());
    }

    @Test
    void deveRetornar422QuandoEstoqueNegativo() {
        // Arrange
        ProdutoRequestDTO dto = dados(-1);
        // Act
        ResponseEntity<String> resposta = rest.postForEntity("/api/v1/produtos", dto, String.class);
        // Assert
        assertEquals(422, resposta.getStatusCode().value());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON, resposta.getHeaders().getContentType());
        assertFalse(resposta.getBody().contains("{produto."));
    }
}

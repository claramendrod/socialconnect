## Declaração de Uso de IA (A1)

### Ferramentas utilizadas:
- [X] ChatGPT / Claude / Gemini
- [ ] Copilot / Codeium
- [ ] Nenhuma

### Como utilizei:
- estou te encaminhando um arquivo e preciso que voce me ajude a entender o que preciso implementar no meu projeto
- me fale a ordem que eue devo começar a fazer a implementação do modulo produtos, tipo controller service, dto
- como posso criar a migration V4 para a tabela produtos? Preciso colocar nome unico e restricoes para impedir estoque negativo
- como eu coloco endpoint no swagger
- no print que eue enviei deu erro na hora de rodar o projeto, me ajude a arrumar
- me dê exemplos para preencher no try out
- me explique os codigos de erro
- Como ajustar o tratamento de validação
- não cadastrou no banco, me ajude
- Como verificar se o Docker está iniciado
- por favor verifique se o projeto atende todos os requsitos da atividade
- me ajude a implementar
- como eu crio uma brand no git hub, ja criei o repositorio

### O que eu entendo 100%:
- como criar as classes e colocar os inputs
- o objetivo do modulo de Produtos: cadastrar, consultar, atualizar e excluir produtos.
- como funcionam o get, post, delete e outros
- A regra de que o estoque atual não pode ser negativo.
- Que um produto está com estoque baixo quando o estoque atual é menor que o estoque mínimo.
- Que não pode haver dois produtos com o mesmo nome.

### O que precisei estudar mais:
- A organização das camadas Controller, Service, Repository, DTO e Model.
- A criação da migration V4 com Flyway e das restrições no banco.
- Como os dados enviados à API são salvos no PostgreSQL.
- A documentação dos endpoints no Swagger.
- Os códigos HTTP e quando retornar 400, 404, 409 e 422.
- As validações dos campos e o tratamento de erros com Problem Details.
- Como identificar erros ao iniciar o projeto ou cadastrar um produto.
- o funcionamento do Docker e como verificar se ele está iniciado.
- como criar uma brand e colocar os arquivos no github
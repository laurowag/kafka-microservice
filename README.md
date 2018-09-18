Projeto de testes com Wildfly Swarm, Kafka, Banco de dados e Docker.

O projeto expoe uma API Rest para gravar um objeto Cliente. Após gravar esse objeto, há um JaxRS Response Filter (KafkaResponseFilter) que irá registrar em um tópico do Kafka o objeto persistido convertido para Avro.

Também no projeto, ao iniciar, um consumer é iniciado escutando o mesmo tópico (Notificador).

Ao fazer um POST em "http://localhost:8080/api/servico" com o json {"id":2, "nome": "Jorge"}, você terá um novo registro criado no banco de dados e uma mensagem recebida no consumer.

Para rodar o projeto, será necessário configurar uma conexão com banco de dados.

Ajustar o arquivo project-defaults.yml

No projeto está disponível o script create.sql com o DDL da estrutura do banco de dados.

Para rodar o projeto, usar os seguintes alvos do Maven:

mvn wildfly-swarm:run (para rodar o wildfly swarm via maven)

mvn package -Pdocker (para gerar o docker localmente)

As configurações do Kafka estão nas próprias classes que fazem as conexões (Notificador e KafkaResponseFilter)
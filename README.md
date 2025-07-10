# 🧩 Sistema MOSAIC – Inspeção Visual Automatizada

O **MOSAIC** é uma aplicação desktop desenvolvida em **Java (Swing)** para automatizar o processo de inspeção visual de peças e componentes. A ferramenta monitora uma área específica da tela, detecta alterações (mudança dos pixels na tela), captura uma sequência de imagens e as mescla automaticamente, criando um mosaico visual completo da superfície inspecionada.

O sistema foi desenvolvido com foco em **rastreabilidade**, **segurança** e **usabilidade**, incluindo controle de acesso por usuários, autenticação segura, e histórico completo das inspeções realizadas.
### Tela de Principal

> ![Image](https://github.com/user-attachments/assets/5df46226-3256-4d98-af2b-61d5aa69f3a2)

---

## ✨ Funcionalidades Principais

- **🎯 Captura Inteligente:** Monitora uma área do ecrã e inicia a captura automaticamente ao detectar mudanças visuais.
- **🧵 Mesclagem de Imagens (Stitching):** Combina diversas capturas em uma imagem panorâmica de alta resolução.
- **📋 Gestão de Inspeções:** Cada inspeção armazena nome da peça, descrição, data e imagens.
- **👤 Rastreabilidade de Operador:** Inspeções são vinculadas automaticamente ao usuário logado.
- **💾 Armazenamento Flexível:** Suporte a armazenamento local e na nuvem (via [Cloudinary](https://cloudinary.com/)).
- **🔐 Autenticação Segura:**
  - Acesso com e-mail e senha.
  - Hashing de senhas com `jBCrypt`.
  - Recuperação de senha via e-mail com token.
- **🛠️ Painel de Administração:**
  - O primeiro usuário é definido como Administrador.
  - Gerencie usuários: promover/rebaixar, resetar senha e excluir contas.
- **📁 Histórico de Inspeções:** Visualização e filtro de inspeções anteriores com acesso às imagens geradas.

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia         | Uso |
|--------------------|-----|
| **Java (JDK 11+)** | Lógica da aplicação |
| **Java Swing**     | Interface gráfica |
| **FlatLaf**        | Tema claro e moderno |
| **SQLite**         | Banco de dados leve e portátil |
| **jBCrypt**        | Hash seguro de senhas |
| **Jakarta Mail**   | Envio de e-mails (recuperação de senha) |
| **Apache Maven**   | Gerenciamento de dependências e build |

---

## 🚀 Como Configurar e Executar

### 1. Pré-Requisitos

- **JDK 11 ou superior**
- **Apache NetBeans IDE 12+**
- Conta Gmail com **verificação em duas etapas** e **senha de app**

---


Abra o projeto no NetBeans. Ele detectará automaticamente como projeto Maven.

### 2. Dependências do Maven
O pom.xml já possui todas as dependências necessárias. Caso precise recriar, use:

<dependencies>
    <!-- SQLite -->
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.46.0.0</version>
    </dependency>

    <!-- jBCrypt -->
    <dependency>
        <groupId>org.mindrot</groupId>
        <artifactId>jbcrypt</artifactId>
        <version>0.4</version>
    </dependency>

    <!-- Jakarta Mail -->
    <dependency>
        <groupId>org.eclipse.angus</groupId>
        <artifactId>angus-mail</artifactId>
        <version>2.0.3</version>
    </dependency>

    <!-- Activation -->
    <dependency>
        <groupId>org.eclipse.angus</groupId>
        <artifactId>angus-activation</artifactId>
        <version>2.0.1</version>
    </dependency>

    <!-- FlatLaf (Tema Visual) -->
    <dependency>
        <groupId>com.formdev</groupId>
        <artifactId>flatlaf</artifactId>
        <version>3.4.1</version>
    </dependency>
</dependencies>
Após salvar, clique com o botão direito no projeto e selecione "Limpar e Construir".

---

3. Configurar Envio de E-mail
   
>Crie o arquivo mail.properties na raiz do projeto:

>NetBeans > "Ficheiros" > Novo > Outro > Ficheiro de Propriedades

```
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.auth=true
mail.smtp.starttls.enable=true

mail.sender.username=seu-email-aqui@gmail.com
mail.sender.password=sua-senha-de-app-de-16-letras-aqui
```
💡 **Importante:** use uma senha de app do Google, não sua senha normal.

🧪 Como Usar
1. Primeira Execução

- Ao abrir o sistema pela primeira vez:

- A base de dados estará vazia.

- Na tela de login, clique em "Criar Conta".

- Preencha seus dados.

- O primeiro usuário será automaticamente definido como Administrador.

2. Inspeção Visual

- Faça login com seu e-mail e senha.

- Vá até a aba "Metadados da Inspeção".

- Informe o nome da peça e uma descrição.

- Clique em "Iniciar Nova Inspeção".

- O sistema irá monitorar a tela e capturar automaticamente as imagens, gerando um mosaico.

📌 **Observações**
A aplicação é modular e pode ser estendida para integração com sistemas MES, ERP ou APIs externas.

📄 Licença
Este projeto está licenciado sob a MIT License. Veja o arquivo LICENSE para mais informações.

👨‍💻 Autor
**João Fontes**
> github.com/JoaoFontes-debug

### 2. Clonar o Repositório

```bash
git clone https://github.com/JoaoFontes-debug/Mosaic.git

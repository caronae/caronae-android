# Caronaê - Andorid

Aplicativo para Android do Caronaê.

**Requisitos:**

* Android Studio 1.5+
* Android Min SDK 15

## Instalação

Para rodar o projeto, clone este repositório e abra no Android Studio.
O projeto esta configurado com Gradle.

Ao concluir, abra o projeto pelo arquivo **Caronae.xcworkspace**.

## Firebase Cloud Messaging

Este projeto faz uso da plataforma [Firebase](https://firebase.google.com/) para receber notificações push. Para fazer uso desse recurso é necessário gerar e adicionar o arquivo `GoogleService-Info.plist` dentro do diretório deste projeto.

Consulte a [documentação](https://firebase.google.com/docs/ios/setup) para saber mais informações. Um exemplo do arquivo pode ser encontrado em: `Caronae/Supporting Files/GoogleService-Info.plist.example`.

Os arquivos são sincronizados através de um repositório privado no GitHub e criptografados com uma senha.

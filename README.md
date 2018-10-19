# Caronaê - Android

[![CircleCI](https://circleci.com/gh/caronae/caronae-android.svg?style=svg)](https://circleci.com/gh/caronae/caronae-android)

Aplicativo para Android do Caronaê.

**Requisitos:**

* Android Studio 3.2.1
* Android 4.0.3+

## Instalação

Para executar o projeto, clone este repositório e abra no Android Studio. O projeto está configurado com o Gradle padrão, não são necessários scripts adicionais.

Ao concluir, abra o projeto pelo diretório `caronae-android`.

Após abrir o projeto no Android Studio, será necessário navegar até `File → Settings → Build, Execution, Deployment → Instant Run` e desmarcar a primeira opção `Enable Instant Run to hot swap code/resource changes on deploy (default enabled)`.

## Fastlane



Este projeto está configurado com o [fastlane](http://fastlane.tools). Consulte a [documentação](/fastlane) da pasta fastlane para ver as ações disponíveis.



O Fastlane é instalado através do comando `bundle install`.

## Firebase Cloud Messaging

Este projeto faz uso da plataforma [Firebase](https://firebase.google.com/) para receber notificações push. Para fazer uso desse recurso é necessário gerar e adicionar o arquivo `google-services` dentro do diretório deste projeto.

Consulte a [documentação](https://firebase.google.com/docs/android) para saber mais informações. Um exemplo do arquivo pode ser encontrado em: `caronae-android\app\google-services.example.json`.
